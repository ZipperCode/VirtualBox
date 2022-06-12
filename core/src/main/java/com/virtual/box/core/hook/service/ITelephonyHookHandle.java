package com.virtual.box.core.hook.service;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.WorkSource;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.ServiceState;
import android.telephony.UiccCardInfo;
import android.telephony.VisualVoicemailSmsFilterSettings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.com.android.internal.telephony.HITelephony;

import java.util.List;
import java.util.Map;

public class ITelephonyHookHandle extends BaseBinderHookHandle {
    public ITelephonyHookHandle() {
        super(Context.TELEPHONY_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HITelephony.Stub.asInterface.call(getOriginBinder());
    }

    /**
     * Place a call to the specified number.
     * @param callingPackage The package making the call.
     * @param number the number to be called.
     */
    void call(MethodHandle methodHandle, String callingPackage, String number){
        methodHandle.invokeOriginMethod(new Object[]{ hostPkg, number });
    }

    /** @deprecated Use { #isRadioOnWithFeature(String, String) instead */
    boolean isRadioOn(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Check to see if the radio is on or not.
     * @param callingPackage the name of the package making the call.
     * @param callingFeatureId The feature the package.
     * @return returns true if the radio is on.
     */
    boolean isRadioOnWithFeature(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * @deprecated Use { #isRadioOnForSubscriberWithFeature(int, String, String) instead
     */
    boolean isRadioOnForSubscriber(MethodHandle methodHandle, int subId, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }

    /**
     * Check to see if the radio is on or not on particular subId.
     * @param subId user preferred subId.
     * @param callingPackage the name of the package making the call.
     * @param callingFeatureId The feature the package.
     * @return returns true if the radio is on.
     */
    boolean isRadioOnForSubscriberWithFeature(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }
    /**
     * Version of updateServiceLocation that records the caller and validates permissions.
     * @return
     */
    Object updateServiceLocationWithPackageName(MethodHandle methodHandle, String callingPkg){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Allow mobile data connections.
     */

    boolean enableDataConnectivity(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Disallow mobile data connections.
     */

    boolean disableDataConnectivity(MethodHandle methodHandle, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    CellIdentity getCellLocation(MethodHandle methodHandle, String callingPkg, String callingFeatureId){
        return (CellIdentity) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the neighboring cell information of the device.
     */
    List<NeighboringCellInfo> getNeighboringCellInfo(MethodHandle methodHandle, String callingPkg, String callingFeatureId){
        return (List<NeighboringCellInfo>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }


    /**
     * Returns the call state for a specific subscriiption.
     */
    int getCallStateForSubscription(MethodHandle methodHandle, int subId, String callingPackage, String featureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, featureId });
    }

    /**
     * Returns the CDMA ERI icon index to display
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getCdmaEriIconIndex(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the CDMA ERI icon index to display on particular subId.
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getCdmaEriIconIndexForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                         String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the CDMA ERI icon mode,
     * 0 - ON
     * 1 - FLASHING
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getCdmaEriIconMode(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the CDMA ERI icon mode on particular subId,
     * 0 - ON
     * 1 - FLASHING
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getCdmaEriIconModeForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                        String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the CDMA ERI text,
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    String getCdmaEriText(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the CDMA ERI text for particular subId,
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    String getCdmaEriTextForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the voice activation state for a particular subscriber.
     * @param subId user preferred sub
     * @param callingPackage package queries voice activation state
     */
    int getVoiceActivationState(MethodHandle methodHandle, int subId, String callingPackage){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }

    /**
     * Returns the data activation state for a particular subscriber.
     * @param subId user preferred sub
     * @param callingPackage package queris data activation state
     */
    int getDataActivationState(MethodHandle methodHandle, int subId, String callingPackage){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }

    /**
     * Returns the unread count of voicemails for a subId.
     * @param subId user preferred subId.
     * Returns the unread count of voicemails
     */
    int getVoiceMessageCountForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                          String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }


    Bundle getVisualVoicemailSettings(MethodHandle methodHandle, String callingPackage, int subId){
        return (Bundle) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, subId });
    }

    String getVisualVoicemailPackageName(MethodHandle methodHandle, String callingPackage, String callingFeatureId, int subId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId, subId });
    }

    // Not , caller needs to make sure the vaule is set before receiving a SMS
    Object enableVisualVoicemailSmsFilter(MethodHandle methodHandle, String callingPackage, int subId,
                                          VisualVoicemailSmsFilterSettings settings){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg, subId, settings });
    }

