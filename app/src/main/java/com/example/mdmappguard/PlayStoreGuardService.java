package com.example.mdmappguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class PlayStoreGuardService extends AccessibilityService {

    public static boolean isSessionActive = false;
    public static long sessionStartTime = 0; // नया टाइमर

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isSessionActive) return;

        // Play Store को लोड होने के लिए 3.5 सेकंड का समय दें (Grace Period)
        if (System.currentTimeMillis() - sessionStartTime < 3500) {
            return;
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        List<AccessibilityNodeInfo> whatsappNodes = rootNode.findAccessibilityNodeInfosByText("WhatsApp");
        List<AccessibilityNodeInfo> pendingNodes = rootNode.findAccessibilityNodeInfosByText("Pending");
        List<AccessibilityNodeInfo> installingNodes = rootNode.findAccessibilityNodeInfosByText("Installing");
        List<AccessibilityNodeInfo> downloadingNodes = rootNode.findAccessibilityNodeInfosByText("Downloading");

        boolean isDownloading = (pendingNodes != null && !pendingNodes.isEmpty()) || 
                                (installingNodes != null && !installingNodes.isEmpty()) ||
                                (downloadingNodes != null && !downloadingNodes.isEmpty());

        // अगर 3.5 सेकंड बाद भी WhatsApp नहीं मिला और डाउनलोडिंग भी नहीं चल रही है, तो ऐप लॉक कर दें
        if ((whatsappNodes == null || whatsappNodes.isEmpty()) && !isDownloading) {
            triggerLockdown();
        }
    }

    private void triggerLockdown() {
        isSessionActive = false;
        Toast.makeText(getApplicationContext(), "❌ Play Store Locked: Exited WhatsApp!", Toast.LENGTH_LONG).show();
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
