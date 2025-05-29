<?php

namespace App\Http\Controllers;

use App\Models\Gift;
use App\Models\User;
use Illuminate\Http\Request;

class GiftController extends Controller
{
    public function sendGift(Request $request)
    {
        $request->validate([
            'sender_id' => 'required|exists:users,id',
            'receiver_id' => 'required|exists:users,id',
            'amount' => 'required|numeric|min:1',
            'message' => 'nullable|string',
        ]);

        $sender = User::find($request->sender_id);
        $receiver = User::find($request->receiver_id);
        $amount = $request->amount;

        // Cek apakah saldo cukup
        if ($sender->balance < $amount) {
            return response()->json(['error' => 'Saldo tidak mencukupi'], 400);
        }

        // Kurangi saldo pengirim
        $sender->balance -= $amount;
        $sender->save();

        // Tambahkan saldo penerima
        $receiver->balance += $amount;
        $receiver->save();

        // Simpan ke tabel riwayat gift
        Gift::create([
            'sender_id' => $sender->id,
            'receiver_id' => $receiver->id,
            'message' => $request->message,
            'amount' => $amount,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Gift berhasil dikirim!',
        ]);
    }
}
