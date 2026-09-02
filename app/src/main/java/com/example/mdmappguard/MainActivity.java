package com.example.mdmappguard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAction;
    private TextView tvStatus;
    private TextView tvMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);

        tvMode = new TextView(this);
        tvMode.setTextSize(14);
        tvMode.setText("MODE: Logic Testing (No MDM Needed)");
        tvMode.setTextColor(0xFF0000FF); // Blue color
        tvMode.setPadding(0, 0, 0, 30);
        layout.addView(tvMode);

        tvStatus = new TextView(this);
        tvStatus.setTextSize(18);
        tvStatus.setPadding(0, 0, 0, 40);
        layout.addView(tvStatus);

        btnAction = new Button(this);
        layout.addView(btnAction);
        setContentView(layout);

        btnAction.setOnClickListener(v -> launchProtectedPlayStore());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private boolean isAppInstalled(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void updateUI() {
        if (isAppInstalled(MdmControl.WHATSAPP)) {
            tvStatus.setText("App: WhatsApp is INSTALLED");
            btnAction.setText("Manage / Uninstall WhatsApp");
        } else {
            tvStatus.setText("App: WhatsApp is NOT Installed");
            btnAction.setText("Install WhatsApp");
        }
    }

    private void launchProtectedPlayStore() {
        // सेशन एक्टिव करें ताकि Guard काम करना शुरू कर दे
        PlayStoreGuardService.isSessionActive = true;

        // सीधे WhatsApp पेज पर ले जाएं
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + MdmControl.WHATSAPP));
        startActivity(intent);
    }
}
