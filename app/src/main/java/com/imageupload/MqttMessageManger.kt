package com.imageupload

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MqttMessageManger {

    // 화면 진입시 마지막 메시지 1개를 전달함
    private val mf = MutableSharedFlow<String> (replay = 1)

    // ChatScreen에서 메시지를 받을때 사용
    val messageFlow = mf.asSharedFlow()

    // MqttService에서 메시ㅣ를 보낼때 사용
    fun emitMessage(message: String) {
        mf.tryEmit(message)
    }

    var onPublishRequested: ((String, String) -> Unit)? =  null
    fun publish(topic: String, payload: String) {
        onPublishRequested?.invoke(topic, payload)
    }
}