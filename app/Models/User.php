<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasFactory, Notifiable, HasApiTokens;

    /**
     * The attributes that are mass assignable.
     *
     * @var list<string>
     */
    protected $fillable = [
        'name',
        'email',
        'password',
        'level',
        'profilePicture',
        'profileBio',
        'level'
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var list<string>
     */
    protected $attributes = [
        'profile_picture' => 'profilePicture',
        'profile_bio' => 'profileBio',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    /**
     * Relasi ke model Tugas (User memiliki banyak Tugas)
     */
    public function tugas()
    {
        return $this->hasMany(Tugas::class);
    }

    /**
     * Relasi ke model Favorite (User memiliki banyak Favorite)
     */
    public function favorites()
    {
        return $this->hasMany(Favorite::class);
    }

    public function friends()
    {
        return $this->belongsToMany(User::class, 'friends', 'user_id', 'friend_id');
    }

    public function followings()
    {
        return $this->belongsToMany(User::class, 'followings', 'user_id', 'followed_id');
    }

    public function followers()
    {
        return $this->belongsToMany(User::class, 'followings', 'followed_id', 'user_id');
    }

    public function answers()
    {
        return $this->hasMany(Answer::class);
    }

     public function sentGifts()
     {
         return $this->hasMany(Gift::class, 'sender_id');
     }
 
     public function receivedGifts()
     {
         return $this->hasMany(Gift::class, 'receiver_id');
     }
}
