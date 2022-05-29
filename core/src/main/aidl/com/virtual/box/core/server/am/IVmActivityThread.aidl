// IVmActivityThread.aidl
package com.virtual.box.core.server.am;

import android.os.IBinder;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

interface IVmActivityThread {
    IBinder getVmActivityThread();
    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);
    void handleApplication();
}