package com.example.mdmappguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class PlayStoreGuardService extends AccessibilityService {

    public static boolean isSessionActive = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isSessionActive) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        // Verify if WhatsApp text or package reference is present on screen
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("WhatsApp");

        // If WhatsApp is missing, user exited, searched, or tapped back/cross
        if (nodes == null || nodes.isEmpty()) {
            triggerLockdown();
        }
    }

    private void triggerLockdown() {
        isSessionActive = false;
        
        // 1. Force close and hide Play Store
        MdmControl.hideApp(MdmControl.PLAY_STORE);
        
        // 2. Disable accessibility immediately (prevents banking app issues)
        MdmControl.disableAccessibility();
        disableSelf();

        // 3. Return user to our kiosk launcher
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
