<?php

namespace App\Http\Controllers;

use App\Jobs\UnbanUser;
use App\Models\Answer;
use App\Models\Report;
use Illuminate\Support\Facades\Auth;
use App\Models\Tugas;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;

class AdminController extends Controller
{
    // Method untuk menampilkan semua data di satu halaman
    public function dashboard()
    {
        $users = User::withCount('tugas')->get();
        // Hitung jumlah semua tugas
        $totalTugas = Tugas::count();

        // Ambil data tugas terbaru (opsional)
        $tugas = Tugas::with('user')
            ->withCount('favorites')
            ->latest()
            ->take(5) // Ambil 5 tugas terbaru
            ->get();

        // Hitung jumlah semua akun
        $totalAccounts = User::count();

        // Hitung jumlah semua laporan
        $totalReports = Report::count();

        // Ambil data laporan terbaru (opsional)
        $reports = Report::latest()
            ->take(5) // Ambil 5 laporan terbaru
            ->get();

        // Kirim semua data ke view
        return view('admin.dashboard.index', compact(
            'totalTugas', 'tugas', 
            'totalAccounts', 
            'totalReports', 'reports', 'users'
        ));
    }

    // Method untuk ban/unban user
    // public function banUser(Request $request, $id)
    // {
    //     // Cari user berdasarkan ID
    //     $user = User::findOrFail($id);

    //     // Toggle status is_banned
    //     $user->is_banned = !$user->is_banned; // Jika true, jadi false, dan sebaliknya
    //     $user->save();

    //     // Berikan pesan sukses
    //     $message = $user->is_banned ? 'User berhasil dibanned.' : 'User berhasil di-unban.';
    //     return redirect()->back()->with('success', $message);
    // }

//     public function banUser(Request $request, $id)
// {
//     $user = User::findOrFail($id);

//     if ($user->is_banned) {
//         // Jika user sudah dibanned, lakukan unban manual
//         $user->is_banned = false;
//         Cache::forget('banned_until_' . $user->id); // Hapus cache waktu banned
//         $message = 'User berhasil di-unban.';
//     } else {
//         // Ban user dan atur unban otomatis setelah 30 hari
//         $user->is_banned = true;
//         $bannedUntil = now()->addSeconds(10); // Waktu unban 10 detik dari sekarang untuk demo
//         Cache::put('banned_until_' . $user->id, $bannedUntil, $bannedUntil);

//         $message = 'User berhasil dibanned selama 30 hari.';
//         \App\Jobs\UnbanUser::dispatch($user->id)->delay($bannedUntil);
//     }
//     $user->save();

//     return redirect()->back()->with('success', $message);
// }

public function banUser(Request $request, $id)
{
    $user = User::findOrFail($id);

    if ($user->is_banned) {
        // Jika user sudah dibanned, lakukan unban manual
        $user->is_banned = false;
        Cache::forget('banned_until_' . $user->id); // Hapus cache waktu banned
        $message = 'User berhasil di-unban.';
    } else {
        // Ban user dan atur unban otomatis setelah 30 hari
        $user->is_banned = true;
        $bannedUntil = now()->addDays(10); // Waktu unban 30 hari dari sekarang
        Cache::put('banned_until_' . $user->id, $bannedUntil, $bannedUntil);

        $message = 'User berhasil dibanned selama 30 hari.';
        UnbanUser::dispatch($user->id)->delay($bannedUntil);
    }
    $user->save();

    return redirect()->back()->with('success', $message);
}




    // Method untuk menghapus user
    public function deleteUser($id)
{
    $user = User::findOrFail($id);
    $user->delete();

    // Tambahkan session flash message
    return redirect()->route('admin.dashboard')->with('success', 'User berhasil dihapus.');
}

    public function showReports()
    {
        // Ambil semua data laporan dari database
       $reports = Report::with(['user', 'tugas', 'answer'])->get();

        // Kirim data laporan ke view
        return view('admin.dashboard.laporan', compact('reports'));
    }

    // Menampilkan daftar tugas
    public function kelolaTugas()
    {
        $tugas = Tugas::with('user')->get(); // Ambil semua tugas beserta relasi user
        return view('admin.dashboard.tugas', compact('tugas'));
    }


    // Menghapus tugas
    public function hapusTugas($id)
    {
        // Cari tugas berdasarkan ID
        $tugas = Tugas::findOrFail($id);

        // Hapus tugas
        $tugas->delete();

        return redirect()->route('admin.tugas')->with('success', 'Tugas berhasil dihapus.');
    }
    // Menampilkan daftar jawaban
    public function kelolaJawaban()
    {
        $answers = Answer::with(['user', 'tugas'])->get(); // Ambil semua jawaban beserta relasi user dan tugas
        return view('admin.dashboard.jawaban', compact('answers'));
    }

    // Menghapus jawaban
    public function hapusJawaban($id)
    {
        // Cari jawaban berdasarkan ID
        $answer = Answer::findOrFail($id);

        // Hapus jawaban
        $answer->delete();

        return redirect()->route('admin.jawaban')->with('success', 'Jawaban berhasil dihapus.');
    }
    

    // Menampilkan form login admin
    public function showLoginForm()
    {
        return view('admin.auth.login');
    }

    // Proses login admin
    public function login(Request $request)
    {
        $credentials = $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        if (Auth::guard('admin')->attempt($credentials)) {
            $request->session()->regenerate();
            return redirect()->route('admin.dashboard');
        }

        return back()->withErrors([
            'email' => 'Email atau password salah.',
        ]);
    }


    // Logout admin
    public function logout(Request $request)
    {
        Auth::logout(); // Menghapus session login

        $request->session()->invalidate(); // Menghapus session
        $request->session()->regenerateToken(); // Regenerate CSRF token

        return redirect()->route('admin.login'); // Arahkan ke halaman login
    }
}
