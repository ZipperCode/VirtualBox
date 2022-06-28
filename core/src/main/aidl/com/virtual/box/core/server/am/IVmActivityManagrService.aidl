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

    ComponentName startService(in IAppApplicationThread caller, in Intent intent, in String resolvedType,
            boolean requireForeground, in int userId);

    int stopService(in Intent intent, in String resolvedType, int userId);

    boolean stopServiceToken(in ComponentName componentName, in IBinder token, int userId);

    int prepareBindService(in Intent intent, in IBinder token, in String resulvedType,
        in IServiceConnection connection, int flags, int userId);

    int unbindService(in IServiceConnection connection, int userId);

    IBinder peekService(in Intent intent, in String resolvedType, int userId);

    Intent sendBroadcast(in Intent intent, String resolvedType, int userId);

    ParceledListSlice getRunningAppProcesses(in String callingPackage, int userId);

    ParceledListSlice getServices(in String callingPackage, int userId);

    String getCallingPackage(in IBinder token, int userId);
}