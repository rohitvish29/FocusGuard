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
            // WhatsApp का एक्शन पूरा हुआ -> Play Store हाइड करें
            PlayStoreGuardService.isSessionActive = false;
            MdmControl.hideApp(context, MdmControl.PLAY_STORE);
        } else if (!MdmControl.PLAY_STORE.equals(packageName)) {
            // कोई अनाधिकृत ऐप इंस्टॉल हुई -> ऐप और Play Store दोनों हाइड करें
            MdmControl.hideApp(context, packageName);
            MdmControl.hideApp(context, MdmControl.PLAY_STORE);
            PlayStoreGuardService.isSessionActive = false;
        }
    }
}
