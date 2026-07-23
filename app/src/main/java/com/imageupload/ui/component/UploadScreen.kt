package com.imageupload.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import com.imageupload.data.remote.RetrofitInstance


@Composable
fun UploadScreen() {
    var code = remember { mutableStateOf("101") }
    var image = remember { mutableStateOf<Bitmap?>(null) }
    var chk = remember { mutableStateOf(value = false) }
    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("업로드")
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("코드") },
                value = code.value,
                onValueChange = { code.value = it; chk.value = true },
                singleLine = true
            )
            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.let {
                        val bitmap = BitmapFactory.decodeStream(it)
                        image.value = bitmap
                        it.close()
                    }
                }
            }
            image.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "첨부이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    pickImageLauncher.launch(arrayOf("image/*"))
                }
            ) {
                Text("이미지 파일 선택")
            }

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { chk.value = true }
            ) {
                Text("이미지 업로드")
            }

            LaunchedEffect(chk.value) {
                if (chk.value) {
                    var codePart = code.value.toRequestBody("text/plain".toMediaType())
                    val bitmap = image.value
                    if ( bitmap != null ) {
                        // 1. 캐시 디렉토리에 임시파일 생성 (현재는 0byte 파일임)
                        val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
                        // 2. bitmap 을 파일로 저장 jpg 포맷의 퀄리티 100으로 저장함(현재는 파일사이즈 변경)
                        FileOutputStream(file).use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }

                        // 3. 생성된 파일을 requestPart로 변경
                        val fileRequest = file.asRequestBody(("image/jpeg".toMediaType()))
                        val filePart = MultipartBody.Part.createFormData("image", file.name, fileRequest)

                        // 4. 백엔드 호출하기
                        val response = RetrofitInstance.api.uploadImage(image=filePart, code=codePart)
                        Log.d("foo", "so far...")
                        if ( response.result == 1 ) {
                            Toast.makeText(context, "Upload 성공", Toast.LENGTH_SHORT).show()
                        }
                    }
                    chk.value = false
                }
            }
        }
    }
}