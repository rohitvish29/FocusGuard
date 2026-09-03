package com.example.mdmappguard;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnAction;
    private Button btnSetupGuard;
    private TextView tvStatus;
    private TextView tvServiceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI लेआउट सेटअप
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);

        // सर्विस स्टेटस टेक्स्ट
        tvServiceStatus = new TextView(this);
        tvServiceStatus.setTextSize(18);
        tvServiceStatus.setPadding(0, 0, 0, 40);
        layout.addView(tvServiceStatus);

        // सेटअप बटन (सिर्फ तब दिखेगा जब सर्विस OFF हो)
        btnSetupGuard = new Button(this);
        btnSetupGuard.setText("⚙️ सेटअप: FocusGuard सर्विस ON करें");
        layout.addView(btnSetupGuard);

        // ऐप स्टेटस टेक्स्ट
        tvStatus = new TextView(this);
        tvStatus.setTextSize(16);
        tvStatus.setPadding(0, 60, 0, 40);
        layout.addView(tvStatus);

        // मेन एक्शन बटन (Install WhatsApp) - यह शुरू में छुपा रहेगा
        btnAction = new Button(this);
        layout.addView(btnAction);

        setContentView(layout);

        // बटन के क्लिक इवेंट्स
        btnSetupGuard.setOnClickListener(v -> requestAccessibilityPermission());
        btnAction.setOnClickListener(v -> launchProtectedPlayStore());

        // **ऑटो-रीडायरेक्ट लॉजिक:** अगर ऐप खुलते ही सर्विस OFF है, तो सीधा सेटिंग्स खोल दो
        if (!isAccessibilityServiceEnabled(this, PlayStoreGuardService.class)) {
            Toast.makeText(this, "⚠️ अनिवार्य: कृपया पहले FocusGuard को ON करें!", Toast.LENGTH_LONG).show();
            requestAccessibilityPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(); // जब यूज़र सेटिंग्स से बैक आएगा, तो यह UI को रिफ्रेश करेगा
    }

    // चेक करता है कि सर्विस ON है या नहीं
    private boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            android.content.pm.ServiceInfo serviceInfo = enabledService.getResolveInfo().serviceInfo;
            if (serviceInfo.packageName.equals(context.getPackageName()) && serviceInfo.name.equals(accessibilityService.getName())) {
                return true;
            }
        }
        return false;
    }

    // सीधे Accessibility Settings पेज खोलता है
    private void requestAccessibilityPermission() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isAppInstalled(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // यह फंक्शन तय करता है कि स्क्रीन पर क्या दिखेगा और क्या छुपेगा
    private void updateUI() {
        if (isAccessibilityServiceEnabled(this, PlayStoreGuardService.class)) {
            // अगर परमिशन मिल गई है
            tvServiceStatus.setText("🛡️ डिवाइस सुरक्षित है (Guard ON)");
            tvServiceStatus.setTextColor(0xFF00AA00); // हरा रंग
            
            btnSetupGuard.setVisibility(View.GONE); // सेटअप बटन गायब कर दें
            btnAction.setVisibility(View.VISIBLE);  // Install बटन दिखा दें
            tvStatus.setVisibility(View.VISIBLE);

            if (isAppInstalled(MdmControl.WHATSAPP)) {
                tvStatus.setText("App: WhatsApp इंस्टॉल हो चुका है");
                btnAction.setText("Manage / Uninstall WhatsApp");
            } else {
                tvStatus.setText("App: WhatsApp इंस्टॉल नहीं है");
                btnAction.setText("Install WhatsApp");
            }
        } else {
            // अगर परमिशन नहीं मिली है (Hard Block)
            tvServiceStatus.setText("⚠️ चेतावनी: ऐप लॉक है!\nआगे बढ़ने के लिए FocusGuard सर्विस ON करना अनिवार्य है।");
            tvServiceStatus.setTextColor(0xFFFF0000); // लाल रंग
            
            btnSetupGuard.setVisibility(View.VISIBLE); // सेटअप बटन दिखाएं
            btnAction.setVisibility(View.GONE); // **Install बटन पूरी तरह गायब कर दें**
            tvStatus.setVisibility(View.GONE); // स्टेटस टेक्स्ट भी छुपा दें
        }
    }

    private void launchProtectedPlayStore() {
        PlayStoreGuardService.isSessionActive = true;
        PlayStoreGuardService.sessionStartTime = System.currentTimeMillis(); 

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + MdmControl.WHATSAPP));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            PlayStoreGuardService.isSessionActive = false;
            Toast.makeText(this, "❌ Error: Play Store नहीं खुल पा रहा है!", Toast.LENGTH_LONG).show();
        }
    }
}
