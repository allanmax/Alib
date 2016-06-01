package com.common.lib.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaoyl on 16/4/26.
 */
public abstract class NfcBaseFragment extends FragmentActivity {


    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Tag tag;
    private boolean isContinueScan;

    private Timer timer;

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                if (isConnected(tag)) {
                    nfcConnectStatus(1);
                    if (isContinueScan) {
                        isContinueScan = false;
                        isContinueScan = operator();
                    }
                } else {
                    nfcConnectStatus(0);
                }
            } catch (Exception e) {
            }
        }
    };

    /**
     * 设置 继续扫描
     */
    public void setContinueScan() {
        isContinueScan = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{ndef};
        mTechLists = new String[][]{new String[]{android.nfc.tech.NfcV.class.getName()}};

        isContinueScan = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(task);
            }
        }, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    /**
     * @return true auto continue scan   false stop scan
     */
    public abstract boolean operator();

    /**
     * @param status 1 connect  0 disconnect
     */
    public abstract void nfcConnectStatus(int status);


    /**
     * @param tag
     * @return
     * @throws Exception
     */
    private boolean isConnected(Tag tag) throws Exception {
        if (tag == null)
            return false;
        try {
            TagTechnology tt = getTagTechnologyByTag(tag);
            tt.close();
            tt.connect();
            boolean isConnected = tt.isConnected();
            tt.close();
            return isConnected;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TODO：get tag from device
     *
     * @param tag
     * @return
     * @author zhengjunyang
     * @version
     */
    @Nullable
    private TagTechnology getTagTechnologyByTag(Tag tag) {
        String[] techListStrings = tag.getTechList();
        try {
            for (String tech : techListStrings) {
                if (tech == android.nfc.tech.MifareClassic.class.getName()) {
                    return MifareClassic.get(tag);
                } else if (tech == android.nfc.tech.NfcV.class.getName()) {
                    return NfcV.get(tag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tag getTag() {
        return tag;
    }

}
