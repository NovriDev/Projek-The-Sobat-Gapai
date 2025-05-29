<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

    class Answer extends Model
    {
        use HasFactory;

        protected $fillable = [
            'user_id',
            'tugas_id',
            'description',
            'imageAnswer',
            'likes',
            'dislikes',
        ];

        // Relasi ke model User
        public function user()
        {
            return $this->belongsTo(User::class, 'user_id');
        }

        public function tugas()
        {
            return $this->belongsTo(Tugas::class);
        }

        public function incrementLikes()
        {
            $this->increment('likes');
        }

        public function incrementDislikes()
        {
            $this->increment('dislikes');
        }

        public function decrementLikes()
        {
            if ($this->likes > 0) {
                $this->decrement('likes');
            }
        }

        public function decrementDislikes()
        {
            if ($this->dislikes > 0) {
                $this->decrement('dislikes');
            }
        }

        public function likes()
        {
            return $this->hasMany(Vote::class)->where('type', 'like');
        }

        public function dislikes()
        {
            return $this->hasMany(Vote::class)->where('type', 'dislike');
        }

        public function reactions()
    {
        return $this->hasMany(Vote::class, 'answer_id');
    }

        
    }
