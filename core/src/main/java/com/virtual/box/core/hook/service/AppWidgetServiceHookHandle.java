package com.virtual.box.core.hook.service;

import android.app.IServiceConnection;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.VirtualBox;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.core.manager.ServiceManager;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.com.android.internal.appwidget.HIAppWidgetService;

public class AppWidgetServiceHookHandle extends BaseBinderHookHandle {

    private final String hostPkg = VirtualBox.get().getHostPkg();

    public AppWidgetServiceHookHandle() {
        super(Context.APPWIDGET_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.APPWIDGET_SERVICE);
        return HIAppWidgetService.Stub.asInterface.call(binder);
    }

    @Override
    public boolean isHooked() {
        return HServiceManager.getService.call(Context.APPWIDGET_SERVICE) == this;
    }

    /**
     * @param host IAppWidgetHost
     */
    ParceledListSlice<?> startListening(MethodHandle methodHandle, IInterface host, String callingPackage, int hostId,
                                        int[] appWidgetIds) {
        return (ParceledListSlice<?>) methodHandle.invokeOriginMethod(new Object[]{host, hostPkg, hostId, appWidgetIds});
    }

    void stopListening(MethodHandle methodHandle, String callingPackage, int hostId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, hostId});
    }

    int allocateAppWidgetId(MethodHandle methodHandle, String callingPackage, int hostId) {
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg, hostId});
    }

    void deleteAppWidgetId(MethodHandle methodHandle, String callingPackage, int appWidgetId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId});
    }

    void deleteHost(MethodHandle methodHandle, String packageName, int hostId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, hostId});
    }

    /**
     * @deprecated maxTargetSdk = 30
     */
    RemoteViews getAppWidgetViews(MethodHandle methodHandle, String callingPackage, int appWidgetId) {
        return (RemoteViews) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId});
    }

    int[] getAppWidgetIdsForHost(MethodHandle methodHandle, String callingPackage, int hostId) {
        return (int[]) methodHandle.invokeOriginMethod(new Object[]{hostPkg, hostId});
    }

    IntentSender createAppWidgetConfigIntentSender(MethodHandle methodHandle, String callingPackage, int appWidgetId,
                                                   int intentFlags) {
        return (IntentSender) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId, intentFlags});
    }

    //
    // for AppWidgetManager
    //
    void updateAppWidgetIds(MethodHandle methodHandle, String callingPackage, int[] appWidgetIds, RemoteViews views) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetIds, views});
    }

    void updateAppWidgetOptions(MethodHandle methodHandle, String callingPackage, int appWidgetId, Bundle extras) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId, extras});
    }

    Bundle getAppWidgetOptions(MethodHandle methodHandle, String callingPackage, int appWidgetId) {
        return (Bundle) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId});
    }

    void partiallyUpdateAppWidgetIds(MethodHandle methodHandle, String callingPackage, int[] appWidgetIds,
                                     RemoteViews views) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetIds, views});
    }

    void updateAppWidgetProvider(MethodHandle methodHandle, ComponentName provider, RemoteViews views) {
        ComponentName newComponentName = new ComponentName(provider.getPackageName(), provider.getClassName());
        methodHandle.invokeOriginMethod(new Object[]{newComponentName, views});
    }

    void updateAppWidgetProviderInfo(MethodHandle methodHandle, ComponentName provider, String metadataKey) {
        ComponentName newComponentName = new ComponentName(provider.getPackageName(), provider.getClassName());
        methodHandle.invokeOriginMethod(new Object[]{newComponentName, metadataKey});
    }

    void notifyAppWidgetViewDataChanged(MethodHandle methodHandle, String packageName, int[] appWidgetIds, int viewId) {
        methodHandle.invokeOriginMethod(new Object[]{packageName, appWidgetIds, viewId});
    }

    ParceledListSlice<?> getInstalledProvidersForProfile(MethodHandle methodHandle, int categoryFilter, int profileId,
                                                         String packageName) {
        return (ParceledListSlice<?>) methodHandle.invokeOriginMethod(new Object[]{categoryFilter, profileId, hostPkg});
    }

    AppWidgetProviderInfo getAppWidgetInfo(MethodHandle methodHandle, String callingPackage, int appWidgetId) {
        return (AppWidgetProviderInfo) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId});
    }

    boolean hasBindAppWidgetPermission(MethodHandle methodHandle, String packageName, int userId) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    void setBindAppWidgetPermission(MethodHandle methodHandle, String packageName, int userId, boolean permission) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId, permission});
    }

    /**
     * @deprecated maxTargetSdk = 30
     */
    @Deprecated
    boolean bindAppWidgetId(MethodHandle methodHandle, String callingPackage, int appWidgetId,
                            int providerProfileId, ComponentName providerComponent, Bundle options) {
        ComponentName componentName = new ComponentName(providerComponent.getPackageName(), providerComponent.getClassName());
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId, providerProfileId, componentName, options});
    }

    /**
     * @param caller IApplicationThread
     * @deprecated maxTargetSdk = 30
     */
    @Deprecated
    boolean bindRemoteViewsService(MethodHandle methodHandle, String callingPackage, int appWidgetId, Intent intent,
                                   Object caller, IBinder token, IServiceConnection connection, int flags) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId, intent, caller, token, connection, flags});
    }

    int[] getAppWidgetIds(MethodHandle methodHandle, ComponentName providerComponent) {
        ComponentName componentName = new ComponentName(providerComponent.getPackageName(), providerComponent.getClassName());
        return (int[]) methodHandle.invokeOriginMethod(new Object[]{componentName});
    }

    boolean isBoundWidgetPackage(MethodHandle methodHandle, String packageName, int userId) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    boolean requestPinAppWidget(MethodHandle methodHandle, String packageName, ComponentName providerComponent,
                                Bundle extras, IntentSender resultIntent) {
        ComponentName componentName = new ComponentName(providerComponent.getPackageName(), providerComponent.getClassName());
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, componentName, extras, resultIntent});
    }

    void noteAppWidgetTapped(MethodHandle methodHandle, String callingPackage, int appWidgetId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, appWidgetId});
    }
}
