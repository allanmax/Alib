// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.common.lib.nfc;


//**** SUMMARY *****

// castHexKeyboard
// input : String "AZER" 
// output : String "AFEF"

// StringForceDigit
// input : String "23"
// input : Int 4
// output : String"0023"

// ConvertHexByteToString 
// input :  byte 0x0F  
// output : String "0F" 

// ConvertHexByteArrayToStrin
// input :  byte[] { 0X0F ; 0X43 ; 0xA4 ; ...}
// output : String "0F 43 A4 ..." 

// FormatStringAddressStart
// input : String "0F"
// input : DataDevice
// output: String  "000F"

// ConvertIntToHexFormatString
// input : Int 2047
// output : String "7FF"

// FormatStringNbBlock
// input :  String "2"  
// output : String "02"

// ConvertStringToHexBytes
// input : String "0F43" 
// output : byte[] { 0X0F ; 0X43 }

// ConvertStringToHexByte
// input : String "43" 
// output : byte { 0X43 }

// ConvertIntTo2bytesHexaFormat
// input : Int 1876 
// output : byte[] {0x07, 0x54}

// Convert2bytesHexaFormatToInt
// input : byte[] {0x07, 0x54}
// output : Int 1876

// ConvertStringToInt
// input : String "0754"
// output : Int 1876

// FormatDisplayReadBlock 
// input : byte[] ReadMultipleBlockAnswer & byte[]AddressStart 
// output : String "Block 0 : 32 FF EE 44"

import android.text.TextUtils;

public class Helper {

    public static byte[] intToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    //***********************************************************************/
    //* the function cast a String to hexa character only
    //* when a character is not hexa it's replaced by '0'
    //* Example : ConvertHexByteToString ("AZER") -> returns "AFEF"
    //* Example : ConvertHexByteToString ("12l./<4") -> returns "12FFFF4"
    //***********************************************************************/
    public static String castHexKeyboard(String sInput) {
        String sOutput = "";

        sInput = sInput.toUpperCase();
        char[] cInput = sInput.toCharArray();

        for (int i = 0; i < sInput.length(); i++) {
            if (cInput[i] != '0' && cInput[i] != '1' && cInput[i] != '2' && cInput[i] != '3' && cInput[i] != '4' && cInput[i] != '5' &&
                    cInput[i] != '6' && cInput[i] != '7' && cInput[i] != '8' && cInput[i] != '9' && cInput[i] != 'A' && cInput[i] != 'B' &&
                    cInput[i] != 'C' && cInput[i] != 'D' && cInput[i] != 'E') {
                cInput[i] = 'F';
            }
            sOutput += cInput[i];
        }

        return sOutput;
    }

    //***********************************************************************/
    //* the function Format a String with the right number of digit
    //* Example : ConvertHexByteToString ("23",4) -> returns "0023"
    //* Example : ConvertHexByteToString ("54",7) -> returns "0000054"
    //***********************************************************************/
    public static String stringForceDigit(String sStringToFormat, int nbOfDigit) {
        String sStringFormated = sStringToFormat.replaceAll(" ", "");

        if (sStringFormated.length() == 4) {
            return sStringFormated;
        } else if (sStringFormated.length() < nbOfDigit) {
            while (sStringFormated.length() != nbOfDigit) {
                sStringFormated = "0".concat(sStringFormated);
            }
        }

        return sStringFormated;
    }

    //***********************************************************************/
    //* the function Convert byte value to a "2-char String" Format
    //* Example : ConvertHexByteToString ((byte)0X0F) -> returns "0F"
    //***********************************************************************/
    public static String convertHexByteToString(byte byteToConvert) {
        String ConvertedByte = "";
        if (byteToConvert < 0) {
            ConvertedByte += Integer.toString(byteToConvert + 256, 16)
                    + " ";
        } else if (byteToConvert <= 15) {
            ConvertedByte += "0" + Integer.toString(byteToConvert, 16)
                    + " ";
        } else {
            ConvertedByte += Integer.toString(byteToConvert, 16) + " ";
        }

        return ConvertedByte;
    }


