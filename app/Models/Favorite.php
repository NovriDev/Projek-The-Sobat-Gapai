<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Favorite extends Model
{
    use HasFactory;

    protected $fillable = ['tugas_id', 'user_id'];

    public static function boot()
    {
        parent::boot();

        // Ketika favorit ditambahkan
        static::created(function ($favorite) {
            $favorite->tugas->update([
                'jml_likes' => $favorite->tugas->favorites()->count(),
            ]);
        });

        // Ketika favorit dihapus
        static::deleted(function ($favorite) {
            $favorite->tugas->update([
                'jml_likes' => $favorite->tugas->favorites()->count(),
            ]);
        });
    }

    public function tugas()
    {
        return $this->belongsTo(Tugas::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }

}
