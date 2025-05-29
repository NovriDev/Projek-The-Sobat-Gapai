<?php

namespace App\Http\Controllers;

use App\Models\Report;
use Illuminate\Http\Request;

class ReportController extends Controller
{
    public function reportAnswer(Request $request)
{
    // Validasi input
    $validated = $request->validate([
        'user_id' => 'required|exists:users,id', // Validasi bahwa user_id ada di tabel users
        'tugas_id' => 'required|exists:tugas,id', // Validasi bahwa user_id ada di tabel users
        'answer_id' => 'required|exists:answers,id', // Validasi bahwa answer_id ada di tabel answers
        'title' => 'required|string|max:255',
        'description' => 'required|string',
    ]);

    // Menyimpan laporan dengan menambahkan user_id dan answer_id
    Report::create([
        'user_id' => $validated['user_id'],
        'tugas_id' => $validated['tugas_id'],
        'answer_id' => $validated['answer_id'],
        'title' => $validated['title'],
        'description' => $validated['description'],
    ]);

    return response()->json([
        'message' => 'Laporan berhasil dikirim',
    ], 201);
}
public function reportTugas(Request $request)
{
    // Validasi input
    $validated = $request->validate([
        'user_id' => 'required|exists:users,id', // Validasi bahwa user_id ada di tabel users
        'tugas_id' => 'required|exists:tugas,id', // Validasi bahwa user_id ada di tabel users
        'title' => 'required|string|max:255',
        'description' => 'required|string',
    ]);

    // Menyimpan laporan dengan menambahkan user_id dan answer_id
    Report::create([
        'user_id' => $validated['user_id'],
        'tugas_id' => $validated['tugas_id'],
        'title' => $validated['title'],
        'description' => $validated['description'],
    ]);

    return response()->json([
        'message' => 'Laporan berhasil dikirim',
    ], 201);
}


}
