<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Tugas extends Model
{
    use HasFactory;

    protected $fillable = ['user_id', 'keterangan', 'mapel', 'deskripsi', 'image_tugas'];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function favorites()
    {
        return $this->hasMany(Favorite::class);
    }

    public function favoriteCount()
    {
        return $this->favorites()->count();
    }

    public function answers()
    {
        return $this->hasMany(Answer::class, 'tugas_id');
    }
    
}
