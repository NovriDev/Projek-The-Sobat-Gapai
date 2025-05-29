<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Vote extends Model
{
    use HasFactory;
    protected $table = "answers_reaction";
    protected $fillable = ['user_id', 'answer_id', 'type'];

      /**
     * Relasi ke model Answer.
     * Setiap vote terkait dengan satu jawaban.
     */
    public function answer()
    {
        return $this->belongsTo(Answer::class, 'answer_id');
    }

    /**
     * Relasi ke model User.
     * Setiap vote diberikan oleh satu pengguna.
     */
    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
