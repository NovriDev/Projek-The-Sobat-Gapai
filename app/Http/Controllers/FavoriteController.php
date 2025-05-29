<?php

namespace App\Http\Controllers;

use App\Models\Favorite;
use App\Models\Notification;
use App\Models\Tugas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class FavoriteController extends Controller
{
    public function getFavoriteStatus($tugasId)
{
    $user = auth()->user();
    $tugas = Tugas::findOrFail($tugasId);

    // Cek apakah tugas tersebut ada di favorit pengguna
    $isFavorite = $user->favorites()->where('tugas_id', $tugasId)->exists();

    return response()->json([
        'isFavorite' => $isFavorite,
    ]);
}

public function store(Request $request)
{
    // Validasi input
    $validated = $request->validate([
        'tugas_id' => 'required|exists:tugas,id',
    ]);

    // Cek apakah tugas sudah ada di favorit pengguna
    $favorite = Favorite::firstOrCreate([
        'tugas_id' => $validated['tugas_id'],
        'user_id' => Auth::id(),
    ]);

    // Update jumlah like (increment) pada tabel tugas
    $tugas = Tugas::findOrFail($validated['tugas_id']);
    $tugas->increment('jml_likes');

    // Ambil informasi tugas yang difavoritkan
    $user = Auth::user();
    $tugasUser = $tugas->user;  // Pengguna yang membuat tugas ini

    // Buat notifikasi untuk pengguna yang memiliki tugas
    Notification::create([
        'user_id' => $tugasUser->id, // Pengguna yang menerima notifikasi
        'type' => 'like',             // Tipe notifikasi
        'source_user_id' => $user->id, // Pengguna yang memberi like
        'task_id' => $tugas->id,      // ID tugas yang diberi like
        'is_read' => false,           // Status notifikasi belum dibaca
    ]);

    // Mengembalikan respons
    return response()->json(['message' => 'Tugas ditambahkan ke favorit dan notifikasi telah dikirim']);
}

    // Menghapus favorit
    public function destroy(Request $request)
    {
        $validated = $request->validate([
            'tugas_id' => 'required|exists:tugas,id',
        ]);

        $favorite = Favorite::where('tugas_id', $validated['tugas_id'])
            ->where('user_id', Auth::id())
            ->first();

        if (!$favorite) {
            return response()->json(['message' => 'Favorit tidak ditemukan'], 404);
        }

        $favorite->delete();
        return response()->json(['message' => 'Tugas dihapus dari favorit']);
    }
}
