package com.example.mdmappguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PackageChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (data == null) return;
        String packageName = data.getSchemeSpecificPart();

        if (MdmControl.WHATSAPP.equals(packageName)) {
            // Target action completed (install, uninstall, or update)
            PlayStoreGuardService.isSessionActive = false;
            MdmControl.hideApp(MdmControl.PLAY_STORE);
            MdmControl.disableAccessibility();
        } else if (!MdmControl.PLAY_STORE.equals(packageName)) {
            // Unauthorized package detected
            MdmControl.hideApp(packageName);
            MdmControl.hideApp(MdmControl.PLAY_STORE);
            MdmControl.disableAccessibility();
        }
    }
}
