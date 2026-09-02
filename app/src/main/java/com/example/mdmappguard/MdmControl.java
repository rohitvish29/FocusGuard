package com.example.mdmappguard;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import java.util.List;

public class MdmControl {

    public static final String PLAY_STORE = "com.android.vending";
    public static final String WHATSAPP = "com.whatsapp";

    // चेक करें कि ऐप के पास Package Access Delegation है या नहीं
    public static boolean hasDelegation(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            List<String> scopes = dpm.getDelegatedScopes(null, context.getPackageName());
            return scopes.contains(DevicePolicyManager.DELEGATION_PACKAGE_ACCESS);
        }
        return false;
    }

    // App को Hide करना (Delegated API)
    public static void hideApp(Context context, String packageName) {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (dpm != null) {
                // Delegated ऐप होने की वजह से admin component 'null' पास होता है
                dpm.setApplicationHidden(null, packageName, true);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Play Store को Unhide करना (Delegated API)
    public static void unhidePlayStore(Context context) {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (dpm != null) {
                dpm.setApplicationHidden(null, PLAY_STORE, false);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
