package android.thaihn.uploadimagesample.service

import android.thaihn.uploadimagesample.entity.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {

    @Multipart
    @POST("api/ocr")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Header("api-key") authorization: String
    ): Call<UploadResponse>
}
