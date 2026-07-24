package com.imageupload.ui.component

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imageupload.service.MqttService

@Composable
fun ChatScreen(mqttService: MqttService?) {
    val msg = remember { mutableStateOf("") }
    val messages = remember { mutableStateOf(listOf<String>()) }
    val topic = remember { mutableStateOf("pknu/class207") }
    val isConnected = remember(mqttService) { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column( modifier = Modifier.fillMaxSize()) {
            Text("채팅")
            Text(
                when {
                    mqttService == null -> "MQTT 서비스 연결 중"
                    isConnected.value -> "MQTT 연결됨"
                    else -> "MQTT 브로커 연결 중"
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("보낼 메시지") },
                value = msg.value,
                onValueChange = { msg.value = it },
                singleLine = true
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = isConnected.value && msg.value.isNotBlank(),
                onClick = {
                    if (mqttService?.publishMqttMessage(topic.value, msg.value) == true) {
                        msg.value = ""
                    }
                }
            ) {
                Text("보내기")
            }

            LaunchedEffect(mqttService) {
                mqttService?.messageFlow?.collect { newMsg ->
                    if ( newMsg.isNotEmpty() ) {
                        messages.value = messages.value + newMsg
                    }
                }
            }

            LaunchedEffect(mqttService) {
                if (mqttService == null) {
                    isConnected.value = false
                } else {
                    mqttService.connectionState.collect { connected ->
                        isConnected.value = connected
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(messages.value) { msg ->
                    Text(msg, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}
