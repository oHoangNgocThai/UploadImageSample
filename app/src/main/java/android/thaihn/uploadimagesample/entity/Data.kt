package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Data(

    @SerializedName("form_type")
    val form_type: Int?,

    @SerializedName("form_name")
    val form_name: String?,

    @SerializedName("data1")
    val data1: List<String>?,

    @SerializedName("data2")
    val data2: List<String>?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(form_type)
        parcel.writeString(form_name)
        parcel.writeStringList(data1)
        parcel.writeStringList(data2)
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