package com.mrikso.kodikdownloader.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.mrikso.kodikdownloader.R;

public class DownloadFile {
    
    public static void download(Context context, String link, String fileName) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isUseDvget = true; //sharedPrefs.getBoolean("dvc_dvget", false);
        boolean isUseIDM = false; //sharedPrefs.getBoolean("dvc_idm", false);

        boolean isDVGetInstalled = false;
        boolean isIDMInstalled = false;
        // adm - dvget
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        if (isUseDvget) {
            if (isPackageInstalled("com.dv.adm", context.getPackageManager())) {
                intent.setClassName("com.dv.adm", "com.dv.adm.AEditor");
                isDVGetInstalled = true;
            } else if (isPackageInstalled("com.dv.get", context.getPackageManager())) {
                intent.setClassName("com.dv.get", "com.dv.get.AEditor");
                isDVGetInstalled = true;
            }
             else if (isPackageInstalled("com.dv.adm.pay", context.getPackageManager())) {
                intent.setClassName("com.dv.adm.pay", "com.dv.adm.pay.AEditor");
                isDVGetInstalled = true;
            }
            if (isDVGetInstalled) {
                intent.putExtra("android.intent.extra.TEXT", link);
                intent.putExtra("com.android.extra.filename", fileName);
                
                ContextCompat.startActivity(context, intent, null);
            } else {
                Toast.makeText(
                                context,
                                context.getString(R.string.dvget_not_found),
                                Toast.LENGTH_LONG)
                        .show();
                String url = "market://play.google.com/store/apps/details?id=com.dv.adm";

                openInBrowser(context, url);
            }
        }
        
        // IDM
        else if (isUseIDM) {
            if (isPackageInstalled(
                    "idm.internet.download.manager.adm.lite", context.getPackageManager())) {
                isIDMInstalled = true;
                intent.setClassName(
                        "idm.internet.download.manager.adm.lite",
                        "idm.internet.download.manager.Downloader");
            } else if (isPackageInstalled(
                    "idm.internet.download.manager.plus", context.getPackageManager())) {
                isIDMInstalled = true;
                intent.setClassName(
                        "idm.internet.download.manager.plus",
                        "idm.internet.download.manager.Downloader");
            } else if (isPackageInstalled(
                    "idm.internet.download.manager", context.getPackageManager())) {
                isIDMInstalled = true;
                intent.setClassName(
                        "idm.internet.download.manager",
                        "idm.internet.download.manager.Downloader");
            }
            
            if (isIDMInstalled) {
                intent.putExtra("secure_uri", link);
                intent.setData(Uri.parse(link));
                intent.putExtra("title", fileName);
                intent.putExtra("filename", fileName);

                ContextCompat.startActivity(context, intent, null);
            } else {
                Toast.makeText(
                                context,
                                context.getString(R.string.idm_not_found),
                                Toast.LENGTH_LONG)
                        .show();
                String url =
                        "market://play.google.com/store/apps/details?id=idm.internet.download.manager";

                openInBrowser(context, url);
            }

        } else {
            openInBrowser(context, link);
        }
    }

    private static void openInBrowser(Context context, String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        //browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER);
        try {
            ContextCompat.startActivity(context, browserIntent, null);
            return;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
