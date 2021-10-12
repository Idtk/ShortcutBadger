package me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;

/**
 * @author Idtk
 */
public class HihonorHomeBadger implements Badger {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        String URI_OLD = "content://com.huawei.android.launcher.settings/badge/";
        String URI_NEW = "content://com.hihonor.android.launcher.settings/badge/";
        Uri uri = Uri.parse(URI_NEW);
        String type = context.getContentResolver().getType(uri);
        if (TextUtils.isEmpty(type)) {
            uri = Uri.parse(URI_OLD);
            type = context.getContentResolver().getType(uri);
            if (TextUtils.isEmpty(type)) {
                uri = null;
            }
        }
        try {
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", componentName.getClassName());
            localBundle.putInt("badgenumber", badgeCount);
            if (uri != null) {
                context.getContentResolver().call(uri, "change_badge", null, localBundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.hihonor.android.launcher"
        );
    }
}
