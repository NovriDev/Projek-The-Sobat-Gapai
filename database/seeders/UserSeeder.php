<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('users')->insert([
            [
                'name' => 'John Doe',
                'email' => 'johndoe@gmail.com',
                'email_verified_at' => now(),
                'password' => Hash::make('password'),
                'profilePicture' => 'https://static.sociofyme.com/thumb/imgsize-481927,msid-99888353,width-960,height-960,resizemode-1,webp-1/99888353.jpg',
                'profileBio' => 'I am a coffee enthusiast.',
                'remember_token' => \Str::random(10),
                'balance' => 100000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'name' => 'Jane Smith',
                'email' => 'janesmith@gmail.com',
                'email_verified_at' => now(),
                'password' => Hash::make('password123'),
                'profilePicture' => 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyHC9h4miHDT7SeZIs03UdztEnbNELc9GkxQ&s',
                'profileBio' => 'Lover of latte art.',
                'remember_token' => \Str::random(10),
                'balance' => 0,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'name' => 'Noprek',
                'email' => 'NovriMulia@gmail.com',
                'email_verified_at' => now(),
                'password' => Hash::make('password123'),
                'profilePicture' => '',
                'profileBio' => 'Hanya ingin menjadi yang terbaik.',
                'remember_token' => \Str::random(10),
                'balance' => 500000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'name' => 'imamhung',
                'email' => 'htarihoran@mahendra.mil.id',
                'email_verified_at' => now(),
                'password' => Hash::make('imam1234'),
                'profilePicture' => 'https://www.random-name-generator.com/images/faces/male-asia/25.jpg?ezimgfmt=rs:148x148/rscb1/ng:webp/ngcb1',
                'profileBio' => 'Guru Informatika.',
                'remember_token' => \Str::random(10),
                'balance' => 300000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'name' => 'padmri',
                'email' => 'wulandari.putri@gmail.co.id',
                'email_verified_at' => now(),
                'password' => Hash::make('35ae7f84'),
                'profilePicture' => 'https://www.random-name-generator.com/images/faces/female-asia/25.jpg?ezimgfmt=rs:148x148/rscb1/ng:webp/ngcb1',
                'profileBio' => 'Saya Adalah Guru Seni Budaya.',
                'remember_token' => \Str::random(10),
                'balance' => 300000,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
