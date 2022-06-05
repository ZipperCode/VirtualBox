package com.virtual.box.core.hook.service;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.fingerprint.HIFingerprintService;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HIFingerprintServiceHookHandle extends BaseBinderHookHandle {

    public HIFingerprintServiceHookHandle() {
        super(Context.FINGERPRINT_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIFingerprintService.Stub.asInterface.call(getOriginBinder());
    }

    /**
     * @param callback ITestSessionCallback
     * @return ITestSession
     */
    Object createTestSession(MethodHandle methodHandle, int sensorId,
                             Object callback, String opPackageName) {
        return methodHandle.invokeOriginMethod(new Object[]{
                sensorId, callback, hostPkg
        });
    }


    /**
     * @return FingerprintSensorPropertiesInternal
     */
    List<?> getSensorPropertiesInternal(MethodHandle methodHandle, String opPackageName) {
        return (List<?>) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * @return FingerprintSensorPropertiesInternal
     */
    Object getSensorProperties(MethodHandle methodHandle, int sensorId, String opPackageName) {
        return methodHandle.invokeOriginMethod(new Object[]{sensorId, hostPkg});
    }

    // Authenticate with a fingerprint. This is protected by USE_FINGERPRINT/USE_BIOMETRIC
    // permission. This is effectively deprecated, since it only comes through FingerprintManager
    // now. A requestId is returned that can be used to cancel this operation.
    long authenticate(MethodHandle methodHandle, IBinder token, long operationId, int sensorId, int userId,
                      Object receiver, String opPackageName,
                      boolean shouldIgnoreEnrollmentState) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{
                token, operationId, sensorId, userId, receiver, hostPkg, shouldIgnoreEnrollmentState
        });
    }

    // Uses the fingerprint hardware to detect for the presence of a finger, without giving details
    // about accept/reject/lockout. A requestId is returned that can be used to cancel this
    // operation.
    long detectFingerprint(MethodHandle methodHandle, IBinder token, int userId, Object receiver,
                           String opPackageName) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{
                token, userId, receiver, hostPkg
        });
    }

    // This method prepares the service to start authenticating, but doesn't start authentication.
    // This is protected by the MANAGE_BIOMETRIC signatuer permission. This method should only be
    // called from BiometricService. The additional uid, pid, userId arguments should be determined
    // by BiometricService. To start authentication after the clients are ready, use
    // startPreparedClient( MethodHandle methodHandle, ).
    void prepareForAuthentication(MethodHandle methodHandle, int sensorId, IBinder token, long operationId, int userId,
                                  Object sensorReceiver, String opPackageName, long requestId,
                                  int cookie, boolean allowBackgroundAuthentication) {
        methodHandle.invokeOriginMethod(new Object[]{
                sensorId, token, operationId, userId, sensorReceiver, hostPkg,
                requestId, cookie, allowBackgroundAuthentication
        });
    }


    // Cancel authentication for the given requestId.
    void cancelAuthentication(MethodHandle methodHandle, IBinder token, String opPackageName, long requestId) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, hostPkg, requestId
        });
    }

    // Cancel finger detection for the given requestId.
    void cancelFingerprintDetect(MethodHandle methodHandle, IBinder token, String opPackageName, long requestId) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, hostPkg, requestId
        });
    }

    // Same as above, except this is protected by the MANAGE_BIOMETRIC signature permission. Takes
    // an additional uid, pid, userid.
    void cancelAuthenticationFromService(MethodHandle methodHandle, int sensorId,
                                         IBinder token, String opPackageName, long requestId) {
        methodHandle.invokeOriginMethod(new Object[]{
                sensorId, token, hostPkg, requestId
        });
    }

    /**
     * @param receiver IFingerprintServiceReceiver
     */
    void enroll(MethodHandle methodHandle, IBinder token, byte[] hardwareAuthToken, int userId, Object receiver,
                String opPackageName, int enrollReason) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, hardwareAuthToken, userId, receiver, hostPkg, enrollReason
        });
    }

    /**
     * @param receiver IFingerprintServiceReceiver
     */
    void remove(MethodHandle methodHandle, IBinder token, int fingerId, int userId, Object receiver,
                String opPackageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, fingerId, userId, receiver, hostPkg
        });
    }

    /**
     * @param receiver IFingerprintServiceReceiver
     */
    void removeAll(MethodHandle methodHandle, IBinder token, int userId,
                   Object receiver, String opPackageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, userId, receiver, opPackageName
        });
    }

    // Get a list of enrolled fingerprints the given userId.
    List<?> getEnrolledFingerprints(MethodHandle methodHandle, int userId, String opPackageName) {
        return (List<?>) methodHandle.invokeOriginMethod(new Object[]{
                userId, hostPkg
        });
    }

    // Determine if the HAL is loaded and ready. Meant to support the deprecated FingerprintManager APIs
    boolean isHardwareDetectedDeprecated(MethodHandle methodHandle, String opPackageName) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg
        });
    }

    // Determine if the specified HAL is loaded and ready
    boolean isHardwareDetected(MethodHandle methodHandle, int sensorId, String opPackageName) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                sensorId, hostPkg
        });
    }

    /**
     * @param receiver IFingerprintServiceReceiver
     */
    // Get a pre-enrollment authentication token
    void generateChallenge(MethodHandle methodHandle, IBinder token, int sensorId,
                           int userId, Object receiver, String opPackageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, sensorId, userId, receiver, hostPkg
        });
    }

    // Finish an enrollment sequence and invalidate the authentication token
    void revokeChallenge(MethodHandle methodHandle, IBinder token, int sensorId, int userId,
                         String opPackageName, long challenge) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, sensorId, userId, hostPkg, challenge
        });
    }

    // Determine if a user has at least one enrolled fingerprint. Meant to support the deprecated FingerprintManager APIs
    boolean hasEnrolledFingerprintsDeprecated(MethodHandle methodHandle, int userId, String opPackageName) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                userId, hostPkg
        });
    }

    // Determine if a user has at least one enrolled fingerprint.
    boolean hasEnrolledFingerprints(MethodHandle methodHandle, int sensorId, int userId, String opPackageName) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                sensorId, userId, hostPkg
        });
    }

    // Reset the timeout when user authenticates with strong auth ( MethodHandle methodHandle, e.g. PIN, pattern or password)
    void resetLockout(MethodHandle methodHandle, IBinder token, int sensorId, int userId,
                      byte[] hardwareAuthToken, String opPackageNAame) {
        methodHandle.invokeOriginMethod(new Object[]{
                token, sensorId, userId, hardwareAuthToken, hostPkg
        });
    }

    /**
     * @param callback IBiometricServiceLockoutResetCallback
     */
    void addLockoutResetCallback(MethodHandle methodHandle,
                                 Object callback, String opPackageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                callback, hostPkg
        });
    }


}
