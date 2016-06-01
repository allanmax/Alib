// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.common.lib.nfc;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;


public class NFCCommand {

    //***********************************************************************/
    //* the function send an Inventory command (0x26 0x01 0x00)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //***********************************************************************/
    public static byte[] sendInventoryCommand(Tag myTag) {
        byte[] UIDFrame = new byte[]{(byte) 0x26, (byte) 0x01, (byte) 0x00};
        byte[] response = new byte[]{(byte) 0x01};

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                nfcvTag.close();
                nfcvTag.connect();
                response = nfcvTag.transceive(UIDFrame);
                nfcvTag.close();
                if (response[0] == (byte) 0x00) {
                    errorOccured = 0;
                }
            } catch (Exception e) {
                errorOccured++;
                if (errorOccured >= 2) {
                    return response;
                }
            }
        }
        Log.i("NFCCOmmand", "Response " + Helper.convertHexByteToString((byte) response[0]));
        return response;
    }


    //***********************************************************************/
    //* the function send an Get System Info command (0x02 0x2B)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //***********************************************************************/
    public static byte[] sendGetSystemInfoCommandCustom(Tag myTag) {

        byte[] response = new byte[]{(byte) 0x01};
        byte[] GetSystemInfoFrame = new byte[2];

        GetSystemInfoFrame = new byte[]{(byte) 0x0A, (byte) 0x2B};

        for (int h = 0; h <= 1; h++) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                nfcvTag.close();
                nfcvTag.connect();
                response = nfcvTag.transceive(GetSystemInfoFrame);
                nfcvTag.close();
                if (response[0] == (byte) 0x00) {
                    h = 2;// to get out of the loop
                }
            } catch (Exception e) {
            }

            Log.i("NFCCOmmand", "Response Get System Info " + Helper.convertHexByteArrayToString(response));
            GetSystemInfoFrame = new byte[]{(byte) 0x02, (byte) 0x2B};
        }
        return response;
    }


    //***********************************************************************/
    //* the function send an ReadSingle command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    public static byte[] sendReadSingleBlockCommand(Tag myTag, byte[] startAddress) {
        byte[] response = new byte[]{(byte) 0x01};
        byte[] readSingleBlockFrame;

        readSingleBlockFrame = new byte[]{(byte) 0x0A, (byte) 0x20, startAddress[0], startAddress[1]};

        int errorOccured = 1;
        NfcV nfcvTag = null;
        while (errorOccured != 0) {
            try {
                nfcvTag = NfcV.get(myTag);
                nfcvTag.close();
                nfcvTag.connect();
                response = nfcvTag.transceive(readSingleBlockFrame);
                if (response[0] == (byte) 0x00) {
                    errorOccured = 0;
                }
            } catch (Exception e) {
                errorOccured++;
                if (errorOccured == 2) {
                    return response;
                }
            } finally {
                if (nfcvTag != null && nfcvTag.isConnected()) {
                    try {
                        nfcvTag.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        Log.i("NFCCOmmand", "Response Read Sigle Block" + Helper.convertHexByteArrayToString(response));
        return response;
    }


    //***********************************************************************/
    //* the function send an ReadSingle Custom command (0x0A 0x20) || (0x02 0x20)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  NbOfBlockToRead : {0x04}
    //* the function will return 04 blocks read from address 0002
    //* According to the ISO-15693 maximum block read is 32 for the same sector
    //***********************************************************************/
    public static byte[] sendReadMultipleBlockCommand(Tag myTag, byte[] startAddress, int nbOfBlockToRead) {
        int nbBytesToRead = (nbOfBlockToRead * 4) + 1;
        byte[] finalResponse = new byte[nbBytesToRead];

        for (int i = 0; i < nbOfBlockToRead; i++) {
            byte[] temp = sendReadSingleBlockCommand(myTag, startAddress);

            if (temp != null && temp[0] == 0x00) {
                if (i == 0) {
                    for (int j = 0; j <= 4; j++) {
                        finalResponse[j] = temp[j];
                    }
                } else {
                    for (int j = 1; j <= 4; j++) {
                        finalResponse[i * 4 + j] = temp[j];
                    }
                }
                startAddress = Helper.addressPlugPlug(startAddress);
            } else {
                return new byte[]{0x00};
            }
        }
        return finalResponse;
    }


    //***********************************************************************/
    //* the function send an WriteSingle command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    public static byte[] sendWriteSingleBlockCommand(Tag myTag, byte[] startAddress, byte[] dataToWrite) {
        byte[] response = new byte[]{(byte) 0x01};
        byte[] writeSingleBlockFrame;

        writeSingleBlockFrame = new byte[]{(byte) 0x0A, (byte) 0x21, startAddress[0], startAddress[1], dataToWrite[0], dataToWrite[1], dataToWrite[2], dataToWrite[3]};

        int errorOccured = 1;
        while (errorOccured != 0) {
            try {
                NfcV nfcvTag = NfcV.get(myTag);
                nfcvTag.close();
                nfcvTag.connect();
                response = nfcvTag.transceive(writeSingleBlockFrame);
                if (response[0] == (byte) 0x00) {
                    errorOccured = 0;
                }
            } catch (Exception e) {
                errorOccured++;
                if (errorOccured == 2) {
                    Log.i("Exception", "Exception " + e.getMessage());
                    return response;
                }
            }
        }
        return response;
    }


    //***********************************************************************/
    //* the function send an Write command (0x0A 0x21) || (0x02 0x21)
    //* the argument myTag is the intent triggered with the TAG_DISCOVERED
    //* example : StartAddress {0x00, 0x02}  DataToWrite : {0x04 0x14 0xFF 0xB2}
    //* the function will write {0x04 0x14 0xFF 0xB2} at the address 0002
    //***********************************************************************/
    public static byte[] sendWriteMultipleBlockCommand(Tag myTag, byte[] StartAddress, byte[] DataToWrite) {
        byte[] response = new byte[]{(byte) 0x01};
        long cpt = 0;
        int NBByteToWrite = DataToWrite.length;
        while (NBByteToWrite % 4 != 0)
            NBByteToWrite++;

        byte[] fullByteArrayToWrite = new byte[NBByteToWrite];
        for (int j = 0; j < NBByteToWrite; j++) {
            if (j < DataToWrite.length) {
                fullByteArrayToWrite[j] = DataToWrite[j];
            } else {
                fullByteArrayToWrite[j] = (byte) 0x00;
            }
        }

        for (int i = 0; i < NBByteToWrite; i = i + 4) {

            int incrementAddressStart0 = (StartAddress[0] + i / 256);                                //Most Important Byte
            int incrementAddressStart1 = (StartAddress[1] + i / 4) - (incrementAddressStart0 * 255);    //Less Important Byte
            response[0] = (byte) 0x01;

            while ((response[0] == (byte) 0x01) && cpt <= 2) {
                response = sendWriteSingleBlockCommand(myTag, new byte[]{(byte) incrementAddressStart0, (byte) incrementAddressStart1}, new byte[]{(byte) fullByteArrayToWrite[i], (byte) fullByteArrayToWrite[i + 1], (byte) fullByteArrayToWrite[i + 2], (byte) fullByteArrayToWrite[i + 3]});
                cpt++;
            }
            if (response[0] == (byte) 0x01)
                return response;
            cpt = 0;
        }
        return response;
    }

}
