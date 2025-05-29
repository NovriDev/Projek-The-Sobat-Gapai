<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('notifications', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id'); // User yang menerima notifikasi
            $table->string('type'); // 'follow' atau 'like'
            $table->unsignedBigInteger('source_user_id'); // User yang menyebabkan notifikasi
            $table->unsignedBigInteger('task_id')->nullable(); // Jika type = 'like'
            $table->boolean('is_read')->default(false); // Status baca
            $table->timestamps();
        
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('source_user_id')->references('id')->on('users')->onDelete('cascade');
        });        
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('notifications');
    }
};
