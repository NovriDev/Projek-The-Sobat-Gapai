<?php

namespace Database\Seeders;

use App\Models\Tugas;
use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class TugasSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $users = User::all();

        // Menambahkan beberapa tugas
        foreach ($users as $user) {
            Tugas::create([
                'user_id' => $user->id, // Relasi dengan user yang mengupload
                'keterangan' => 'Tugas Matematika',
                'mapel' => 'Aljabar',
                'deskripsi' => 'Kerjakan soal persamaan linier',
                'image_tugas' => 'image_asset/image.jpg', // Gambar placeholder
            ]);
        }
    }
}
