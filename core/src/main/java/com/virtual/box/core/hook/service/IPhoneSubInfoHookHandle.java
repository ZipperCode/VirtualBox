package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.com.android.internal.telephony.HIPhoneSubInfo;

public class IPhoneSubInfoHookHandle extends BaseBinderHookHandle {
    public IPhoneSubInfoHookHandle() {
        super("iphonesubinfo");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIPhoneSubInfo.Stub.asInterface.call(getOriginBinder());
    }

    /**
     * @deprecated Use {getDeviceIdWithFeature(String, String) instead
     */
    // @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String getDeviceId(MethodHandle methodHandle, String callingPackage) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * Retrieves the unique device ID, e.g., IMEI for GSM phones.
     */
    String getDeviceIdWithFeature(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the unique Network Access ID
     */
    String getNaiForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the unique device ID of a phone for the device, e.g., IMEI
     * for GSM phones.
     */
    String getDeviceIdForPhone(MethodHandle methodHandle, int phoneId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{phoneId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the IMEI.
     */
    String getImeiForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the software version number for the device, e.g., IMEI/SV
     * for GSM phones.
     */
    String getDeviceSvn(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the software version number of a subId for the device, e.g., IMEI/SV
     * for GSM phones.
     */
    String getDeviceSvnUsingSubId(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * @deprecated Use {getSubscriberIdWithFeature(String, String) instead
     */
    String getSubscriberId(MethodHandle methodHandle, String callingPackage) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * Retrieves the unique sbuscriber ID, e.g., IMSI for GSM phones.
     */
    String getSubscriberIdWithFeature(MethodHandle methodHandle, String callingPackage, String callingComponenId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingComponenId});
    }

    /**
     * Retrieves the unique subscriber ID of a given subId, e.g., IMSI for GSM phones.
     */
    String getSubscriberIdForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                        String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the Group Identifier Level1 for GSM phones of a subId.
     */
    String getGroupIdLevel1ForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                         String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * @deprecared Use { getIccSerialNumberWithFeature(String, String)} instead
     */
    String getIccSerialNumber(MethodHandle methodHandle, String callingPackage) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * Retrieves the serial number of the ICC, if applicable.
     */
    String getIccSerialNumberWithFeature(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the serial number of a given subId.
     */
    String getIccSerialNumberForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                           String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the phone number string for line 1.
     */
    String getLine1Number(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the phone number string for line 1 of a subcription.
     */
    String getLine1NumberForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }


    /**
     * Retrieves the alpha identifier for line 1.
     */
    String getLine1AlphaTag(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the alpha identifier for line 1 of a subId.
     */
    String getLine1AlphaTagForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                         String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }


    /**
     * Retrieves MSISDN Number.
     */
    String getMsisdn(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the Msisdn of a subId.
     */
    String getMsisdnForSubscriber(MethodHandle methodHandle, int subId, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the voice mail number.
     */
    String getVoiceMailNumber(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the voice mail number of a given subId.
     */
    String getVoiceMailNumberForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                           String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the Carrier information used to encrypt IMSI and IMPI.
     */
    Object getCarrierInfoForImsiEncryption(MethodHandle methodHandle, int subId, int keyType,
                                           String callingPackage) {
        return methodHandle.invokeOriginMethod(new Object[]{subId, keyType, hostPkg});
    }

    /**
     * Stores the Carrier information used to encrypt IMSI and IMPI.
     */
    void setCarrierInfoForImsiEncryption(MethodHandle methodHandle, int subId, String callingPackage,
                                         Object imsiEncryptionInfo) {
        methodHandle.invokeOriginMethod(new Object[]{
                subId, hostPkg, imsiEncryptionInfo
        });
    }

    /**
     * Resets the Carrier Keys the database. This involves 2 steps:
     * 1. Delete the keys from the database.
     * 2. Send an intent to download new Certificates.
     */
    void resetCarrierKeysForImsiEncryption(MethodHandle methodHandle, int subId, String callingPackage) {
        methodHandle.invokeOriginMethod(new Object[]{
                subId, hostPkg
        });
    }

    /**
     * Retrieves the alpha identifier associated with the voice mail number.
     */
    String getVoiceMailAlphaTag(MethodHandle methodHandle, String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }

    /**
     * Retrieves the alpha identifier associated with the voice mail number
     * of a subId.
     */
    String getVoiceMailAlphaTagForSubscriber(MethodHandle methodHandle, int subId, String callingPackage,
                                             String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, hostPkg, callingFeatureId});
    }

    /**
     * Returns the response of the SIM application on the UICC to authentication
     * challenge/response algorithm. The data string and challenge response are
     * Base64 encoded Strings.
     * Can support EAP-SIM, EAP-AKA with results encoded per 3GPP TS 31.102.
     *
     * @param subId    subscription ID to be queried
     * @param appType  ICC application type (@see com.android.internal.telephony.PhoneConstants#APPTYPE_xxx)
     * @param authType Authentication type, see PhoneConstants#AUTHTYPE_xxx
     * @param data     authentication challenge data
     * @return challenge response
     */
    String getIccSimChallengeResponse(MethodHandle methodHandle, int subId, int appType, int authType, String data,
                                      String callingPackage, String callingFeatureId) {
        return (String) methodHandle.invokeOriginMethod(new Object[]{subId, appType, authType, data, hostPkg, callingFeatureId});
    }
}
