package com.ignite.boycott.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BlacklistUpdaterService extends Service {
    public BlacklistUpdaterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        //no interaction is supported with this service
        return null;
    }
}
