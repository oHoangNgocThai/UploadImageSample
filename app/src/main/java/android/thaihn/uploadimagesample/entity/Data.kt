package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Data(

        @SerializedName("form_type")
        val form_type: Int?,

        @SerializedName("total")
        val total: String?,

        @SerializedName("kokumin")
        val kokumin: String?,

        @SerializedName("kikan_kosei")
        val kikan_kosei: String?,

        @SerializedName("kikan_total")
        val kikan_total: String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(form_type)
        parcel.writeString(total)
        parcel.writeString(kokumin)
        parcel.writeString(kikan_kosei)
        parcel.writeString(kikan_total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}
