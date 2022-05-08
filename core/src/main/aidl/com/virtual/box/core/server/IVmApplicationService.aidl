// IVmApplicationService.aidl
package com.virtual.box.core.server;

// Declare any non-default types here with import statements

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import java.util.List;
import android.content.pm.ResolveInfo;

interface IVmApplicationService {
    IBinder getSystemApplicationThread();

    IBinder getVmMainApplication();
}
