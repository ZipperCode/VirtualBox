// IVmActivityManagrService.aidl
package com.virtual.box.core.server.am;

import android.os.IBinder;
import android.content.Intent;
import java.lang.String;
import com.virtual.box.core.entity.VmAppConfig;

interface IVmActivityManagrService {

    void launchActivity(in Intent intent, int userId);

    Intent prepareStartActivity(in Intent intent, int userId);

    int startActivity(in Intent intent, in int userId);

    VmAppConfig initNewProcess(in String packageName, in String processName, int userId);

    ComponentName startService(in Intent intent, in int userId);

}