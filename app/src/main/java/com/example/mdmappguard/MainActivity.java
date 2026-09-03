private void launchProtectedPlayStore() {
        // 1. सबसे पहले Play Store को Unhide (Show) करने की कोशिश करें
        boolean isDelegationWorking = MdmControl.setAppHidden(this, MdmControl.PLAY_STORE, false);
        
        if (isDelegationWorking) {
            Toast.makeText(this, "Delegation Active: Play Store Unhidden", Toast.LENGTH_SHORT).show();
        }

        // 2. सेशन और टाइमर स्टार्ट करें
        PlayStoreGuardService.isSessionActive = true;
        PlayStoreGuardService.sessionStartTime = System.currentTimeMillis(); 

        // 3. Play Store खोलें
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + MdmControl.WHATSAPP));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            PlayStoreGuardService.isSessionActive = false;
            Toast.makeText(this, "❌ Error: Play Store नहीं खुल पा रहा है! (शायद पूरी तरह ब्लॉक है)", Toast.LENGTH_LONG).show();
        }
    }
