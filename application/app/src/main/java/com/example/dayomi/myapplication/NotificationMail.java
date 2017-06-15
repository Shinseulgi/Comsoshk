package com.example.dayomi.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Dayomi on 2017-05-18.
 */
public class NotificationMail extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
        Bundle extras = getIntent().getExtras();
        String url = extras.getString("web");
        WebView wb = (WebView)findViewById(R.id.wb);
        wb.loadUrl(url);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1234);
    }
}
