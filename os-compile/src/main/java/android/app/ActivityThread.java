package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.UserHandle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityThread {

    public static ActivityThread currentActivityThread() {
        throw new RuntimeException("Stub!");
    }

    public static String currentPackageName() {
        throw new RuntimeException("Stub!");
    }

    public static String currentProcessName() {
        throw new RuntimeException("Stub!");
    }

    public static Application currentApplication() {
        throw new RuntimeException("Stub!");
    }

    public static void updateHttpProxy(@NonNull Context context) {
        throw new RuntimeException("Stub!");
    }

    public Handler getHandler() {
        throw new RuntimeException("Stub!");
    }
    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo,
                                          int flags) {
        throw new RuntimeException("Stub!");
    }
    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo,
                                          int flags, int userId) {
        throw new RuntimeException("Stub!");
    }

    public final LoadedApk getPackageInfo(ApplicationInfo ai, CompatibilityInfo compatInfo,
                                          int flags) {
        throw new RuntimeException("Stub!");
    }

    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai,
                                                 CompatibilityInfo compatInfo) {
        throw new RuntimeException("Stub!");
    }

    //@UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P, trackingBug = 115609023)
    public final LoadedApk peekPackageInfo(String packageName, boolean includeCode) {
        throw new RuntimeException("Stub!");
    }

    public ApplicationThread getApplicationThread() {
        throw new RuntimeException("Stub!");
    }

    public Instrumentation getInstrumentation() {
        throw new RuntimeException("Stub!");
    }

    private class ApplicationThread {

    }

    public Application getApplication() {
        throw new RuntimeException("Stub!");
    }

    public String getProcessName() {
        throw new RuntimeException("Stub!");
    }

    public final void installSystemProviders(List<ProviderInfo> providers) {
        throw new RuntimeException("Stub!");
    }

    public ActivityClientRecord getLaunchingActivity(IBinder token) {
        throw new RuntimeException("Stub!");
    }

    class H extends Handler {
        public static final int BIND_APPLICATION = 110;

        public static final int EXIT_APPLICATION = 111;

        public static final int RECEIVER = 113;

        public static final int CREATE_SERVICE = 114;

        public static final int SERVICE_ARGS = 115;

        public static final int STOP_SERVICE = 116;

        public static final int CONFIGURATION_CHANGED = 118;
        public static final int CLEAN_UP_CONTEXT = 119;

        public static final int GC_WHEN_IDLE = 120;

        public static final int BIND_SERVICE = 121;

        public static final int UNBIND_SERVICE = 122;
        public static final int DUMP_SERVICE = 123;
        public static final int LOW_MEMORY = 124;
        public static final int PROFILER_CONTROL = 127;
        public static final int CREATE_BACKUP_AGENT = 128;
        public static final int DESTROY_BACKUP_AGENT = 129;
        public static final int SUICIDE = 130;

        public static final int REMOVE_PROVIDER = 131;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;

        public static final int SCHEDULE_CRASH = 134;
        public static final int DUMP_HEAP = 135;
        public static final int DUMP_ACTIVITY = 136;
        public static final int SLEEPING = 137;
        public static final int SET_CORE_SETTINGS = 138;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;

        public static final int DUMP_PROVIDER = 141;
        public static final int UNSTABLE_PROVIDER_DIED = 142;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;

        public static final int INSTALL_PROVIDER = 145;
        public static final int ON_NEW_ACTIVITY_OPTIONS = 146;

        public static final int ENTER_ANIMATION_COMPLETE = 149;
        public static final int START_BINDER_TRACKING = 150;
        public static final int STOP_BINDER_TRACKING_AND_DUMP = 151;
        public static final int LOCAL_VOICE_INTERACTION_STARTED = 154;
        public static final int ATTACH_AGENT = 155;
        public static final int APPLICATION_INFO_CHANGED = 156;
        public static final int RUN_ISOLATED_ENTRY_POINT = 158;
        public static final int EXECUTE_TRANSACTION = 159;
        public static final int RELAUNCH_ACTIVITY = 160;
        public static final int PURGE_RESOURCES = 161;
        public static final int ATTACH_STARTUP_AGENTS = 162;
        public static final int UPDATE_UI_TRANSLATION_STATE = 163;
        public static final int SET_CONTENT_CAPTURE_OPTIONS_CALLBACK = 164;
        public static final int DUMP_GFXINFO = 165;

        public static final int INSTRUMENT_WITHOUT_RESTART = 170;
        public static final int FINISH_INSTRUMENTATION_WITHOUT_RESTART = 171;

    }

    public static final class ActivityClientRecord {

        public IBinder token;
        public IBinder assistToken;
        // A reusable token for other purposes, e.g. content capture, translation. It shouldn't be
        // used without security checks
        public IBinder shareableActivityToken;
        int ident;

        Intent intent;
        String referrer;
        // IVoiceInteractor voiceInteractor;
        Bundle state;
        PersistableBundle persistentState;

        Activity activity;
        Window window;
        Activity parent;
        String embeddedID;
        // Activity.NonConfigurationInstances lastNonConfigurationInstances;
        // TODO(lifecycler): Use mLifecycleState instead.

        boolean paused;

        boolean stopped;
        boolean hideForNow;
        Configuration createdConfig;
        Configuration overrideConfig;
        // Used to save the last reported configuration from server side so that activity
        // configuration transactions can always use the latest configuration.
        @GuardedBy("this")
        private Configuration mPendingOverrideConfig;
        // Used for consolidating configs before sending on to Activity.
        private Configuration tmpConfig = new Configuration();
        // Callback used for updating activity override config.
        //ViewRootImpl.ActivityConfigCallback configCallback;
        ActivityClientRecord nextIdle;

        // Indicates whether this activity is currently the topmost resumed one in the system.
        // This holds the last reported value from server.
        boolean isTopResumedActivity;
        // This holds the value last sent to the activity. This is needed, because an update from
        // server may come at random time, but we always need to report changes between ON_RESUME
        // and ON_PAUSE to the app.
        boolean lastReportedTopResumedState;

        //ProfilerInfo profilerInfo;


        ActivityInfo activityInfo;

        CompatibilityInfo compatInfo;

        public LoadedApk packageInfo;

        //List<ResultInfo> pendingResults;
        //List<ReferrerIntent> pendingIntents;

        boolean startsNotResumed;
        //public final boolean isForward;
        int pendingConfigChanges;
        // Whether we are in the process of performing on user leaving.
        boolean mIsUserLeaving;

        Window mPendingRemoveWindow;
        WindowManager mPendingRemoveWindowManager;

        boolean mPreserveWindow;

        /**
         * The options for scene transition.
         */
        ActivityOptions mActivityOptions;

        /**
         * If non-null, the activity is launching with a specified rotation, the adjustments should
         * be consumed before activity creation.
         */
        //FixedRotationAdjustments mPendingFixedRotationAdjustments;

        /**
         * Whether this activiy was launched from a bubble.
         */
        boolean mLaunchedFromBubble;

        //private int mLifecycleState = PRE_ON_CREATE;

        //private SizeConfigurationBuckets mSizeConfigurations;

    }
}
