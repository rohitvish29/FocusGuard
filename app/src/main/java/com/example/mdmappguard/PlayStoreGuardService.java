package com.example.mdmappguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class PlayStoreGuardService extends AccessibilityService {

    public static boolean isSessionActive = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isSessionActive) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        // चेक करें कि स्क्रीन पर WhatsApp लिखा है या नहीं
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("WhatsApp");

        // अगर WhatsApp नहीं मिला (मतलब यूज़र ने Back, Cross या Search दबाया है)
        if (nodes == null || nodes.isEmpty()) {
            triggerLockdown();
        }
    }

    private void triggerLockdown() {
        isSessionActive = false;

        // स्क्रीन पर मैसेज दिखाएं
        Toast.makeText(getApplicationContext(), "❌ Play Store Locked: You exited WhatsApp!", Toast.LENGTH_LONG).show();

        // यूज़र को तुरंत वापस अपनी ऐप पर ले आएं
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
