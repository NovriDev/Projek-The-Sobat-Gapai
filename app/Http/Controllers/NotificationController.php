<?php

namespace App\Http\Controllers;

use App\Models\Notification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function getUserNotifications()
{
    $userId = auth()->id(); // Mendapatkan ID pengguna dari token

    // Memuat notifikasi bersama data pengguna yang menyebabkan notifikasi
    $notifications = Notification::where('user_id', $userId)
        ->with('sourceUser:id,name') // Memuat relasi ke pengguna lain (sourceUser)
        ->orderBy('created_at', 'desc')
        ->get();

    return response()->json($notifications);
}


    
}
