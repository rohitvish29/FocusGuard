private void launchProtectedPlayStore() {
        // सेशन एक्टिव करें और टाइमर स्टार्ट करें
        PlayStoreGuardService.isSessionActive = true;
        PlayStoreGuardService.sessionStartTime = System.currentTimeMillis(); 

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + MdmControl.WHATSAPP));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            PlayStoreGuardService.isSessionActive = false;
            android.widget.Toast.makeText(this, "❌ Error: Play Store MDM द्वारा Disabled है!", android.widget.Toast.LENGTH_LONG).show();
        }
    }
