package android.thaihn.uploadimagesample.service

import android.thaihn.uploadimagesample.entity.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UploadService {

    @Multipart
    @POST("api/ocr")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Header("api-key") authorization: String
    ): Call<UploadResponse>
}
