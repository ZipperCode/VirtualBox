package com.virtual.box.core.hook.service;

import android.app.AutomaticZenRule;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.service.notification.Condition;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import com.virtual.box.base.util.log.L;
import com.virtual.box.base.util.log.Logger;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.app.HINotificationManager;

import java.util.List;

public class INotificationManagerHookHandle extends BaseBinderHookHandle {

    private final Logger logger = Logger.getLogger(L.HOOK_TAG, "INotificationManagerHookHandle");

    public INotificationManagerHookHandle() {
        super(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected Object getOriginObject() {
        return HINotificationManager.Stub.asInterface.call(getOriginBinder());
    }

    void cancelAllNotifications(MethodHandle methodHandle, String pkg, int userId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    void clearData(MethodHandle methodHandle, String pkg, int uid, boolean fromApp) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, fromApp});
    }

    /**
     * @param callback ITransientNotificationCallback
     */
    void enqueueTextToast(MethodHandle methodHandle, String pkg, IBinder token, CharSequence text,
                          int duration, int displayId, Object callback) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, token, text, duration, displayId, callback
        });
    }

    /**
     * @param callback ITransientNotification
     */
    void enqueueToast(MethodHandle methodHandle, String pkg, IBinder token, Object callback,
                      int duration, int displayId) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, token, callback, duration, displayId
        });
    }

    void cancelToast(MethodHandle methodHandle, String pkg, IBinder token) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, token});
    }

    void finishToken(MethodHandle methodHandle, String pkg, IBinder token) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, token
        });
    }

    void enqueueNotificationWithTag(MethodHandle methodHandle, String pkg, String opPkg, String tag, int id,
                                    Notification notification, int userId) {
        logger.i("enqueueNotificationWithTag#pks = %s, opPkg = %s", pkg, opPkg);
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, hostPkg, tag, id, notification, userId
        });
    }

    void cancelNotificationWithTag(MethodHandle methodHandle, String pkg, String opPkg, String tag, int id, int userId) {
        logger.i("cancelNotificationWithTag#pks = %s, opPkg = %s", pkg, opPkg);
        methodHandle.invokeOriginMethod(new Object[]{
                pkg, opPkg, tag, id, userId
        });
    }

    void setShowBadge(MethodHandle methodHandle, String pkg, int uid, boolean showBadge) {
        methodHandle.invokeOriginMethod(new Object[]{pkg, uid, showBadge});
    }

    boolean canShowBadge(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    boolean hasSentValidMsg(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    boolean isInInvalidMsgState(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    boolean hasUserDemotedInvalidMsgApp(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    void setInvalidMsgAppDemoted(MethodHandle methodHandle, String pkg, int uid, boolean isDemoted) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, isDemoted});
    }

    void setNotificationsEnabledForPackage(MethodHandle methodHandle, String pkg, int uid, boolean enabled) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, enabled});
    }

    /**
     * Updates the notification's enabled state. Additionally locks importance for all of the
     * notifications belonging to the app, such that future notifications aren't reconsidered for
     * blocking helper.
     */
    void setNotificationsEnabledWithImportanceLockForPackage(MethodHandle methodHandle,
                                                             String pkg, int uid, boolean enabled) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, enabled});
    }


    boolean areNotificationsEnabledForPackage(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    boolean areNotificationsEnabled(MethodHandle methodHandle, String pkg) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    int getPackageImportance(MethodHandle methodHandle, String pkg) {
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    List<String> getAllowedAssistantAdjustments(MethodHandle methodHandle, String pkg) {
        return (List<String>) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    boolean shouldHideSilentStatusIcons(MethodHandle methodHandle, String callingPkg) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    void setBubblesAllowed(MethodHandle methodHandle, String pkg, int uid, int bubblePreference) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, bubblePreference});
    }

    boolean areBubblesAllowed(MethodHandle methodHandle, String pkg) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    int getBubblePreferenceForPackage(MethodHandle methodHandle, String pkg, int uid) {
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    void createNotificationChannelGroups(MethodHandle methodHandle, String pkg, ParceledListSlice channelGroupList) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, channelGroupList});
    }

    void createNotificationChannels(MethodHandle methodHandle, String pkg, ParceledListSlice channelsList) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, channelsList});
    }

    void createNotificationChannelsForPackage(MethodHandle methodHandle, String pkg, int uid, ParceledListSlice channelsList) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, channelsList});
    }

    ParceledListSlice getConversationsForPackage(MethodHandle methodHandle, String pkg, int uid) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid});
    }

    ParceledListSlice getNotificationChannelGroupsForPackage(MethodHandle methodHandle, String pkg,
                                                             int uid, boolean includeDeleted) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, uid, includeDeleted
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannelGroup getNotificationChannelGroupForPackage(MethodHandle methodHandle,
                                                                   String groupId, String pkg, int uid) {
        return (NotificationChannelGroup) methodHandle.invokeOriginMethod(new Object[]{
                groupId, hostPkg, uid
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannelGroup getPopulatedNotificationChannelGroupForPackage(
            MethodHandle methodHandle,
            String pkg, int uid, String groupId, boolean includeDeleted) {
        return (NotificationChannelGroup) methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, groupId, includeDeleted});
    }

    void updateNotificationChannelGroupForPackage(MethodHandle methodHandle, String pkg, int uid, NotificationChannelGroup group) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, group});
    }

    void updateNotificationChannelForPackage(MethodHandle methodHandle, String pkg, int uid, NotificationChannel channel) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, channel});
    }

    void unlockNotificationChannel(MethodHandle methodHandle, String pkg, int uid, String channelId) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, uid, channelId});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannel getNotificationChannel(MethodHandle methodHandle, String callingPkg, int userId, String pkg, String channelId) {
        return (NotificationChannel) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId, pkg, channelId});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannel getConversationNotificationChannel(MethodHandle methodHandle, String callingPkg, int userId, String pkg,
                                                           String channelId, boolean returnParentIfNoConversationChannel, String conversationId) {
        return (NotificationChannel) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId, pkg, channelId, returnParentIfNoConversationChannel, conversationId});
    }

    void createConversationNotificationChannelForPackage(MethodHandle methodHandle, String pkg, int uid,
                                                         NotificationChannel parentChannel, String conversationId) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, uid, parentChannel, conversationId
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannel getNotificationChannelForPackage(MethodHandle methodHandle, String pkg, int uid,
                                                         String channelId, String conversationId, boolean includeDeleted) {
        return (NotificationChannel) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, uid, channelId, conversationId, includeDeleted
        });
    }

    void deleteNotificationChannel(MethodHandle methodHandle, String pkg, String channelId) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, channelId
        });
    }

    ParceledListSlice getNotificationChannels(MethodHandle methodHandle, String callingPkg, String targetPkg, int userId) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, targetPkg, userId
        });
    }

    ParceledListSlice getNotificationChannelsForPackage(MethodHandle methodHandle, String pkg, int uid, boolean includeDeleted) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    int getNumNotificationChannelsForPackage(MethodHandle methodHandle, String pkg, int uid, boolean includeDeleted) {
        return (int) methodHandle.invokeOriginMethod();
    }

    int getDeletedChannelCount(MethodHandle methodHandle, String pkg, int uid) {
        return (int) methodHandle.invokeOriginMethod();
    }

    int getBlockedChannelCount(MethodHandle methodHandle, String pkg, int uid) {
        return (int) methodHandle.invokeOriginMethod();
    }

    void deleteNotificationChannelGroup(MethodHandle methodHandle, String pkg, String channelGroupId) {
        methodHandle.invokeOriginMethod();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannelGroup getNotificationChannelGroup(MethodHandle methodHandle, String pkg, String channelGroupId) {
        return (NotificationChannelGroup) methodHandle.invokeOriginMethod();
    }

    ParceledListSlice getNotificationChannelGroups(MethodHandle methodHandle, String pkg) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    boolean onlyHasDefaultChannel(MethodHandle methodHandle, String pkg, int uid) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    ParceledListSlice getNotificationChannelsBypassingDnd(MethodHandle methodHandle, String pkg, int userId) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    boolean isPackagePaused(MethodHandle methodHandle, String pkg) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    void deleteNotificationHistoryItem(MethodHandle methodHandle, String pkg, int uid, long postedTime) {
        methodHandle.invokeOriginMethod();
    }

    StatusBarNotification[] getActiveNotifications(MethodHandle methodHandle, String callingPkg) {
        return (StatusBarNotification[]) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg
        });
    }

    StatusBarNotification[] getActiveNotificationsWithAttribution(MethodHandle methodHandle, String callingPkg,
                                                                  String callingAttributionTag) {
        return (StatusBarNotification[]) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callingAttributionTag
        });
    }

    StatusBarNotification[] getHistoricalNotifications(MethodHandle methodHandle, String callingPkg, int count, boolean includeSnoozed) {
        return (StatusBarNotification[]) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, count, includeSnoozed
        });
    }

    StatusBarNotification[] getHistoricalNotificationsWithAttribution(MethodHandle methodHandle, String callingPkg,
                                                                      String callingAttributionTag, int count, boolean includeSnoozed) {
        return (StatusBarNotification[]) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callingAttributionTag, count, includeSnoozed
        });
    }

    /**
     * @return NotificationHistory
     */
    Object getNotificationHistory(MethodHandle methodHandle, String callingPkg, String callingAttributionTag) {
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingAttributionTag});
    }

    /**
     * @param listener INotificationListener
     */
    void registerListener(MethodHandle methodHandle, Object listener, ComponentName component, int userid) {
        methodHandle.invokeOriginMethod();
    }
    /**
     * @param listener INotificationListener
     */
    void unregisterListener(MethodHandle methodHandle, Object listener, int userid) {
        methodHandle.invokeOriginMethod();
    }
    /**
     * @param token INotificationListener
     */
    void cancelNotificationFromListener(MethodHandle methodHandle, Object token, String pkg, String tag, int id) {
        methodHandle.invokeOriginMethod();
    }
    /**
     * @param token INotificationListener
     */
    void cancelNotificationsFromListener(MethodHandle methodHandle, Object token, String[] keys) {
        methodHandle.invokeOriginMethod();
    }

    void snoozeNotificationUntilContextFromListener(MethodHandle methodHandle, Object token, String key, String snoozeCriterionId) {
        methodHandle.invokeOriginMethod();
    }
    
    void snoozeNotificationUntilFromListener(MethodHandle methodHandle, Object token, String key, long until) {
        methodHandle.invokeOriginMethod();
    }

    void requestBindListener(MethodHandle methodHandle, ComponentName component) {
        methodHandle.invokeOriginMethod();
    }

    void requestUnbindListener(MethodHandle methodHandle, Object token) {
        methodHandle.invokeOriginMethod();
    }

    void requestBindProvider(MethodHandle methodHandle, ComponentName component) {
        methodHandle.invokeOriginMethod();
    }

    /**
     * @param token IConditionProvider
     */
    void requestUnbindProvider(MethodHandle methodHandle, Object token) {
        methodHandle.invokeOriginMethod();
    }

    void setNotificationsShownFromListener(MethodHandle methodHandle, Object token, String[] keys) {
        methodHandle.invokeOriginMethod();
    }

    void setInterruptionFilter(MethodHandle methodHandle, String pkg, int interruptionFilter) {
        methodHandle.invokeOriginMethod();
    }

    void updateNotificationChannelGroupFromPrivilegedListener(MethodHandle methodHandle, Object token, String pkg, UserHandle user, NotificationChannelGroup group) {
        methodHandle.invokeOriginMethod();
    }

    void updateNotificationChannelFromPrivilegedListener(MethodHandle methodHandle, Object token, String pkg, UserHandle user, NotificationChannel channel) {
        methodHandle.invokeOriginMethod();
    }

    ParceledListSlice getNotificationChannelsFromPrivilegedListener(MethodHandle methodHandle, Object token, String pkg, UserHandle user) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    ParceledListSlice getNotificationChannelGroupsFromPrivilegedListener(MethodHandle methodHandle, Object token, String pkg, UserHandle user) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    boolean isNotificationListenerAccessGranted(MethodHandle methodHandle, ComponentName listener) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    boolean isNotificationListenerAccessGrantedForUser(MethodHandle methodHandle, ComponentName listener, int userId) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    boolean isNotificationAssistantAccessGranted(MethodHandle methodHandle, ComponentName assistant) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    void setNotificationListenerAccessGranted(MethodHandle methodHandle, ComponentName listener, boolean enabled, boolean userSet) {
        methodHandle.invokeOriginMethod();
    }

    void setNotificationAssistantAccessGranted(MethodHandle methodHandle, ComponentName assistant, boolean enabled) {
        methodHandle.invokeOriginMethod();
    }

    void setNotificationListenerAccessGrantedForUser(MethodHandle methodHandle, ComponentName listener, int userId, boolean enabled, boolean userSet) {
        methodHandle.invokeOriginMethod();
    }

    void setNotificationAssistantAccessGrantedForUser(MethodHandle methodHandle, ComponentName assistant, int userId, boolean enabled) {
        methodHandle.invokeOriginMethod();
    }

    boolean hasEnabledNotificationListener(MethodHandle methodHandle, String packageName, int userId) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    void notifyConditions(MethodHandle methodHandle, String pkg, Object provider, Object[] conditions) {
        methodHandle.invokeOriginMethod();
    }

    boolean isNotificationPolicyAccessGranted(MethodHandle methodHandle, String pkg) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    NotificationManager.Policy getNotificationPolicy(MethodHandle methodHandle, String pkg) {
        return (NotificationManager.Policy) methodHandle.invokeOriginMethod();
    }

    void setNotificationPolicy(MethodHandle methodHandle, String pkg, NotificationManager.Policy policy) {
        methodHandle.invokeOriginMethod();
    }

    boolean isNotificationPolicyAccessGrantedForPackage(MethodHandle methodHandle, String pkg) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    void setNotificationPolicyAccessGranted(MethodHandle methodHandle, String pkg, boolean granted) {
        methodHandle.invokeOriginMethod();
    }

    void setNotificationPolicyAccessGrantedForUser(MethodHandle methodHandle, String pkg, int userId, boolean granted) {
        methodHandle.invokeOriginMethod();
    }


    String addAutomaticZenRule(MethodHandle methodHandle, AutomaticZenRule automaticZenRule, String pkg) {
        return (String) methodHandle.invokeOriginMethod();
    }

    boolean removeAutomaticZenRules(MethodHandle methodHandle, String packageName) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    int getRuleInstanceCount(MethodHandle methodHandle, ComponentName owner) {
        return (int) methodHandle.invokeOriginMethod();
    }

    ParceledListSlice getAppActiveNotifications(MethodHandle methodHandle, String callingPkg, int userId) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    void setNotificationDelegate(MethodHandle methodHandle, String callingPkg, String delegate) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, delegate});
    }

    String getNotificationDelegate(MethodHandle methodHandle, String callingPkg) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    boolean canNotifyAsPackage(MethodHandle methodHandle, String callingPkg, String targetPkg, int userId) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, targetPkg, userId});
    }

}
