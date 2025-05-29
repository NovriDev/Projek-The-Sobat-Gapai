<?php

namespace App\Http\Controllers;

use App\Models\Tugas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;

class TugasController extends Controller
{
    // Menampilkan semua tugas
    public function index()
    {
        $tugas = Tugas::with('user')
            ->withCount('favorites') // Menambahkan jumlah favorites
            ->get()
            ->map(function ($tugas) {
                $tugas->image_tugas = asset('storage/' . $tugas->image_tugas); // Menambahkan URL lengkap
                $tugas->jml_likes = $tugas->favorites_count; // Menyisipkan jumlah likes
                unset($tugas->favorites_count); // Menghapus key favorites_count agar lebih bersih
                return $tugas;
            });
    
        return response()->json($tugas);
    }

    public function viewTugasUser($tugasId)
    {
        $tugas = Tugas::with('user')
            ->withCount('favorites')
            ->where('id', $tugasId)
            ->map(function ($tugas) {
                $tugas->image_tugas = asset('storage/' . $tugas->image_tugas); // Menambahkan URL lengkap
                $tugas->jml_likes = $tugas->favorites_count; // Menyisipkan jumlah likes
                unset($tugas->favorites_count); // Menghapus key favorites_count agar lebih bersih
                return $tugas;
            });
    
        return response()->json($tugas);
    }
    

//     public function viewTugasUser($tugasId)
// {
//     $tugas = Tugas::with('user')
//         ->withCount('favorites')
//         ->where('id', $tugasId)
//         ->first(); // Gunakan first() agar mendapatkan satu objek

//     if (!$tugas) {
//         return response()->json(['message' => 'Tugas tidak ditemukan'], 404);
//     }

//     $tugas->image_tugas = asset('storage/' . $tugas->image_tugas); // Tambahkan URL lengkap
//     $tugas->jml_likes = $tugas->favorites_count; // Menyisipkan jumlah likes
//     unset($tugas->favorites_count); // Menghapus key favorites_count agar lebih bersih

//     return response()->json($tugas);
// }

    
    // Membuat tugas baru
    public function store(Request $request)
    {
        $validated = $request->validate([
            'keterangan' => 'required|string|max:255',
            'mapel' => 'required|string|max:255',
            'deskripsi' => 'required|string',
            'image_tugas' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:10240',
        ]);

        // Upload gambar tugas jika ada
        $imagePath = null;
        if ($request->hasFile('image_tugas')) {
            $imagePath = $request->file('image_tugas')->store('image_asset', 'public');
        }

        $tugas = Tugas::create([
            'user_id' => Auth::id(),
            'keterangan' => $validated['keterangan'],
            'mapel' => $validated['mapel'],
            'deskripsi' => $validated['deskripsi'],
            'image_tugas' => $imagePath,
        ]);

        return response()->json(['message' => 'Tugas berhasil dibuat'], 201);
    }

    // Menampilkan detail tugas
    public function show($id)
    {
        $tugas = Tugas::with('user')->withCount('favorites')->findOrFail($id);
        return response()->json($tugas);
    }

    // Menghapus tugas
    public function destroy($id)
    {
        $tugas = Tugas::findOrFail($id);

        // Hanya pemilik tugas yang bisa menghapus
        if ($tugas->user_id != Auth::id()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $tugas->delete();
        return response()->json(['message' => 'Tugas berhasil dihapus']);
    }

    public function getUserTasks()
{
    // Mengambil semua tugas yang diupload oleh pengguna yang sedang login
    $tugas = Tugas::where('user_id', Auth::id()) // Filter berdasarkan ID pengguna yang sedang login
        ->with('user')
        ->withCount('favorites') // Menambahkan jumlah favorites
        ->get()
        ->map(function ($tugas) {
            // Menambahkan URL lengkap untuk gambar tugas
            $tugas->image_tugas = asset('storage/' . $tugas->image_tugas);
            // Menyisipkan jumlah likes
            $tugas->jml_likes = $tugas->favorites_count;
            unset($tugas->favorites_count); // Menghapus key favorites_count agar lebih bersih
            return $tugas;
        });

    // Mengembalikan response dalam bentuk JSON
    return response()->json($tugas);
}

public function getUserTasksWithId($userId)
{
    // Mengambil semua tugas yang diupload oleh pengguna yang sedang login
    $tugas = Tugas::where('user_id', $userId) // Filter berdasarkan ID pengguna yang sedang login
        ->with('user')
        // ->withCount('favorites') // Menambahkan jumlah favorites
        ->get()
        ->map(function ($tugas) {
            // Menambahkan URL lengkap untuk gambar tugas
            $tugas->image_tugas = asset('storage/' . $tugas->image_tugas);
            // Menyisipkan jumlah likes
            // $tugas->jml_likes = $tugas->favorites_count;
            // unset($tugas->favorites_count); // Menghapus key favorites_count agar lebih bersih
            return $tugas;
        });

    // Mengembalikan response dalam bentuk JSON
    return response()->json($tugas);
}

}
