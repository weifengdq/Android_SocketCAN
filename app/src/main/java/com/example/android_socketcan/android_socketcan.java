package com.example.android_socketcan;

public class android_socketcan {
    static {
        System.loadLibrary("android_socketcan");
    }

    public int fd;

    public native int socketcanOpen(String canx);   //return fd
    public native int socketcanClose(int fd);       //return 0 is success
    public native int socketcanWrite(int fd, long canid, long eff, long rtr, int len, int[] data);
    public native long[] socketcanRead(int fd);
}
