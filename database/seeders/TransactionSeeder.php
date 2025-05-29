<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class TransactionSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        // Dummy data transaksi
        DB::table('transaction_histories')->insert([
            [
                'user_id' => 1,
                'type' => 'withdraw',
                'amount' => 100000,
                'balance_after' => 900000,
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
            [
                'user_id' => 2,
                'type' => 'deposit',
                'amount' => 200000,
                'balance_after' => 1200000,
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
            [
                'user_id' => 1,
                'type' => 'withdraw',
                'amount' => 50000,
                'balance_after' => 850000,
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
            [
                'user_id' => 3,
                'type' => 'deposit',
                'amount' => 300000,
                'balance_after' => 1300000,
                'created_at' => Carbon::now(),
                'updated_at' => Carbon::now(),
            ],
        ]);
    }
}
