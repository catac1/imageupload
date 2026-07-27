package com.imageupload.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.imageupload.data.remote.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

suspend fun extracted(context: Context, codePart: RequestBody = RequestBody.EMPTY, bitmap: Bitmap) {
    // 1. 캐시 디렉토리에 임시파일 생성 (현재는 0byte 파일임)
    val file =
        File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
    // 2. bitmap 을 파일로 저장 jpg 포맷의 퀄리티 100으로 저장함(현재는 파일사이즈 변경)
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    // 3. 생성된 파일을 requestPart로 변경
    val fileRequest = file.asRequestBody(("image/jpeg".toMediaType()))
    val filePart =
        MultipartBody.Part.createFormData(
            "image",
            file.name,
            fileRequest
        )

    // 4. 백엔드 호출하기
    val response =
        RetrofitInstance.api.uploadImage(
            image = filePart,
            code = codePart
        )
    Log.d("foo", "so far...")
    if (response.result == 1) {
        Toast.makeText(context, "Upload 성공", Toast.LENGTH_SHORT).show()
    }
}