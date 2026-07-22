package com.imageupload.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
                    Toast.makeText(context, "백엔드 연동 필요", Toast.LENGTH_SHORT).show()
                    chk.value = false
                }
            }
        }
    }
}