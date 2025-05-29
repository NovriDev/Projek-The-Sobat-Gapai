<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class AnswerSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('answers')->insert([
            [
                'user_id' => 1,
                'tugas_id' => 1,
                'description' => 'Ini adalah jawaban pertama.',
                'imageAnswer' => 'https://cdn.maatloob.com/profile/services/p650x650/img-20231020-135801781-6532250cafca84-20360833.jpg',
                'likes' => '20',
                'dislikes' => '2',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 4,
                'tugas_id' => 2,
                'description' => 'Ini adalah jawaban kedua.',
                'imageAnswer' => 'https://storage.googleapis.com/fastwork-static/4f720fb2-6338-47ed-aa40-3e1e7759da83.jpg',
                'likes' => '30',
                'dislikes' => '3',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => 3,
                'tugas_id' => 2,
                'description' => 'Ini adalah jawaban ketiga.',
                'imageAnswer' => 'https://id-static.z-dn.net/files/dc0/75dc89aaa92b10067438b226c1c515f7.jpg',
                'likes' => '30',
                'dislikes' => '3',
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
