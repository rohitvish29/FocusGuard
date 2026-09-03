package com.example.mdmappguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class PlayStoreGuardService extends AccessibilityService {

    public static boolean isSessionActive = false;
    public static long sessionStartTime = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isSessionActive) return;

        // Play Store को लोड होने के लिए 3.5 सेकंड का समय दें 
        if (System.currentTimeMillis() - sessionStartTime < 3500) {
            return;
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        // स्क्रीन पर WhatsApp और डाउनलोडिंग से जुड़े शब्दों की जाँच
        List<AccessibilityNodeInfo> whatsappNodes = rootNode.findAccessibilityNodeInfosByText("WhatsApp");
        List<AccessibilityNodeInfo> pendingNodes = rootNode.findAccessibilityNodeInfosByText("Pending");
        List<AccessibilityNodeInfo> installingNodes = rootNode.findAccessibilityNodeInfosByText("Installing");
        List<AccessibilityNodeInfo> downloadingNodes = rootNode.findAccessibilityNodeInfosByText("Downloading");

        boolean isDownloading = (pendingNodes != null && !pendingNodes.isEmpty()) || 
                                (installingNodes != null && !installingNodes.isEmpty()) ||
                                (downloadingNodes != null && !downloadingNodes.isEmpty());

        // यदि यूज़र ने Back दबाया या WhatsApp पेज से बाहर निकला
        if ((whatsappNodes == null || whatsappNodes.isEmpty()) && !isDownloading) {
            forceExitToFocusGuard();
        }
    }

    // यह वह हाइब्रिड फंक्शन है जो Hide/Unhide और Accessibility दोनों को हैंडल करेगा
    private void forceExitToFocusGuard() {
        isSessionActive = false;
        
        // 1. Delegation के ज़रिए Play Store को हमेशा के लिए Hide करने की कोशिश करें
        boolean isHidden = MdmControl.setAppHidden(getApplicationContext(), MdmControl.PLAY_STORE, true);

        if (isHidden) {
            Toast.makeText(getApplicationContext(), "🔒 Play Store पूरी तरह Hide कर दिया गया है!", Toast.LENGTH_LONG).show();
        } else {
            // 2. अगर Delegation काम नहीं कर रहा, तो ज़बरदस्ती बैकग्राउंड में भेजें (Accessibility Fallback)
            Toast.makeText(getApplicationContext(), "❌ Play Store Locked (Accessibility Guard)", Toast.LENGTH_LONG).show();
            performGlobalAction(GLOBAL_ACTION_HOME); // Home बटन दबाने का सिग्नल
        }

        // 3. तुरंत FocusGuard (MainActivity) को स्क्रीन पर ले आएं
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
