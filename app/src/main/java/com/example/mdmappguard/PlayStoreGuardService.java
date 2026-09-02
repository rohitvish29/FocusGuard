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
        // अगर सेशन एक्टिव नहीं है तो कुछ प्रोसेस न करें (बैटरी और बैंकिंग सुरक्षा)
        if (!isSessionActive) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        // चेक करें कि क्या स्क्रीन पर WhatsApp का विवरण मौजूद है
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("WhatsApp");

        // अगर WhatsApp नहीं मिला तो यूजर ने Cross [X], Back या Search दबाया है
        if (nodes == null || nodes.isEmpty()) {
            triggerLockdown();
        }
    }

    private void triggerLockdown() {
        isSessionActive = false;

        // 1. Play Store को Hide करें (Delegated call)
        MdmControl.hideApp(getApplicationContext(), MdmControl.PLAY_STORE);

        // 2. यूजर को वापस अपने ऐप पर ले आएं
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
