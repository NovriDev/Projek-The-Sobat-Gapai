<?php

namespace App\Notifications;

use Illuminate\Bus\Queueable;
use Illuminate\Notifications\Notification;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Notifications\Messages\MailMessage;
use Illuminate\Notifications\Messages\BroadcastMessage;
use App\Models\User;

class FollowedUserNotif extends Notification implements ShouldQueue
{
    use Queueable;

    protected $follower;

    /**
     * Create a new notification instance.
     */
    public function __construct(User $follower)
    {
        $this->follower = $follower;
    }

    /**
     * Get the notification's delivery channels.
     */
    public function via($notifiable)
    {
        return ['database', 'broadcast']; // Simpan di database & kirim via broadcast (jika ada)
    }

    /**
     * Get the array representation of the notification.
     */
    public function toArray($notifiable)
    {
        return [
            'message' => "{$this->follower->name} mulai mengikuti Anda.",
            'follower_id' => $this->follower->id,
            'follower_name' => $this->follower->name,
            'follower_avatar' => $this->follower->profile_photo_url, // Jika ada avatar
        ];
    }

    /**
     * Get the broadcast representation of the notification.
     */
    public function toBroadcast($notifiable)
    {
        return new BroadcastMessage([
            'message' => "{$this->follower->name} mulai mengikuti Anda.",
            'follower_id' => $this->follower->id,
        ]);
    }

    /**
     * Get the mail representation of the notification.
     */
    public function toMail($notifiable)
    {
        return (new MailMessage)
            ->subject('Pengguna Baru Mengikuti Anda')
            ->line("{$this->follower->name} mulai mengikuti Anda.")
            ->action('Lihat Profil', url("/profile/{$this->follower->id}"))
            ->line('Terima kasih telah menggunakan aplikasi kami!');
    }
}
