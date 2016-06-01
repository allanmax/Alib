package com.common.lib.nfc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.common.lib.R;
import android.widget.TextView;


/**
 * Created by zhaoyl on 16/4/27.
 */
public class NfcActivity extends NfcBaseFragment {

    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nfc_layout);

        tv = (TextView) findViewById(R.id.content);


    }

    @Override
    public boolean operator() {

//        byte[] sAdd = Helper.convertStringToHexBytes(Helper.formatStringAddressStart("78"));
////        byte[] re = NFCCommand.sendReadMultipleBlockCommand(getTag(), sAdd, 6);
//        byte[] re = NFCCommand.sendReadSingleBlockCommand(getTag(), sAdd);


        byte[] sAdd = Helper.convertStringToHexBytes(Helper.formatStringAddressStart("f0"));
//        byte[] re = NFCCommand.sendWriteSingleBlockCommand(getTag(), sAdd, Helper.intToByteArray(100));
//        byte[] re = NFCCommand.sendReadSingleBlockCommand(getTag(), sAdd);
        byte[] re = NFCCommand.sendReadMultipleBlockCommand(getTag(), sAdd, 5);

        Log.d("rrrrr", "********:" + re.length);
        Log.d("rrrrr", "********:" + new String(re));
        return true;
    }

    @Override
    public void nfcConnectStatus(int status) {

    }
}
