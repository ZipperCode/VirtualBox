package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;

/**
 * 仅仅用来编译，可以不需要打包进apk
 */
public class VerifierInfo implements Parcelable {

    public static final Creator<VerifierInfo> CREATOR = new Creator<VerifierInfo>() {
        public VerifierInfo createFromParcel(final Parcel source) {
            return new VerifierInfo(source);
        }

        public VerifierInfo[] newArray(final int size) {
            return new VerifierInfo[size];
        }
    };

    public VerifierInfo(final String packageName, final PublicKey publicKey) {
        throw new RuntimeException("Stub!");
    }

    private VerifierInfo(final Parcel source) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        throw new RuntimeException("Stub!");
    }
}