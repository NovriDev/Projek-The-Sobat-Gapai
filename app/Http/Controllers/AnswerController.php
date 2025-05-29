<?php

namespace App\Http\Controllers;

use App\Models\Answer;
use App\Models\Tugas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AnswerController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function show($tugasId)
{
    $tugas = Tugas::with(['answers.user' => function () {
    }])->findOrFail($tugasId);

    return response()->json([
        'tugas' => $tugas,
    ]);
}

// public function show($tugasId)
//     {
//         // Mengambil tugas beserta jawaban dan menghitung jumlah likes dan dislikes
//         $tugas = Tugas::with(['answers' => function ($query) {
//             $query->withCount([
//                 'reactions as likes' => function ($query) {
//                     $query->where('type', 'like');
//                 },
//                 'reactions as dislikes' => function ($query) {
//                     $query->where('type', 'dislike');
//                 }
//             ]);
//         }])->findOrFail($tugasId);

//         return response()->json([
//             'tugas' => $tugas,
//         ]);
//     }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
//     public function store(Request $request)
// {
//     $validated = $request->validate([
//         'user_id' => 'required|exists:users,id',
//         'description' => 'required|string',
//         'image' => 'nullable|image|max:2048' // Validasi gambar
//     ]);

//     // Upload gambar jika ada
//     $imageUrl = null;
//     if ($request->hasFile('image')) {
//         $imagePath = $request->file('image')->store('uploads', 'public');
//         $imageUrl = asset('storage/' . $imagePath);
//     }

//     // Simpan jawaban
//     $answer = Answer::create([
//         'user_id' => $validated['user_id'],
//         'description' => $validated['description'],
//         'image_url' => $imageUrl
//     ]);

//     return response()->json([
//         'success' => true,
//         'message' => 'Answer created successfully',
//         'data' => $answer
//     ]);
// }

public function store(Request $request)
{
    $validated = $request->validate([
        'user_id' => 'required|exists:users,id',
        'tugas_id' => 'required|exists:tugas,id', // Validasi tugas_id
        'description' => 'required|string',
        'imageAnswer' => 'nullable|image|max:10240' // Validasi gambar
    ]);

    // Upload gambar jika ada
    $imageUrl = null;
    if ($request->hasFile('imageAnswer')) {
        $imagePath = $request->file('imageAnswer')->store('uploads', 'public');
        $imageUrl = asset('storage/' . $imagePath);
    }

    // Simpan jawaban
    $answer = Answer::create([
        'user_id' => $validated['user_id'],
        'tugas_id' => $validated['tugas_id'], // Tambahkan tugas_id
        'description' => $validated['description'],
        'imageAnswer' => $imageUrl
    ]);

    return response()->json([
        'success' => true,
        'message' => 'Answer created successfully',
    ]);
}


    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function vote(Request $request)
{
    $validated = $request->validate([
        'user_id' => 'required|integer|exists:users,id',
        'answer_id' => 'required|integer|exists:answers,id',
        'type' => 'required|in:like',
    ]);

    // Cek apakah user sudah memberikan suara sebelumnya
    $existingVote = DB::table('answers_reaction')
        ->where('user_id', $validated['user_id'])
        ->where('answer_id', $validated['answer_id'])
        ->first();

    if ($existingVote) {
        if ($existingVote->type === $validated['type']) {
            return response()->json([
                'message' => 'You have already voted this option.',
            ], 400);
        }

        // Jika jenis suara berbeda, update suara
        DB::table('answers_reaction')
            ->where('id', $existingVote->id)
            ->update(['type' => $validated['type']]);

        return response()->json([
            'message' => 'Vote updated successfully.',
        ]);
    }

    // Jika belum ada suara, simpan suara baru
    DB::table('answers_reaction')->insert([
        'user_id' => $validated['user_id'],
        'answer_id' => $validated['answer_id'],
        'type' => $validated['type'],
    ]);

    return response()->json([
        'message' => 'Vote added successfully.',
    ]);
}

public function getVotes($tugasId)
{
    // Mengambil jumlah likes dan dislikes
    $answer = Answer::withCount([
        'reactions as likes' => function ($query) {
            $query->where('type', 'like');
        },
        'reactions as dislikes' => function ($query) {
            $query->where('type', 'dislike');
        },
    ])->findOrFail($tugasId);

    return response()->json([
        'likes' => $answer->likes,
        'dislikes' => $answer->dislikes,
    ]);
}

public function updateLikesAndDislikes($answerId)
{
    // Ambil jawaban berdasarkan ID
    $answer = Answer::withCount([
        'reactions as likes' => function ($query) {
            $query->where('type', 'like');
        },
        'reactions as dislikes' => function ($query) {
            $query->where('type', 'dislike');
        },
    ])->findOrFail($answerId);

    // Update kolom likes di tabel answers
    $answer->update([
        'likes' => $answer->likes,
    ]);

    return response()->json([
        'message' => 'Likes and dislikes count updated successfully.',
        'data' => [
            'likes' => $answer->likes,
            'dislikes' => $answer->dislikes,
        ],
    ]);
}

}
