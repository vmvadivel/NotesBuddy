package com.vadivel.notesbuddy.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import com.vadivel.notesbuddy.R;

public class AppFeedBackUtil {
    public final static String APP_NAME = "NotesBuddy";
    public final static String APP_PACKAGE_NAME = "com.vadivel.notesbuddy";
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;

    public static void appLaunched(Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences("notes_buddy", 0);
        if (prefs.getBoolean("stop_showing_feedback", false)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();

        long appLaunchCount = prefs.getLong("app_launch_count", 0) + 1;

        editor.putLong("app_launch_count", appLaunchCount);

        Long dateFirstLaunch = prefs.getLong("date_first_launch", 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("date_first_launch", dateFirstLaunch);
        }

        if (appLaunchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showDialog(mContext, editor);
            }
        }
        editor.commit();
    }

    private static void showDialog(final Context mContext, final SharedPreferences.Editor editor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setMessage("If you enjoy using " + APP_NAME + ", please take a moment to rate us. Thanks for your support!");
        builder.setPositiveButton("Rate Us",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        goToMarket(mContext);
                        if (editor != null) {
                            editor.putBoolean("stop_showing_feedback", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton("Remind me later",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (editor != null) {
                            editor.putLong("app_launch_count", 0);
                            editor.putLong("date_first_launch", 0);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("No, thanks",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (editor != null) {
                            editor.putBoolean("stop_showing_feedback", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    public static void goToMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + AppFeedBackUtil.APP_PACKAGE_NAME);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + AppFeedBackUtil.APP_PACKAGE_NAME)));
        }
    }
}