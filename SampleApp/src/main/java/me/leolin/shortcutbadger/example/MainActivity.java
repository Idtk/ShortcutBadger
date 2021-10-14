package me.leolin.shortcutbadger.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText numInput = findViewById(R.id.numInput);

        Button button = findViewById(R.id.btnSetBadge);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }
//                executeBadge(MainActivity.this,badgeCount);
                boolean success = ShortcutBadger.applyCount(MainActivity.this, badgeCount);

                Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
                goHome();
            }
        });

        Button launchNotification = findViewById(R.id.btnSetBadgeByNotification);
        launchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }
                finish();
                startService(
                    new Intent(MainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                );
                goHome();
            }
        });

        Button removeBadgeBtn = findViewById(R.id.btnRemoveBadge);
        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = ShortcutBadger.removeCount(MainActivity.this);

                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
                goHome();
            }
        });


        //find the home launcher Package
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        String currentHomePackage = "none";
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // in case of duplicate apps (Xiaomi), calling resolveActivity from one will return null
        if (resolveInfo != null) {
            currentHomePackage = resolveInfo.activityInfo.packageName;
        }

        TextView textViewHomePackage = findViewById(R.id.textViewHomePackage);
        textViewHomePackage.setText("launcher:" + currentHomePackage);
    }


    public void goHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }


    public void executeBadge(Context context, int badgeCount) {
        Uri uri = Uri.parse("content://com.hihonor.android.launcher.settings/badge/");
        if (TextUtils.isEmpty(context.getContentResolver().getType(uri))) {
            uri = Uri.parse("content://com.huawei.android.launcher.settings/badge/");
            if (TextUtils.isEmpty(context.getContentResolver().getType(uri))) {
                uri = null;
            }
        }
        try {
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", getClass().getName());
            localBundle.putInt("badgenumber", badgeCount);
            Log.w("HihonorHomeBadger", context.getPackageName());
            Log.w("HihonorHomeBadger", getClass().getName());
            Log.w("HihonorHomeBadger", "构建 bundle");
            if (uri != null) {
                Log.w("HihonorHomeBadger", uri.toString());
                context.getContentResolver().call(uri, "change_badge", (String) null, localBundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
