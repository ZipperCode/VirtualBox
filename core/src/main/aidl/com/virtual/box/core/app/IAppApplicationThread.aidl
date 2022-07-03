// IAppApplicationThread.aidl
package com.virtual.box.core.app;

// Declare any non-default types here with import statements
import com.virtual.box.core.entity.VmAppConfig;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

interface IAppApplicationThread {

    VmAppConfig getVmAppConfig();

    void attachApplication(in VmAppConfig appConfig);

    void scheduleCreateService(IBinder token, in ServiceInfo serviceInfo, in Intent intent);

    void scheduleServiceArgs(IBinder token, int startId, in Intent intent);

    void schduleStopService(in IBinder token, in Intent intent);

    void scheduleBindService(IBinder token, in Intent intent, boolean rebind);

    void scheduleUnbindService(IBinder token, in Intent intent);

    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);

    void finishActivity(IBinder token);
}
