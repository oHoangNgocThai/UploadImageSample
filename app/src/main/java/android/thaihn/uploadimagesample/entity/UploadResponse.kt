package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UploadResponse(

        @SerializedName("message")
        val message: String?,

        @SerializedName("code")
        val code: Int?,

        @SerializedName("data")
        val data: Data?,

        @SerializedName("errors")
        val errors: Error?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readParcelable(Data::class.java.classLoader),
            parcel.readParcelable(Error::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeValue(code)
        parcel.writeParcelable(data, flags)
        parcel.writeParcelable(errors, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UploadResponse> {
        override fun createFromParcel(parcel: Parcel): UploadResponse {
            return UploadResponse(parcel)
        }

        override fun newArray(size: Int): Array<UploadResponse?> {
            return arrayOfNulls(size)
        }
    }
}
