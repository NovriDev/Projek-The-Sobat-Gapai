package com.example.the_sobat_gapai;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getRelativeTime(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            Date past = dateFormat.parse(dateTime);
            Date now = new Date();

            long duration = now.getTime() - past.getTime();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            long days = TimeUnit.MILLISECONDS.toDays(duration);

            if (seconds < 60) {
                return seconds < 10 ? "baru saja" : seconds + " detik yang lalu";
            } else if (minutes < 60) {
                return minutes + " menit lalu";
            } else if (hours < 24) {
                return hours + " jam lalu";
            } else if (days < 7) {
                return days + " hari lalu";
            } else {
                return "bulan lalu";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "tanggal tidak valid";
        }
    }
}
