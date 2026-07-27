package com.imageupload.ui.component

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imageupload.CameraActivity

@Composable
fun SettingScreen () {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column ( modifier = Modifier.fillMaxSize()) {
            Button (
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(context, CameraActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text("카메라")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}