    //***********************************************************************/
    //* the function Convert byte Array to a "String" Formated with spaces
    //* Example : ConvertHexByteArrayToString { 0X0F ; 0X43 } -> returns "0F 43"
    //***********************************************************************/
    public static String convertHexByteArrayToString(byte[] byteArrayToConvert) {
        String ConvertedByte = "";
        for (int i = 0; i < byteArrayToConvert.length; i++) {
            if (byteArrayToConvert[i] < 0) {
                ConvertedByte += Integer.toString(byteArrayToConvert[i] + 256, 16)
                        + " ";
            } else if (byteArrayToConvert[i] <= 15) {
                ConvertedByte += "0" + Integer.toString(byteArrayToConvert[i], 16)
                        + " ";
            } else {
                ConvertedByte += Integer.toString(byteArrayToConvert[i], 16) + " ";
            }
        }

        return ConvertedByte;
    }

    //***********************************************************************/
    //* the function verify and convert the start address from the EditText
    //* in order to not read out of memory range and code String on 4chars.
    //* Example : FormatStringAddressStart ("0F") -> returns "000F"
    //* Example : FormatStringAddressStart ("FFFF") -> returns "07FF"
    //***********************************************************************/
    public static String formatStringAddressStart(String stringToFormat) {
        stringToFormat = stringForceDigit(stringToFormat, 4);


        int iAddressStart = convertStringToInt(stringToFormat);
        int iAddresStartMax = convertStringToInt(stringForceDigit("FFFF", 4));

        if (iAddressStart > iAddresStartMax) {
            iAddressStart = iAddresStartMax;
        }

        stringToFormat = convertIntToHexFormatString(iAddressStart);


        return stringToFormat.toUpperCase();
    }

    //***********************************************************************/
    //* this function give the right format for the 4 EditText to fill in
    //* the screen BASICWRITE
    //***********************************************************************/
    public static String formatValueByteWrite(String stringToFormat) {
        String stringFormated = stringToFormat;
        stringFormated = stringForceDigit(stringToFormat, 2);
        stringFormated = castHexKeyboard(stringFormated);
        return stringFormated.toUpperCase();
    }

    //***********************************************************************/
    //* the function convert an Int value to a String with Hexadecimal format
    //* Example : ConvertIntToHexFormatString (2047) -> returns "7FF"
    //***********************************************************************/
    public static String convertIntToHexFormatString(int iNumberToConvert) {
        String sConvertedNumber = "";
        byte[] bNumberToConvert;

        bNumberToConvert = convertIntTo2bytesHexaFormat(iNumberToConvert);
        sConvertedNumber = convertHexByteArrayToString(bNumberToConvert);
        sConvertedNumber = sConvertedNumber.replaceAll(" ", "");
        return sConvertedNumber;
    }

    /**
     * 地址 +1
     * @param mAddress
     * @return
     */
    public static byte[] addressPlugPlug(byte[] mAddress) {
        int mask = 0xFF;
        int result = 0;
        result = mAddress[0] & mask;
        result = result + ((mAddress[1] & mask) << 8) + 1;

        byte[] ret = new byte[2];
        ret[0] = (byte) (result & 0xFF);
        ret[1] = (byte) ((result >> 8) & 0xFF);
        return ret;
    }


    //***********************************************************************/
    //* the function verify and convert the NbBlock from the EditText
    //* in order to not read out of memory range and code String on 4chars.
    //* Example : FormatStringAddressStart ("0F") -> returns "000F"
    //* Example : FormatStringAddressStart ("FFFF") -> returns "07FF"
    //***********************************************************************/
    public static String formatStringNbBlock(String stringToformat, String sAddressStart) {
        String sNbBlockToRead = stringToformat;
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        int iNbBlockToRead = convertStringToInt(sNbBlockToRead);
        int iAddressStart = convertStringToInt(sAddressStart);
        int iAddresStartMax = convertStringToInt(stringForceDigit("FFFF", 4));

        if (iAddressStart + iNbBlockToRead > iAddresStartMax) {
            iNbBlockToRead = iAddresStartMax - iAddressStart + 1;
        }
        /*
        else if(iNbBlockToRead > iAddresStartMax)
		{
			iNbBlockToRead = iAddresStartMax +1;
		}
		*/

        sNbBlockToRead = convertIntToHexFormatString(iNbBlockToRead);
        sNbBlockToRead = stringForceDigit(sNbBlockToRead, 4);

        return sNbBlockToRead;
    }

