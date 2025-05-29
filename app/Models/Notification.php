<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Notification extends Model
{
    protected $fillable = [
        'user_id',
        'type',
        'source_user_id',
        'task_id',
        'is_read',
    ];

    public function user() {
        return $this->belongsTo(User::class);
    }

    public function sourceUser() {
        return $this->belongsTo(User::class, 'source_user_id');
    }
}
