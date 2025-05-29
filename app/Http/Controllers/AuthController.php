<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use App\Models\TransactionHistory;
use App\Models\User;
use App\Notifications\FollowedUserNotif;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    public function updateUser(Request $request)
    {
        $user = Auth::user();

        $validator = Validator::make($request->all(), [
            'name' => 'sometimes|string|max:255',
            'email' => 'sometimes|email|max:255|unique:users,email,' . $user->id,
            'password' => 'sometimes|string|min:8',
            'profileBio' => 'sometimes|string|max:1000',
            'profilePicture' => 'sometimes|image|mimes:jpeg,png,jpg,gif|max:10240'
        ]);

        if ($request->has('name')) {
            $user->name = $request->name;
        }

        if ($request->has('email')) {
            $user->email = $request->email;
        }

        if ($request->has('password')) {
            $user->password = Hash::make($request->password);
        }

        if ($request->has('profileBio')) {
            $user->profileBio = $request->profileBio;
        }

        //  Update gambar profil (opsional)
        if ($request->hasFile('profilePicture')) {
            $image = $request->file('profilePicture');
            $imagePath = $image->store('profile_pictures', 'public');
            $user->profilePicture = $imagePath; // Simpan path ke database
        }

        $user->save();

        return response()->json(['message' => 'Profile updated successfully', 'user' => $user], 200);
    }

    public function register(Request $request)
    {
        // Validation rules
        $validator = Validator::make($request->all(), [
            'email' => 'required|email|unique:users,email',
            'name' => 'required|string|max:255',
            'password' => 'required|string|min:8|confirmed',
        ]);

        // Check if validation fails
        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 422);
        }

        // Create user
        $user = User::create([
            'email' => $request->email,
            'name' => $request->name,
            'password' => Hash::make($request->password),
        ]);

        // Optionally, authenticate the user after registration
        Auth::login($user);

        return response()->json(['message' => 'Registrasi Berhasil!'], 201);
    }

    public function login(Request $request)
{
    $request->validate([
        'email' => 'required|email',
        'password' => 'required',
    ]);

    $user = User::where('email', $request->email)->first();

    if (!$user || !Hash::check($request->password, $user->password)) {
        return response()->json(['message' => 'Invalid credentials'], 401);
    }

    // Periksa status is_banned
    if ($user->is_banned) {
        return response()->json([
            'message' => 'Akun Anda telah dibanned. Silakan hubungi admin.',
        ], 403); // 403 Forbidden
    }

    // Hapus token lama (opsional)
    $user->tokens()->delete();

    // Buat token baru
    $token = $user->createToken('auth_token')->plainTextToken;

    return response()->json([
        'message' => 'Login successful',
        'access_token' => $token,
        'token_type' => 'Bearer',
        'user' => [
            'name' => $user->name,
            'email' => $user->email,
        ],
    ]);
}
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Logout successful']);
    }

    // Mengikuti seseorang
    public function follow($userId)
{
    $user = auth()->user(); // Mendapatkan pengguna yang sedang login
    $followedUser = User::findOrFail($userId); // Mencari pengguna yang akan di-follow

    // Cek jika sudah mengikuti
    if ($user->followings->contains($followedUser)) {
        return response()->json([
            'isFollowed' => true
        ], 400); // Mengembalikan status isFollowed true jika sudah mengikuti
    }

    // Menambahkan pengguna yang diikuti (menambah data ke tabel followings)
    $user->followings()->attach($followedUser);

    // Membuat notifikasi untuk pengguna yang di-follow
    Notification::create([
        'user_id' => $followedUser->id,  // Penerima notifikasi (user yang di-follow)
        'type' => 'follow',             // Tipe notifikasi, misalnya "follow"
        'source_user_id' => $user->id,    // Pengirim notifikasi (user yang mengikuti)
        'is_read' => false,             // Notifikasi baru belum dibaca
        // Jika ada field task_id, bisa dibiarkan null atau disesuaikan
    ]);

    return response()->json([
        'isFollowed' => true
    ]);
}

    // Membatalkan mengikuti seseorang
    public function unfollow($userId)
{
    $user = auth()->user(); // Mendapatkan pengguna yang sedang login
    $followedUser = User::findOrFail($userId); // Mencari pengguna yang akan di-unfollow

    // Cek jika tidak mengikuti
    if (!$user->followings->contains($followedUser)) {
        return response()->json([
            'isFollowed' => false
        ], 400); // Mengembalikan status isFollowed false jika tidak mengikuti
    }

    // Membatalkan mengikuti pengguna (menghapus data dari tabel followings)
    $user->followings()->detach($followedUser);

    return response()->json([
        'isFollowed' => false
    ]);
}
public function notif()
    {
        $user = Auth::user();  // Mendapatkan pengguna yang sedang login
        $notifications = $user->notifications; // Ambil semua notifikasi

        return response()->json($notifications);
    }

