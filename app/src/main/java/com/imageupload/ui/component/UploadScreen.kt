package com.imageupload.ui.component

import android.content.Intent
import android.graphics.Bitmap
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
import com.imageupload.data.model.remote.ApiService
import kotlin.contracts.contract
import androidx.activity.compose.rememberLauncherForActivityResult


@Composable
fun UploadScreen()
{
    var code = remember { mutableStateOf("101") }
    var image  = remember { mutableStateOf<Bitmap?>(null) }
    var chk = remember { mutableStateOf(value = false )}
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("업로드")
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("코드")},
                value = code.value,
                onValueChange = { code.value = it; chk.value = true},
                singleLine = true
            )
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Use the selected file
            Log.d("ttush", it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()}
    }
    Button(
        onClick = {
            pickImageLauncher.launch(arrayOf("image/*"))
        }
    ) {
        Text("이미지 파일 선택")
   }
}