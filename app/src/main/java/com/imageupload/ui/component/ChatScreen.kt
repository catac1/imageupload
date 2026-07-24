package com.imageupload.ui.component

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.imageupload.MqttMessageManger

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    var msg by remember { mutableStateOf(value = "") }
    var chk by remember { mutableStateOf(value = false) }
    var messages by remember { mutableStateOf(listOf<String>())}
    var topic by remember { mutableStateOf("pknu/class207")}

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column( modifier = Modifier.fillMaxSize()) {
            Text("채팅")
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("보낼 메시지") },
                value = msg,
                onValueChange = { msg = it },
                singleLine = true
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if ( !msg.none()) {
                        MqttMessageManger.publish(topic, msg)
                    }
                }
            ) {
                Text("보내기")
            }

            LaunchedEffect(Unit) {
                MqttMessageManger.messageFlow.collect { newMsg ->
                    if ( newMsg.isNotEmpty() ) {
                        messages = messages + newMsg
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(messages) { msg ->
                    Text(msg, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}