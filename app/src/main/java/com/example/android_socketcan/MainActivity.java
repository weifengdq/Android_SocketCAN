package com.example.android_socketcan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    android_socketcan can0;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textview0);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());

        can0 = new android_socketcan();
        can0.fd = can0.socketcanOpen("can0");

        //send
        new Thread() {
            int[] data = {0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7};
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    data[0] = (data[0] + 1) % 0xFF;
                    can0.socketcanWrite(can0.fd, 0x123, 0, 0, 8, data);
                }
            }
        }.start();

        //receive
        new Thread() {
            long[] ret = new long[12];
            @Override
            public void run() {
                while (true) {
                    ret = can0.socketcanRead(can0.fd);
                    long can0id = ret[0];
                    long can0eff = ret[1];
                    long can0rtr = ret[2];
                    long can0len = ret[3];
                    long[] can0data = Arrays.copyOfRange(ret, 4, (int) (4+can0len));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //can0  RX E R  123   [8]  CF A1 A2 A3 A4 A5 A6 A7
                            String str = "can0  RX ";
                            str += (can0eff==0) ? "S " : "E ";
                            str += (can0rtr==0) ? "-  " : "R  ";
                            String strid = Long.toHexString(can0id);
                            if(can0eff == 0) {
                                for(int i=0; i<3-strid.length(); i++) {
                                    strid = '0' + strid;
                                }
                            } else {
                                for(int i=0; i<8-strid.length(); i++) {
                                    strid = '0' + strid;
                                }
                            }
                            str = str + strid + "   [" + Long.toString(can0len) + "]  ";
                            for(int i=0; i<can0len; i++) {
                                String hex = Long.toHexString(can0data[i]);
                                hex = (hex.length()==1) ? ('0'+hex) : hex;
                                str = str + ' ' + hex;
                            }
                            str = str.toUpperCase();
                            str += '\n';

                            if(tv.getLineCount() > 1000) {
                                tv.setText("");
                            }
                            tv.append(str);
                            int offset = tv.getLineCount() * tv.getLineHeight();
                            if(offset > tv.getHeight()) {
                                tv.scrollTo(0, offset - tv.getHeight());
                            }
                        }
                    });
                }
            }
        }.start();

    }
}