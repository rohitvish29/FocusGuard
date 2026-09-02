package com.example.mdmappguard;

import android.content.Context;
import java.io.DataOutputStream;

public class MdmControl {

    public static final String PLAY_STORE = "com.android.vending";
    public static final String WHATSAPP = "com.whatsapp";

    public static void runPrivileged(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            try {
                Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            } catch (Exception ignored) {}
        }
    }

    public static void hideApp(String packageName) {
        runPrivileged("am force-stop " + packageName);
        runPrivileged("pm uninstall -k --user 0 " + packageName);
    }

    public static void unhidePlayStore() {
        runPrivileged("cmd package install-existing " + PLAY_STORE);
        runPrivileged("pm enable " + PLAY_STORE);
    }

    public static void enableAccessibility(Context context) {
        String service = context.getPackageName() + "/" + PlayStoreGuardService.class.getName();
        runPrivileged("settings put secure enabled_accessibility_services " + service);
        runPrivileged("settings put secure accessibility_enabled 1");
    }

    public static void disableAccessibility() {
        runPrivileged("settings put secure enabled_accessibility_services \"\"");
        runPrivileged("settings put secure accessibility_enabled 0");
    }
}
