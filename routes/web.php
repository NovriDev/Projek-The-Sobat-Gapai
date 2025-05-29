<?php

use App\Http\Controllers\AdminController;
use Illuminate\Support\Facades\Route;

Route::get('/', function(){
    return view('admin.auth.login');
});

Route::prefix('admin')->group(function () {

Route::get('/admin/dashboard', [AdminController::class, 'dashboard'])->name('admin.dashboard');

// Route untuk memban user
Route::post('/admin/user/{id}/ban', [AdminController::class, 'banUser'])->name('admin.user.ban');

Route::get('/admin/laporan', [AdminController::class, 'showReports'])->name('admin.laporan');

// Route untuk menghapus user
Route::delete('/admin/users/{id}/delete', [AdminController::class, 'deleteUser'])->name('admin.user.hapus');

// Route untuk menampilkan daftar tugas
Route::get('/admin/tugas', [AdminController::class, 'kelolaTugas'])->name('admin.tugas');

// Route untuk menampilkan daftar jawaban
Route::get('/admin/jawaban', [AdminController::class, 'kelolaJawaban'])->name('admin.jawaban');

// Route untuk menghapus jawaban
Route::delete('/admin/jawaban/{id}/hapus', [AdminController::class, 'hapusJawaban'])->name('admin.jawaban.hapus');

// Route untuk menghapus tugas
Route::delete('/admin/tugas/{id}/hapus', [AdminController::class, 'hapusTugas'])->name('admin.tugas.hapus');

// Route untuk logout admin
Route::post('/logout', [AdminController::class, 'logout'])->name('admin.logout');
});
// Route untuk menampilkan form login
Route::get('/login', [AdminController::class, 'showLoginForm'])->name('admin.login');

// Route untuk memproses login
Route::post('/login', [AdminController::class, 'login']);

