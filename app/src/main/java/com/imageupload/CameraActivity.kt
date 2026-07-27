package com.imageupload

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.imageupload.ui.theme.ImageUploadTheme
import com.imageupload.util.extracted

class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageUploadTheme {
                var chk by remember { mutableStateOf(value = false) }
                // 상태바 가리지 않기
                Scaffold(modifier = Modifier.fillMaxSize())  { innerPadding ->
                    Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        // 1. 상태변수
                        var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

                        // 2. 카메라 호출 완료후 반환되는 bitmap 변수가 null이 아니면 상태변수에 보관
                        val takePictureLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicturePreview()
                        ) { bitmap ->
                            if ( bitmap != null ) {
                                capturedImage = bitmap
                            }
                        }

                        // 3. 권한설정
                        val permissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if ( isGranted ) {
                                takePictureLauncher.launch(null)
                            }
                            else {
                                Toast.makeText(this, "카메라 권한을 취소했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            if ( capturedImage != null) {
                                Image(
                                    bitmap = (capturedImage as Bitmap).asImageBitmap(),
                                    "촬영된 이미지",
                                    modifier = Modifier.size(300.dp)
                                )
                            }

                            Button(onClick = {
                                when {
                                    ContextCompat.checkSelfPermission(this@CameraActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ->
                                        takePictureLauncher.launch(null)
                                    else ->
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Text("사진 촬영")
                            }

                            Spacer(modifier = Modifier.fillMaxWidth())

                            Button( onClick = {
                                chk = true
                            }) {
                                Text("촬영 사진 업로드")
                            }

                        }
                        LaunchedEffect (chk) {
                            try {
                                if (chk) {
                                    // 여기서 fastapi  호출하기
                                    capturedImage?.let { extracted(this@CameraActivity, bitmap = it) }
                                    chk = false
                                }
                            } catch (e: Exception) {
                                Log.d("AAA", e.localizedMessage)
                            }
                        }

                    }
                }
            }
        }
    }
}