public function suggestedFriends()
{
    $user = auth()->user(); // Dapatkan pengguna yang sedang login

    if (!$user) {
        return response()->json(['message' => 'Silakan login terlebih dahulu.'], 401);
    }

    // Query teman yang belum diikuti
    $suggestedFriends = User::select('users.*')
    ->addSelect(['isFollowed' => function ($query) use ($user) {
        $query->selectRaw('COUNT(*) > 0')
              ->from('followings')
              ->whereColumn('followings.followed_id', 'users.id')
              ->where('followings.user_id', '=', $user->id);
    }])
    ->where('users.id', '!=', $user->id)
    ->get();

    // Konversi angka menjadi boolean
    $suggestedFriends->transform(function ($friend) {
        $friend->isFollowed = (bool) $friend->isFollowed; // Konversi ke boolean
        return $friend;
    });

return response()->json($suggestedFriends);

}

public function getProfile(Request $request)
{
    $user = $request->user(); // Ambil data pengguna yang sedang login
    return response()->json([
        'user' => $user, // Kembalikan data pengguna
    ]);
}
public function getOtherProfile(Request $request, $userId)
{
    // Ambil data pengguna berdasarkan userId
    $user = User::find($userId);
    
    if (!$user) {
        return response()->json([
            'message' => 'User not found'
        ], 404);
    }
    
    // Hitung jumlah pengikut, jumlah yang diikuti, dan jumlah tugas
    $followersCount = $user->followers()->count();
    $followingCount = $user->followings()->count();
    $tugasCount = $user->tugas()->count();
    
    // Kembalikan data pengguna dan count-nya sebagai JSON
    return response()->json([
        'user' => $user,
        'followers_count' => $followersCount,
        'following_count' => $followingCount,
        'tugas_count' => $tugasCount,
    ]);
}


public function getFollowers(Request $request)
    {
        // Mendapatkan pengguna yang sedang login
        $user = $request->user(); 

        // Ambil semua followers yang mengikuti pengguna yang sedang login, kecuali diri mereka sendiri
        $followers = User::whereIn('id', function ($query) use ($user) {
            // Ambil followers berdasarkan relasi yang ada di tabel 'followings'
            $query->select('user_id')
                  ->from('followings')  // Tabel relasi followings
                  ->where('followed_id', $user->id);  // Mengambil user_id yang mengikuti user yang login
        })
        ->where('id', '!=', $user->id) // Pastikan tidak memasukkan akun pengguna yang sedang login
        ->get();

        return response()->json([
            'followers' => $followers
        ]);
    }

    public function updateUserLevel(Request $request, $userId)
    {
        // Validasi input
        $validator = Validator::make($request->all(), [
            'level' => 'required|integer|min:0', // Level harus berupa angka dan tidak negatif
        ]);
    
        // Jika validasi gagal
        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid input',
                'errors' => $validator->errors(),
            ], 400); // Bad Request
        }
    
        // Ambil data pengguna
        $user = DB::table('users')->where('id', $userId)->first();
    
        // Jika pengguna tidak ditemukan
        if (!$user) {
            return response()->json([
                'status' => 'error',
                'message' => 'User not found',
            ], 404); // Not Found
        }
    
        // Ambil level saat ini dari database
        $currentLevel = $user->level;
    
        // Ambil level baru dari request
        $levelToAdd = $request->input('level');
    
        // Hitung level baru
        $newLevel = $currentLevel + $levelToAdd;
    
        // Update level pengguna di database
        try {
            $affectedRows = DB::table('users')
                ->where('id', $userId)
                ->update(['level' => $newLevel]);
    
            // Jika berhasil diupdate
            return response()->json([
                'status' => 'success',
                'message' => 'User level updated successfully',
                'data' => [
                    'userId' => $userId,
                    'currentLevel' => $currentLevel,
                    'addedLevel' => $levelToAdd,
                    'newLevel' => $newLevel,
                ],
            ], 200); // OK
        } catch (\Exception $e) {
            // Jika terjadi error
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to update user level',
                'error' => $e->getMessage(),
            ], 500); // Internal Server Error
        }
    }

public function withdraw(Request $request)
{
    // Validasi input
    $request->validate([
        'amount' => 'required|numeric|min:1', // Pastikan jumlah penarikan valid
    ]);

    $user = auth()->user();
    $amount = $request->input('amount');

    // Cek apakah saldo mencukupi
    if ($user->balance < $amount) {
        return response()->json(['message' => 'Insufficient balance'], 400);
    }

    // Mulai transaksi untuk memastikan integritas data
    DB::beginTransaction();

    try {
        // Kurangi saldo pengguna
        $user->balance -= $amount;
        $user->save();

        // Simpan riwayat transaksi penarikan
        TransactionHistory::create([
            'user_id' => $user->id,
            'type' => 'withdrawal',
            'amount' => $amount,
            'balance_after' => $user->balance,
        ]);

        // Commit transaksi
        DB::commit();

        return response()->json([
            'message' => 'Withdrawal successful',
            'balance' => $user->balance
        ], 200);
    } catch (\Exception $e) {
        // Rollback transaksi jika terjadi error
        DB::rollback();

        return response()->json(['message' => 'Transaction failed', 'error' => $e->getMessage()], 500);
    }
}

}