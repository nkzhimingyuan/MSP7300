package com.example.msp7300_client;

public class ByteAndShort {
    static byte[] U162ByteArray(short n) {
        byte [] data = new byte[2];
        data[0] = (byte)((n & 0x0000ff00) >> 8);
        data[1] = (byte)(n & 0x000000ff);
        return data;
    }
    
    static short byteArray2U16(byte []b) {
             
        short value = (short)((b[0] & 0xFF) << 8 | (b[1] & 0xFF));        
        return value;
    }    
     
    static int compareBytesData(byte []data, byte subData[], int len) {
        int i = -1;
        for(i = 0; i < len; i++) {
            if(data[i] != subData[i])
                break;
        }
        
        return (i == -1)?-1:0;
    }
}
