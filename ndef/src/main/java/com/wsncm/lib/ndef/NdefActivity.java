package com.wsncm.lib.ndef;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

/**
 * 读取设备id
 */
public class NdefActivity extends Activity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    EditText v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        v = (EditText) findViewById(R.id.value);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(NdefActivity.this);
        mNfcPendingIntent = PendingIntent.getActivity(NdefActivity.this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private void enableTagWriteMode() {
        IntentFilter intentFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] intentFilters = new IntentFilter[]{intentFilter};
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, intentFilters, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableTagWriteMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableTagWriteMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void disableTagWriteMode() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            v.setText(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
        }
    }

    private String ByteArrayToHexString(byte[] inarray) { // converts byte arrays to string
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}
