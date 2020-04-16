package com.android.dogs.view;


import android.os.Handler;
import android.os.Message;

public class CustomThread implements Runnable {
    //public static i
    Handler handler;
    @Override
    public void run() {
        for(int i =0; i<=10;i++){
            System.out.println(i);
            Message m = new Message();
            m.what = 100;
            handler.sendMessage(new Message());
        }
    }

    public void setCallback(Handler handler){
        this.handler = handler;
    }
}
