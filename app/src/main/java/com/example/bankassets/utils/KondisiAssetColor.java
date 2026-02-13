package com.example.bankassets.utils;

import android.graphics.Color;
import android.widget.TextView;

import com.example.bankassets.R;

public class KondisiAssetColor {
    public static void setKondisiColor(TextView tv, String kondisi) {
        switch (kondisi) {
            case "Baik":
                tv.setBackgroundResource(R.drawable.bg_status_green);
                tv.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Peringatan":
                tv.setBackgroundResource(R.drawable.bg_status_orange);
                tv.setTextColor(Color.parseColor("#EF6C00"));
                break;
            case "Kritis":
                tv.setBackgroundResource(R.drawable.bg_status_red);
                tv.setTextColor(Color.parseColor("#D32F2F"));
                break;
            case "Gudang":
                tv.setBackgroundResource(R.drawable.bg_status_pink);
                tv.setTextColor(Color.parseColor("#613B9E"));
                break;
        }
    }

    public static void setStatusPenggunaanColor(TextView tv, String status) {
        switch (status) {
            case "Aktif":
                tv.setBackgroundResource(R.drawable.bg_status_biru);
                break;
            case "Nonaktif":
                tv.setBackgroundResource(R.drawable.bg_status_yellow);
                break;
        }
    }
}
