package com.cartracker.mobile.android.util.handler.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.ui.MyWelcome;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;

import java.util.List;

/**
 * Created by jw362j on 10/7/2014.
 */
public class DeskShortCutInit implements Initializer {
    private Context mContext;

    @Override
    public void init(Context context) {
        this.mContext = context;
        createDeskShortCut();
    }

    private void createDeskShortCut() {
        if(!hasShortcut()){
            //创建快捷方式的Intent
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            //不允许重复创建
            shortcut.putExtra("duplicate", false);
            //需要现实的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getResources().getString(R.string.app_name));
            //快捷图片
            Parcelable icon = Intent.ShortcutIconResource.fromContext(mContext.getApplicationContext(), R.drawable.ic_launcher);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            //快捷方式入口
            Intent intent = new Intent(mContext.getApplicationContext(), MyWelcome.class);
            //下面两个属性是为了当应用程序卸载时，删除桌面上的快捷方式
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            //点击快捷图片，运行的程序主入口
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            //发送广播 OK
            mContext.sendBroadcast(shortcut);
        }
    }

    private boolean hasShortcut() {
        String url = "";
        url = "content://" + getAuthorityFromPermission(mContext, "com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "title=?", new String[]{mContext.getResources().getString(R.string.app_name).trim()}, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    private String getAuthorityFromPermission(Context context, String permission) {
        if (permission == null)
            return null;
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission))
                            return provider.authority;
                        if (permission.equals(provider.writePermission))
                            return provider.authority;
                    }
                }
            }
        }
        return null;
    }
}
