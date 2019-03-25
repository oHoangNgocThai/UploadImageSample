package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadResponse(

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("code")
    @Expose
    val code: Int,

    @SerializedName("data")
    @Expose
    val data: Data

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readParcelable(Data::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeInt(code)
        parcel.writeParcelable(data, flags)
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