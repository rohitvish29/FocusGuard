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
    private TextView tvDelegation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);

        tvDelegation = new TextView(this);
        tvDelegation.setTextSize(14);
        tvDelegation.setPadding(0, 0, 0, 30);
        layout.addView(tvDelegation);

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
        // डेलिगेशन स्टेटस चेक
        if (MdmControl.hasDelegation(this)) {
            tvDelegation.setText("MDM Status: Delegation GRANTED (Active)");
            tvDelegation.setTextColor(0xFF008800); // Green
        } else {
            tvDelegation.setText("MDM Status: Delegation NOT GRANTED!\n(Run: dpm set-delegated-scopes)");
            tvDelegation.setTextColor(0xFFFF0000); // Red
        }

        if (isAppInstalled(MdmControl.WHATSAPP)) {
            tvStatus.setText("App: WhatsApp is INSTALLED");
            btnAction.setText("Manage / Uninstall WhatsApp");
        } else {
            tvStatus.setText("App: WhatsApp is NOT Installed");
            btnAction.setText("Install WhatsApp");
        }
    }

    private void launchProtectedPlayStore() {
        // सेशन एक्टिव करें
        PlayStoreGuardService.isSessionActive = true;

        // Play Store को Delegated API से Unhide करें
        MdmControl.unhidePlayStore(this);

        // सीधे WhatsApp पेज पर ले जाएं
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + MdmControl.WHATSAPP));
        startActivity(intent);
    }
}
