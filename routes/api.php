<?php

use App\Http\Controllers\AnswerController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\FavoriteController;
use App\Http\Controllers\NotificationController;
use App\Http\Controllers\ReportController;
use App\Http\Controllers\TugasController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

Route::post('login', [AuthController::class, 'login']);
Route::post('register', [AuthController::class, 'register']);
Route::middleware('auth:sanctum')->group(function (){
    Route::post('/tugas/upload', [TugasController::class, 'store']); // Membuat tugas baru
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/tugas/{id}', [TugasController::class, 'show']); // Menampilkan detail tugas
    Route::get('/user-tugas', [TugasController::class, 'getUserTasks']); // Menampilkan tugas yang diupload oleh pengguna yang sedang login
    Route::get('/user-profile-tugas/{userId}', [TugasController::class, 'getUserTasksWithId']); // Menampilkan tugas dengan user id
    Route::delete('/user-tugas/{id}', [TugasController::class, 'destroy']); // Menghapus tugas
    Route::put('/users/{userId}', [AuthController::class, 'updateUserLevel']);
    Route::put('/profile/update', [AuthController::class, 'updateUser']);

    // Route Favorit
    Route::post('/favorites', [FavoriteController::class, 'store']); // Menambahkan favorit
    Route::delete('/favorites', [FavoriteController::class, 'destroy']); // Menghapus favorit 
    Route::get('/favorites/status/{tugasId}', [FavoriteController::class, 'getFavoriteStatus']);

    Route::get('user/followers', [AuthController::class, 'getFollowers']);
    Route::post('follow/{userId}', [AuthController::class, 'follow']);
    Route::post('unfollow/{userId}', [AuthController::class, 'unfollow']);
    Route::get('suggested-friends', [AuthController::class, 'suggestedFriends']);

    Route::get('/tugas/{tugasId}', [TugasController::class, 'viewTugasUser']);


    Route::post('/laporkan/answer', [ReportController::class, 'reportAnswer']);
    Route::post('/laporkan/tugas', [ReportController::class, 'reportTugas']);


    Route::get('/notifications', [NotificationController::class, 'getUserNotifications']);

    Route::get('/answers/{id}', [AnswerController::class, 'show']);
    Route::post('/{tugasId}/answers', [AnswerController::class, 'store']);

    Route::get('/profile', [AuthController::class, 'getProfile']);
    Route::get('/other-profile/{userId}', [AuthController::class, 'getOtherProfile']);

    Route::post('/answers/vote', [AnswerController::class, 'vote']);
    Route::get('/answers/{answerId}/votes', [AnswerController::class, 'getVotes']);
    Route::put('/answers/{answerId}/update-votes', [AnswerController::class, 'updateLikesAndDislikes']);

    // // Endpoint untuk menarik saldo
    // Route::post('/withdraw', [AuthController::class, 'withdraw']);

    // // Endpoint untuk deposit (misalnya jika ada fitur deposit)
    // Route::post('/deposit', [AuthController::class, 'deposit']);
});

Route::get('/tugas', [TugasController::class, 'index']); // Menampilkan semua tugas

