package com.common.lib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.common.lib.barscan.CaptureActivity;
import com.common.lib.nfc.NfcActivity;
import com.wsncm.lib.ndef.NdefActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.scan_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScan();
            }
        });


        findViewById(R.id.nfc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNfc();
            }
        });



        findViewById(R.id.ndef_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNdef();
            }
        });
    }


    private void openScan() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    private void openNfc() {
        Intent intent = new Intent(this, NfcActivity.class);
        startActivity(intent);
    }


    private void openNdef() {
        Intent intent = new Intent(this, NdefActivity.class);
        startActivity(intent);
    }
}