    Object disableVisualVoicemailSmsFilter(MethodHandle methodHandle, String callingPackage, int subId){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg, subId });
    }

    // Get settings set by the calling package
    @RequiresApi(api = Build.VERSION_CODES.O)
    VisualVoicemailSmsFilterSettings getVisualVoicemailSmsFilterSettings(MethodHandle methodHandle, String callingPackage,
                                                                         int subId){
        return (VisualVoicemailSmsFilterSettings) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, subId });
    }

    /**
     * Send a visual voicemail SMS. Internal use only.
     * Requires caller to be the default dialer and have SEND_SMS permission
     * @return
     */
    Object sendVisualVoicemailSmsForSubscriber(MethodHandle methodHandle, String callingPackage, String callingAttributeTag,
                                               int subId, String number, int port, String text, PendingIntent sentIntent){
        return methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callingAttributeTag, subId, number, port, text, sentIntent
        });
    }

    // Send the special dialer code. The IPC caller must be the current default dialer.
    Object sendDialerSpecialCode(MethodHandle methodHandle, String callingPackageName, String inputCode){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg, inputCode });
    }

    /**
     * Returns the network type of a subId.
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getNetworkTypeForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the network type for data transmission
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getDataNetworkType(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the data network type of a subId
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     */
    int getDataNetworkTypeForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                        String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the voice network type of a subId
     * @param subId user preferred subId.
     * @param callingPackage package making the call.getLteOnCdmaMode
     * @param callingFeatureId The feature the package.
     * Returns the network type
     */
    int getVoiceNetworkTypeForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                         String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @param callingPackage the name of the calling package
     * @param callingFeatureId The feature the package.
     * @return { Phone#LTE_ON_CDMA_UNKNOWN}, { Phone#LTE_ON_CDMA_FALSE}
     * or { PHone#LTE_ON_CDMA_TRUE}
     */
    int getLteOnCdmaMode(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @param callingPackage the name of the calling package
     * @param callingFeatureId The feature the package.
     * @return { Phone#LTE_ON_CDMA_UNKNOWN}, { Phone#LTE_ON_CDMA_FALSE}
     * or { PHone#LTE_ON_CDMA_TRUE}
     */
    int getLteOnCdmaModeForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns all observed cell information of the device.
     */
    List<CellInfo> getAllCellInfo(MethodHandle methodHandle, String callingPkg, String callingFeatureId){
        return (List<CellInfo>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Request a cell information update for the specified subscription,
     * reported via the CellInfoCallback.
     * @return
     */
    Object requestCellInfoUpdate(MethodHandle methodHandle, int subId, Object cb, String callingPkg,
                                 String callingFeatureId){
        return methodHandle.invokeOriginMethod(new Object[]{ subId, cb, hostPkg, callingFeatureId });
    }

    /**
     * Request a cell information update for the specified subscription,
     * reported via the CellInfoCallback.
     *
     * @param ws the requestor to whom the power consumption for this should be attributed.
     * @return
     */
    Object requestCellInfoUpdateWithWorkSource(MethodHandle methodHandle, int subId, Object cb,
                                               String callingPkg, String callingFeatureId, WorkSource ws){
        return methodHandle.invokeOriginMethod(new Object[]{ subId, cb, hostPkg, callingFeatureId, ws });
    }

    /**
     * Transmit an APDU to the ICC card over the basic channel using the physical slot index and port index.
     *
     * Input parameters equivalent to TS 27.007 AT+CSIM command.
     *
     * @param slotIndex The physical slot index of the target ICC card
     * @param portIndex The unique index referring to a port belonging to the SIM slot
     * @param callingPackage the name of the package making the call.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduBasicChannelByPort(MethodHandle methodHandle, int slotIndex, int portIndex, String callingPackage, int cla,
                                             int instruction, int p1, int p2, int p3, String data){
        return (String) methodHandle.invokeOriginMethod(new Object[]{
                slotIndex, portIndex, hostPkg, cla, instruction, p1, p2, p3, data
        });
    }

    /**
     * Transmit an APDU to the ICC card over the basic channel.
     *
     * Input parameters equivalent to TS 27.007 AT+CSIM command.
     *
     * @param subId The subscription to use.
     * @param callingPackage the name of the package making the call.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduBasicChannel(MethodHandle methodHandle, int subId, String callingPackage, int cla, int instruction,
                                       int p1, int p2, int p3, String data){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, cla, instruction, p1, p2, p3, data });
    }

    /**
     *  @return true if the ImsService to bind to for the slot id specified was set, false otherwise.
     */
    boolean setBoundImsServiceOverride(MethodHandle methodHandle, int slotIndex, boolean isCarrierService,
                                       int[] featureTypes, String packageName){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, isCarrierService, featureTypes, hostPkg });
    }

    /**
     * Perform a radio scan and return the list of avialble networks.
     *
     * @param subId the id of the subscription.
     * @param callingPackage the calling package
     * @param callingFeatureId The feature the package
     * @return CellNetworkScanResult containing status of scan and networks.
     */
    Object getCellNetworkScanResults(MethodHandle methodHandle, int subId, String callingPackage,
                                                    String callingFeatureId){
        return methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Perform a radio network scan and return the id of this scan.
     *
     * @param subId the id of the subscription.
     * @param renounceFineLocationAccess Set this to true if the caller would not like to
     * receive fine location related information
     * @param request Defines all the configs for network scan.
     * @param messenger Callback messages will be sent using this messenger.
     * @param binder the binder object instantiated TelephonyManager.
     * @param callingPackage the calling package
     * @param callingFeatureId The feature the package
     * @return An id for this scan.
     */
    int requestNetworkScan(MethodHandle methodHandle, int subId, boolean renounceFineLocationAccess,
                           NetworkScanRequest request, Messenger messenger, IBinder binder,
                           String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{
                subId, renounceFineLocationAccess, request, messenger, binder, hostPkg, callingFeatureId
        });
    }

    /**
     * Control of data connection and provide the reason triggering the data connection control.
     *  @param subId user preferred subId.
     * @param reason the reason the data enable change is taking place
     * @param enable true to turn on, else false
     * @param callingPackage the package that changed the data enabled state
     * @return
     */
    Object setDataEnabledForReason(MethodHandle methodHandle, int subId, int reason, boolean enable, String callingPackage){
        return methodHandle.invokeOriginMethod(new Object[]{ subId, reason, enable, hostPkg });
    }


    /**
     * Request that the next incoming call from a number matching {@code range} be intercepted.
     * @param range The range of phone numbers the caller expects a phone call from.
     * @param timeoutMillis The amount of time to wait for such a call, or
     *                      { #MAX_NUMBER_VERIFICATION_TIMEOUT_MILLIS}, whichever is lesser.
     * @param callback the callback aidl
     * @param callingPackage the calling package name.
     */
    void requestNumberVerification(MethodHandle methodHandle, Object range, long timeoutMillis,
                                   Object callback, String callingPackage){
        methodHandle.invokeOriginMethod(new Object[]{
                range, timeoutMillis, callback, hostPkg
        });
    }

    /**
     * Similar to above, but check for the package whose name is pkgName.
     * Requires that the calling app has READ_PRIVILEGED_PHONE_STATE permission
     */
    int checkCarrierPrivilegesForPackage(MethodHandle methodHandle, int subId, String pkgName){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }

    /**
     * Similar to above, but check across all phones.
     * Requires that the calling app has READ_PRIVILEGED_PHONE_STATE permission
     */
    int checkCarrierPrivilegesForPackageAnyPhone(MethodHandle methodHandle, String pkgName){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Returns the displayed dialing number string if it was set previously via
     * { #setLine1NumberForDisplay}. Otherwise returns null.
     *
     * @param subId whose dialing number for line 1 is returned.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return the displayed dialing number if set, or null if not set.
     */
    String getLine1NumberForDisplay(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Returns the displayed alphatag of the dialing number if it was set
     * previously via { #setLine1NumberForDisplay}. Otherwise returns null.
     *
     * @param subId whose alphatag associated with line 1 is returned.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return the displayed alphatag of the dialing number if set, or null if
     *         not set.
     */
    String getLine1AlphaTagForDisplay(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Return the set of subscriber IDs that should be considered "merged together" for data usage
     * purposes. This is commonly {@code null} to indicate no merging is required. Any returned
     * subscribers are sorted a deterministic order.
     * <p>
     * The returned set of subscriber IDs will include the subscriber ID corresponding to this
     * TelephonyManager's subId.
     *
     * @hide
     */
    String[] getMergedSubscriberIds(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (String[]) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * @hide
     */
    String[] getMergedImsisFromGroup(MethodHandle methodHandle, int subId, String callingPackage){
        return (String[]) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }


    /**
     * Get phone radio type and access technology.
     *
     * @param phoneId which phone you want to get
     * @param callingPackage the name of the package making the call
     * @return phone radio type and access technology
     */
    int getRadioAccessFamily(MethodHandle methodHandle, int phoneId, String callingPackage){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ phoneId, hostPkg });
    }

    void uploadCallComposerPicture(MethodHandle methodHandle, int subscriptionId, String callingPackage,
                                   String contentType, ParcelFileDescriptor fd, ResultReceiver callback){
        methodHandle.invokeOriginMethod(new Object[]{ subscriptionId, hostPkg, contentType, fd, callback });
    }

    /**
     * Whether video calling has been enabled by the user.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return {@code true} if the user has enabled video calling, {@code false} otherwise.
     */
    boolean isVideoCallingEnabled(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Whether the DTMF tone length can be changed.
     *
     * @param subId The subscription to use.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return {@code true} if the DTMF tone length can be changed.
     */
    boolean canChangeDtmfToneLength(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Whether the device is a world phone.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return {@code true} if the devices is a world phone.
     */
    boolean isWorldPhone(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /** @deprecated Use {@link #getDeviceIdWithFeature(MethodHandle methodHandle, String, String) instead */

    String getDeviceId(MethodHandle methodHandle, String callingPackage){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Returns the unique device ID of phone, for example, the IMEI for
     * GSM and the MEID for CDMA phones. Return null if device ID is not available.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getDeviceIdWithFeature(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Returns the IMEI for the given slot.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getImeiForSlot(MethodHandle methodHandle, int slotIndex, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, hostPkg, callingFeatureId });
    }

    /**
     * Returns the MEID for the given slot.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getMeidForSlot(MethodHandle methodHandle, int slotIndex, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, hostPkg, callingFeatureId });
    }

    /**
     * Returns the device software version.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getDeviceSoftwareVersionForSlot(MethodHandle methodHandle, int slotIndex, String callingPackage,
                                           String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, hostPkg, callingFeatureId });
    }

    /**
     * Returns the subscription ID associated with the specified PhoneAccountHandle.
     */
    int getSubIdForPhoneAccountHandle(MethodHandle methodHandle, PhoneAccountHandle phoneAccountHandle,
                                      String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ phoneAccountHandle, hostPkg, callingFeatureId });
    }


    void factoryReset(MethodHandle methodHandle, int subId, String callingPackage){
        methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }


    /**
     * Get the service state on specified subscription
     * @param subId Subscription id
     * @param renounceFineLocationAccess Set this to true if the caller would not like to
     * receive fine location related information
     * @param renounceCoarseLocationAccess Set this to true if the caller would not like to
     * receive coarse location related information
     * @param callingPackage The package making the call
     * @param callingFeatureId The feature the package
     * @return Service state on specified subscription.
     */
    ServiceState getServiceStateForSubscriber(MethodHandle methodHandle, int subId, boolean renounceFineLocationAccess,
                                              boolean renounceCoarseLocationAccess,
                                              String callingPackage, String callingFeatureId){
        return (ServiceState) methodHandle.invokeOriginMethod(new Object[]{
                subId, renounceFineLocationAccess, renounceCoarseLocationAccess, hostPkg, callingFeatureId
        });
    }

    /**
     * Sets the per-account voicemail ringtone.
     *
     * <p>Requires that the calling app is the default dialer, or has carrier privileges, or
     * has permission {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}.
     *
     * @param phoneAccountHandle The handle for the {@link PhoneAccount} for which to set the
     * voicemail ringtone.
     * @param uri The URI for the ringtone to play when receiving a voicemail from a specific
     * PhoneAccount.
     */
    void setVoicemailRingtoneUri(MethodHandle methodHandle, String callingPackage,
                                 PhoneAccountHandle phoneAccountHandle, Uri uri){
        methodHandle.invokeOriginMethod(new Object[]{ hostPkg, phoneAccountHandle, uri });
    }


    /**
     * Sets the per-account preference whether vibration is enabled for voicemail notifications.
     *
     * <p>Requires that the calling app is the default dialer, or has carrier privileges, or
     * has permission {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}.
     *
     * @param phoneAccountHandle The handle for the {@link PhoneAccount} for which to set the
     * voicemail vibration setting.
     * @param enabled Whether to enable or disable vibration for voicemail notifications from a
     * specific PhoneAccount.
     */
    void setVoicemailVibrationEnabled(MethodHandle methodHandle, String callingPackage,
                                      PhoneAccountHandle phoneAccountHandle, boolean enabled){
        methodHandle.invokeOriginMethod(new Object[]{ hostPkg, phoneAccountHandle, enabled });
    }
    /**
     * Get Client request stats which will contastatistical information
     * on each request made by client.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature the package.
     * @param subId Subscription index
     * @hide
     */
    Object getClientRequestStats(MethodHandle methodHandle, String callingPackage, String callingFeatureId,
                                                   int subId){
        return methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId, subId });
    }

    /**
     * Returns a list of Forbidden PLMNs from the specified SIM App
     * Returns null if the query fails.
     *
     * <p>Requires that the calling app has READ_PRIVILEGED_PHONE_STATE or READ_PHONE_STATE
     *
     * @param subId subscription ID used for authentication
     * @param appType the icc application type, like { #APPTYPE_USIM}
     */
    String[] getForbiddenPlmns(MethodHandle methodHandle, int subId, int appType, String callingPackage,
                               String callingFeatureId){
        return (String[]) methodHandle.invokeOriginMethod(new Object[]{ subId, appType, hostPkg, callingFeatureId });
    }

    /**
     * Set the forbidden PLMN list from the givven app type (MethodHandle methodHandle, ex APPTYPE_USIM) on a particular
     * subscription.
     *
     * @param subId subId the id of the subscription
     * @param appType appType the uicc app type, must be USIM or SIM.
     * @param fplmns plmns the Forbiden plmns list that needed to be written to the SIM.
     * @param callingPackage the op Package name.
     * @param callingFeatureId the feature the package.
     * @return number of fplmns that is successfully written to the SIM
     */
    int setForbiddenPlmns(MethodHandle methodHandle, int subId, int appType, List<String> fplmns, String callingPackage,
                          String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, appType, fplmns, hostPkg, callingFeatureId });
    }

    /**
     * Get the card ID of the default eUICC card. If there is no eUICC, returns
     * { #INVALID_CARD_ID}.
     *
     * @param subId subscription ID used for authentication
     * @param callingPackage package making the call
     * @return card ID of the default eUICC card.
     */
    int getCardIdForDefaultEuicc(MethodHandle methodHandle, int subId, String callingPackage){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }

    /**
     * Gets information about currently inserted UICCs and eUICCs.
     * <p>
     * Requires that the calling app has carrier privileges (MethodHandle methodHandle, see { #hasCarrierPrivileges}).
     * <p>
     * If the caller has carrier priviliges on any active subscription, then they have permission to
     * get simple information like the card ID (MethodHandle methodHandle, {@link UiccCardInfo#getCardId()}), whether the card
     * is an eUICC ({@link UiccCardInfo#isEuicc()}), and the slot index where the card is inserted
     * ({@link UiccCardInfo#getSlotIndex()}).
     * <p>
     * To get private information such as the EID ({@link UiccCardInfo#getEid()}) or ICCID
     * ({@link UiccCardInfo#getIccId()}), the caller must have carrier priviliges on that specific
     * UICC or eUICC card.
     * <p>
     * See {@link UiccCardInfo} for more details on the kind of information available.
     *
     * @param callingPackage package making the call, used to evaluate carrier privileges
     * @return a list of UiccCardInfo objects, representing information on the currently inserted
     * UICCs and eUICCs. Each UiccCardInfo the list will have private information filtered out if
     * the caller does not have adequate permissions for that card.
     */
    List<UiccCardInfo> getUiccCardsInfo(MethodHandle methodHandle, String callingPackage){
        return (List<UiccCardInfo>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * Get slot info for all the UICC slots.
     * @return UiccSlotInfo array.
     * @hide
     */
    Object[] getUiccSlotsInfo(MethodHandle methodHandle, String callingPackage){
        return (Object[]) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }

    /**
     * How many modems can have simultaneous data connections.
     * @hide
     */
    int getNumberOfModemsWithSimultaneousDataConnections(MethodHandle methodHandle, int subId, String callingPackage,
                                                         String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }


    /**
     * Return the modem radio power state for slot index.
     *
     */
    int getRadioPowerState(MethodHandle methodHandle, int slotIndex, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, hostPkg, callingFeatureId });
    }
    /**
     * Return the emergency number list from all the active subscriptions.
     */
    Map getEmergencyNumberList(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (Map) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }


    /**
     * Returns if the usage of multiple SIM cards at the same time is supported.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature the package.
     * @return { #MULTISIM_ALLOWED} if the device supports multiple SIMs.
     * { #MULTISIM_NOT_SUPPORTED_BY_HARDWARE} if the device does not support multiple SIMs.
     * { #MULTISIM_NOT_SUPPORTED_BY_CARRIER} the device supports multiple SIMs, but the
     * functionality is restricted by the carrier.
     */
    int isMultiSimSupported(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingFeatureId });
    }

    /**
     * Get if altering modems configurations will trigger reboot.
     * @hide
     */
    boolean doesSwitchMultiSimConfigTriggerReboot(MethodHandle methodHandle, int subId, String callingPackage,
                                                  String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

    /**
     * Get the mapping from logical slots to port index.
     */
    List<?> getSlotsMapping(MethodHandle methodHandle, String callingPackage){
        return (List<?>) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }


    boolean isModemEnabledForSlot(MethodHandle methodHandle, int slotIndex, String callingPackage, String callingFeatureId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ slotIndex, hostPkg, callingFeatureId });
    }

    boolean isDataEnabledForApn(MethodHandle methodHandle, int apnType, int subId, String callingPackage){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ apnType, subId, hostPkg });
    }

    /**
     * Enqueue a pending sms Consumer, which will answer with the user specified selection for an
     * outgoing SmsManager operation.
     */
     void enqueueSmsPickResult(MethodHandle methodHandle, String callingPackage, String callingAttributeTag,
                                     Object subIdResult){
         methodHandle.invokeOriginMethod(new Object[]{ hostPkg, callingAttributeTag, subIdResult });
    }


    /**
     * Returns a list of the equivalent home PLMNs (EF_EHPLMN) from the USIM app.
     *
     * @return A list of equivalent home PLMNs. Returns an empty list if EF_EHPLMN is empty or
     * does not exist on the SIM card.
     */
    List<String> getEquivalentHomePlmns(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId){
        return (List<String>) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }


    /**
     * Thermal mitigation request to control functionalities at modem.
     *
     * @param subId the id of the subscription
     * @param thermalMitigationRequest holds the parameters necessary for the request.
     * @param callingPackage the package name of the calling package.
     */
    int sendThermalMitigationRequest(MethodHandle methodHandle, int subId,
                                     Object thermalMitigationRequest,
                                     String callingPackage){
        return (int) methodHandle.invokeOriginMethod(new Object[]{ subId, thermalMitigationRequest, hostPkg });
    }

    /**
     * Set the GbaService Package Name that Telephony will bind to.
     */
    boolean setBoundGbaServiceOverride(MethodHandle methodHandle, int subId, String packageName){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg });
    }


    /**
     * Set a SignalStrengthUpdateRequest to receive notification when Signal Strength breach the
     * specified thresholds.
     */
    void setSignalStrengthUpdateRequest(MethodHandle methodHandle, int subId, Object request,
                                        String callingPackage){
        methodHandle.invokeOriginMethod(new Object[]{ subId, request, hostPkg });
    }

    /**
     * Clear a SignalStrengthUpdateRequest from system.
     */
    void clearSignalStrengthUpdateRequest(MethodHandle methodHandle, int subId, Object request,
                                          String callingPackage){
        methodHandle.invokeOriginMethod(new Object[]{ subId, request, hostPkg });
    }

    /**
     * Register an IMS connection state callback
     */
    void registerImsStateCallback(MethodHandle methodHandle, int subId, int feature, Object cb,
                                  String callingPackage){
        methodHandle.invokeOriginMethod(new Object[]{ subId, feature, cb, hostPkg });
    }

    /**
     * return last known cell identity
     * @param subId user preferred subId.
     * @param callingPackage the name of the package making the call.
     * @param callingFeatureId The feature the package.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    CellIdentity getLastKnownCellIdentity(MethodHandle methodHandle, int subId, String callingPackage,
                                          String callingFeatureId){
        return (CellIdentity) methodHandle.invokeOriginMethod(new Object[]{ subId, hostPkg, callingFeatureId });
    }

}
