// IVmActivityThread.aidl
package com.virtual.box.core.server.am;

import android.os.IBinder;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

interface IVmActivityThread {
    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);
    void handleApplication();
}