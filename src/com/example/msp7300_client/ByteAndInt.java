package com.example.msp7300_client;

public class ByteAndInt {
    static byte[] int2ByteArray(int n) {
        byte [] data = new byte[4];
        data[0] = (byte)((n & 0xff000000) >> 24);
        data[1] = (byte)((n & 0x00ff0000) >> 16);
        data[2] = (byte)((n & 0x0000ff00) >> 8);
        data[3] = (byte)(n & 0x000000ff);
        return data;
    }
    
    static int byteArray2Int(byte []b) {
             
        int value = ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | (b[3] & 0xFF);        
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

