package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UploadResponse(

        @SerializedName("message")
        val message: String,

        @SerializedName("code")
        val code: Int,

        @SerializedName("data")
        val data: Data,

        @SerializedName("error")
        val error: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readParcelable(Data::class.java.classLoader),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeInt(code)
        parcel.writeParcelable(data, flags)
        parcel.writeString(error)
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