    //***********************************************************************/
    //* the function Convert a "4-char String" to a two bytes format
    //* Example : "0F43" -> { 0X43 ; 0X0F }
    //***********************************************************************/
    public static byte[] convertStringToHexBytes(String stringToConvert) {
        byte digest[] = new byte[stringToConvert.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = stringToConvert.substring(2 * (digest.length - 1 - i), 2 * (digest.length - 1 - i) + 2);
            digest[i] = convertStringToHexByte(byteString);
        }
        return digest;
    }


    //***********************************************************************/
    //* the function Convert a "4-char String" to a two bytes format
    //* Example : "43" -> { 0X43 }
    //***********************************************************************/
    public static byte convertStringToHexByte(String stringToConvert) {
        if (TextUtils.isEmpty(stringToConvert) || stringToConvert.length() != 2) {
            return 0;
        }
        stringToConvert = stringToConvert.toUpperCase();
        return (byte) Integer.parseInt(stringToConvert, 16);
    }


    //***********************************************************************/
    //* the function Convert Int value to a "2 bytes Array" Format
    //*  (decimal)1876 == (hexadecimal)0754
    //* Example : ConvertIntTo2bytesHexaFormat (1876) -> returns {0x07, 0x54}
    //***********************************************************************/
    public static byte[] convertIntTo2bytesHexaFormat(int numberToConvert) {
        byte[] ConvertedNumber = new byte[2];

        ConvertedNumber[0] = (byte) (numberToConvert / 256);
        ConvertedNumber[1] = (byte) (numberToConvert - (256 * (numberToConvert / 256)));

        return ConvertedNumber;
    }

    //***********************************************************************/
    //* the function Convert a "2 bytes Array" To int Format
    //*  (decimal)1876 = (hexadecimal)0754
    //* Example : Convert2bytesHexaFormatToInt {0x07, 0x54} -> returns 1876
    //***********************************************************************/
    public static int convert2bytesHexaFormatToInt(byte[] ArrayToConvert) {
        int ConvertedNumber = 0;
        if (ArrayToConvert[1] <= -1)//<0
            ConvertedNumber += ArrayToConvert[1] + 256;
        else
            ConvertedNumber += ArrayToConvert[1];

        if (ArrayToConvert[0] <= -1)//<0
            ConvertedNumber += (ArrayToConvert[0] * 256) + 256;
        else
            ConvertedNumber += ArrayToConvert[0] * 256;

        return ConvertedNumber;
    }

    //***********************************************************************/
    //* the function Convert String to an Int value
    //***********************************************************************/
    public static int convertStringToInt(String nbOfBlocks) {
        int count = 0;

        if (nbOfBlocks.length() > 2) {
            String msb = nbOfBlocks.substring(0, 2);
            String lsb = nbOfBlocks.substring(2, 4);

            count = Integer.parseInt(lsb, 16);
            count += (Integer.parseInt(msb, 16)) * 256;
        } else {
            String lsb = nbOfBlocks.substring(0, 2);
            count = Integer.parseInt(lsb, 16);
        }

        return count;
    }


    public static String[] buildArrayBlocks(byte[] addressStart, int length) {
        String array[] = new String[length];

        int add = (int) addressStart[1];

        if ((int) addressStart[1] < 0)
            add = ((int) addressStart[1] + 256);

        if ((int) addressStart[0] < 0)
            add += (256 * ((int) addressStart[0] + 256));
        else
            add += (256 * (int) addressStart[0]);

        for (int i = 0; i < length; i++) {
            if (i == 14) {
                i = 14;
            }
            array[i] = "Block  " + convertIntToHexFormatString(i + add).toUpperCase();
        }

        return array;
    }

    public static String[] buildArrayValueBlocks(byte[] ReadMultipleBlockAnswer, int length) {
        String array[] = new String[length];
        int sup = 1;

        for (int i = 0; i < length; i++) {
            array[i] = "";
            array[i] += Helper.convertHexByteToString(ReadMultipleBlockAnswer[sup]).toUpperCase();
            array[i] += " ";
            array[i] += Helper.convertHexByteToString(ReadMultipleBlockAnswer[sup + 1]).toUpperCase();
            array[i] += " ";
            array[i] += Helper.convertHexByteToString(ReadMultipleBlockAnswer[sup + 2]).toUpperCase();
            array[i] += " ";
            array[i] += Helper.convertHexByteToString(ReadMultipleBlockAnswer[sup + 3]).toUpperCase();
            sup += 4;
        }
        return array;
    }

}
