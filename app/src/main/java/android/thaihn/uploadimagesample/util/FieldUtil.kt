package android.thaihn.uploadimagesample.util

import android.thaihn.uploadimagesample.entity.Field
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FieldUtil {

    fun getFields(): ArrayList<Field> {
        val fieldStr = SharedPrefs.instance[Util.PREF_FIELDS, String::class.java, ""]
        var fields = arrayListOf<Field>()

        if (fieldStr.isNotEmpty()) {
            val type = object : TypeToken<ArrayList<Field>>() {}.type
            fields = Gson().fromJson(fieldStr, type)
        }

        return fields
    }

    fun saveFields(fields: List<Field>) {
        SharedPrefs.instance.put(Util.PREF_FIELDS, Gson().toJson(fields))
    }
}
