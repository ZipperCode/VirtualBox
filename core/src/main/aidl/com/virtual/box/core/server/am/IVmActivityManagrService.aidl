// IVmActivityManagrService.aidl
package com.virtual.box.core.server.am;

import android.os.IBinder;
import android.content.Intent;
import java.lang.String;

interface IVmActivityManagrService {

    void launchActivity(in Intent intent, int userId);

    Intent prepareStartActivity(in Intent intent, int userId);

    int startActivity(in Intent intent, in int userId);

    ComponentName startService(in Intent intent, in int userId);

}