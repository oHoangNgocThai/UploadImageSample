package android.thaihn.uploadimagesample.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Data(

        @SerializedName("form_type")
        val form_type: Int?,

        @SerializedName("total")
        val total: Long?,

        @SerializedName("kokumin")
        val kokumin: Int?,

        @SerializedName("kikan_kosei")
        val kikan_kosei: Int?,

        @SerializedName("kikan_total")
        val kikan_total: Int?

) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Long::class.java.classLoader) as? Long,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(form_type)
                parcel.writeValue(total)
                parcel.writeValue(kokumin)
                parcel.writeValue(kikan_kosei)
                parcel.writeValue(kikan_total)
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
