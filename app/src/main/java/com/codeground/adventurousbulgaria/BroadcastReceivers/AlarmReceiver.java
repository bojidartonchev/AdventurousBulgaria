package com.codeground.adventurousbulgaria.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codeground.adventurousbulgaria.Services.LocationUpdateService;


public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final long INTERVAL_TWO_MINUTES = 2 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LocationUpdateService.class);
        //i.putExtra("foo", "bar");
        context.startService(i);
    }
}
