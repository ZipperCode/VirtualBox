package com.virtual.box.core.hook.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.com.android.internal.telecom.HITelecomService;

import java.util.List;

public class ITelecomServiceHookHandle extends BaseBinderHookHandle {
    public ITelecomServiceHookHandle() {
        super(Context.TELECOM_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HITelecomService.Stub.asInterface.call(getOriginBinder());
    }

    /**
     * Brings the in-call screen to the foreground if there is an active call.
     *
     * @param showDialpad if true, make the dialpad visible initially.
     */
    void showInCallScreen(MethodHandle methodHandle, boolean showDialpad, String callingPackage, String callingFeatureId){
         methodHandle.invokeOriginMethod(new Object[]{ showDialpad, hostPkg, callingFeatureId });
    }

    PhoneAccountHandle getDefaultOutgoingPhoneAccount(MethodHandle methodHandle, String uriScheme, String callingPackage,
                                                      String callingFeatureId){
        return (PhoneAccountHandle) methodHandle.invokeOriginMethod(new Object[]{ uriScheme, hostPkg, callingFeatureId });
    }

    PhoneAccountHandle getUserSelectedOutgoingPhoneAccount(MethodHandle methodHandle, String callingPackage){
        return (PhoneAccountHandle) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }


    List<PhoneAccountHandle> getCallCapablePhoneAccounts(MethodHandle methodHandle,
            boolean includeDisabledAccounts, String callingPackage, String callingFeatureId){
        return (List<PhoneAccountHandle>) methodHandle.invokeOriginMethod(new Object[]{ includeDisabledAccounts, hostPkg, callingFeatureId });
    }

    List<PhoneAccountHandle> getSelfManagedPhoneAccounts(MethodHandle methodHandle, String callingPackage,
                                                         String callingFeatureId){
        return (List<PhoneAccountHandle>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    List<PhoneAccountHandle> getPhoneAccountsSupportingScheme(MethodHandle methodHandle, String uriScheme,
                                                              String callingPackage){
        return (List<PhoneAccountHandle>) methodHandle.invokeOriginMethod(new Object[]{ uriScheme, hostPkg });
    }

    List<PhoneAccountHandle> getPhoneAccountsForPackage(MethodHandle methodHandle, String packageName){
        return (List<PhoneAccountHandle>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    PhoneAccount getPhoneAccount(MethodHandle methodHandle, PhoneAccountHandle account, String callingPackage){
        return (PhoneAccount) methodHandle.invokeOriginMethod(new Object[]{ account, hostPkg });
    }

    Object clearAccounts(MethodHandle methodHandle, String packageName){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    boolean isVoiceMailNumber(MethodHandle methodHandle, PhoneAccountHandle accountHandle, String number,
                              String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ accountHandle, number, hostPkg, callingFeatureId });
    }

    String getVoiceMailNumber(MethodHandle methodHandle, PhoneAccountHandle accountHandle, String callingPackage,
                              String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ accountHandle, hostPkg, callingFeatureId });
    }

    String getLine1Number(MethodHandle methodHandle, PhoneAccountHandle accountHandle, String callingPackage,
                          String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ accountHandle, hostPkg, callingFeatureId });
    }

    void silenceRinger(MethodHandle methodHandle, String callingPackage){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    boolean isInCall(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    boolean hasManageOngoingCallsPermission(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    boolean isInManagedCall(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    boolean isRinging(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    int getCallStateUsingPackage(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    boolean endCall(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    void acceptRingingCall(MethodHandle methodHandle, String callingPackage){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    void acceptRingingCallWithVideoState(MethodHandle methodHandle, String callingPackage, int videoState){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg, videoState });
    }

    void cancelMissedCallsNotification(MethodHandle methodHandle, String callingPackage){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    boolean handlePinMmi(MethodHandle methodHandle, String dialString, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ dialString, hostPkg });
    }

    boolean handlePinMmiForPhoneAccount(MethodHandle methodHandle, PhoneAccountHandle accountHandle, String dialString,
                                        String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ accountHandle, dialString, hostPkg });
    }

    Uri getAdnUriForPhoneAccount(MethodHandle methodHandle, PhoneAccountHandle accountHandle, String callingPackage){
        return (Uri) methodHandle.invokeOriginMethod(new Object[]{ accountHandle, hostPkg });
    }

    boolean isTtySupported(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    int getCurrentTtyMode(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    void startConference(MethodHandle methodHandle, List<Uri> participants, Bundle extras,
                         String callingPackage){
         methodHandle.invokeOriginMethod(new Object[]{ participants, extras, hostPkg });
    }

    void placeCall(MethodHandle methodHandle, Uri handle, Bundle extras, String callingPackage, String callingFeatureId){
         methodHandle.invokeOriginMethod(new Object[]{ handle, extras, hostPkg, callingFeatureId });
    }

    boolean setDefaultDialer(MethodHandle methodHandle, String packageName){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    void setTestEmergencyPhoneAccountPackageNameFilter(MethodHandle methodHandle, String packageName){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    void handleCallIntent(MethodHandle methodHandle, Intent intent, String callingPackageProxy){
         methodHandle.invokeOriginMethod(new Object[]{ intent, hostPkg });
    }


    void setTestDefaultCallRedirectionApp(MethodHandle methodHandle, String packageName){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }


    void setTestDefaultCallScreeningApp(MethodHandle methodHandle, String packageName){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    void addOrRemoveTestCallCompanionApp(MethodHandle methodHandle, String packageName, boolean isAdded){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg, isAdded });
    }

    void setSystemDialer(MethodHandle methodHandle, ComponentName testComponentName){
         methodHandle.invokeOriginMethod();
    }
    void setTestDefaultDialer(MethodHandle methodHandle, String packageName){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }
    void setTestCallDiagnosticService(MethodHandle methodHandle, String packageName){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }
}
