package com.example.bankassets.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bankassets.model.AssetModel;

import java.util.List;

public class AssetNotificationChecker {
    public static void checkAndNotify(Context context, List<AssetModel> assets) {

        SharedPreferences userPref =
                context.getSharedPreferences("pref_user", Context.MODE_PRIVATE);

        // 🔕 Jika notifikasi OFF → STOP
        if (!userPref.getBoolean("notification", true)) return;

        for (AssetModel asset : assets) {
            if (asset.getKondisi().equalsIgnoreCase("kritis")) {
                NotificationHelper.sendAssetCriticalNotification(
                        context,
                        asset.getNama(),
                        asset.getDivisi()
                );
            }
        }
    }
}
