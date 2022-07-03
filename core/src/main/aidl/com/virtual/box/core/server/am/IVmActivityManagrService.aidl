// IVmActivityManagrService.aidl
package com.virtual.box.core.server.am;

import android.os.IBinder;
import android.app.IServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import java.lang.String;
import com.virtual.box.core.entity.VmAppConfig;
import com.virtual.box.core.app.IAppApplicationThread;

interface IVmActivityManagrService {

    void launchActivity(in Intent intent, int userId);

    Intent prepareStartActivity(in Intent intent, int userId);

    int startActivity(in Intent intent, in int userId);

    VmAppConfig initNewProcess(in String packageName, in String processName, int userId);

    ComponentName startService(in Intent intent, in String resolvedType,
            boolean requireForeground, in int userId);

    int stopService(in Intent intent, in String resolvedType, int userId);

    boolean stopServiceToken(in ComponentName componentName, in IBinder token, int startId, int userId);

    int bindService(IAppApplicationThread caller, in Intent intent, IBinder token,
        IServiceConnection conn,in String resolvedType, int userId);

    boolean unbindService(in IServiceConnection connection, int userId);

    IBinder peekService(in Intent intent, in String resolvedType, int userId);

    void publishService(IBinder token, in Intent intent, IBinder binder);

    Intent sendBroadcast(in Intent intent, String resolvedType, int userId);

    ParceledListSlice getRunningAppProcesses(in String callingPackage, int userId);

    ParceledListSlice getServices(in String callingPackage, int userId);

    String getCallingPackage(in IBinder token, int userId);

    void forceStopPackage(in String packageName, int userId);

    void killApplication(in String packageName, int appId, int userId, in String reason);

    void killApplicationProcess(in String processName,int uid);

    void killBackgroundProcesses(in String packageName, int userId);
}