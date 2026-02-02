package com.example.bankassets.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.bankassets.R;

public class NotificationHelper {

    public static final String CHANNEL_ID = "asset_kritis_channel";

    public static void sendAssetCriticalNotification(Context context, String namaAsset, String divisi) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ANDROID 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notifikasi Aset Kritis",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi jika aset dalam kondisi kritis");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bg_status_red) // pastikan ada
                        .setContentTitle("⚠️ Aset Kritis")
                        .setContentText(
                                "Divisi " + divisi +
                                        " - Aset \"" + namaAsset +
                                        "\" dalam kondisi KRITIS"
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
