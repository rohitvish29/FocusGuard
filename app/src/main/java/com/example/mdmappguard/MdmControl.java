package com.example.mdmappguard;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

public class MdmControl {
    public static final String WHATSAPP = "com.whatsapp";
    public static final String PLAY_STORE = "com.android.vending";

    // ऐप को Hide या Unhide करने का फंक्शन
    public static boolean setAppHidden(Context context, String packageName, boolean hide) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            // null पास करने का मतलब है कि हम Delegated Scope का इस्तेमाल कर रहे हैं
            boolean result = dpm.setApplicationHidden(null, packageName, hide);
            Log.d("MdmControl", "App Hidden state changed: " + result);
            return true; 
        } catch (SecurityException e) {
            // अगर MDM ने डेलिगेशन नहीं दिया है, तो यह एरर आएगा
            Log.e("MdmControl", "Delegation Missing: " + e.getMessage());
            return false;
        }
    }
}
