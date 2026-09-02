package com.example.mdmappguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class PackageChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (data == null) return;
        String packageName = data.getSchemeSpecificPart();

        if (MdmControl.WHATSAPP.equals(packageName)) {
            // WhatsApp का इंस्टॉल या अनइंस्टॉल पूरा हुआ
            PlayStoreGuardService.isSessionActive = false;
            Toast.makeText(context, "✅ WhatsApp action completed!", Toast.LENGTH_LONG).show();
            redirectToHome(context);
        } else if (!MdmControl.PLAY_STORE.equals(packageName)) {
            // कोई दूसरी ऐप इंस्टॉल होने की कोशिश हुई
            PlayStoreGuardService.isSessionActive = false;
            Toast.makeText(context, "❌ Unauthorized App Blocked!", Toast.LENGTH_LONG).show();
            redirectToHome(context);
        }
    }

    // यूज़र को होम स्क्रीन (MainActivity) पर वापस भेजने का फंक्शन
    private void redirectToHome(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
}
