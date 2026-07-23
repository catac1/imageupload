package com.imageupload.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.imageupload.R
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

class MqttService: Service() {

//    private val MQTT_URL = "tcp://192.168.0.12:1883"
    private val MQTT_URL = "tcp://10.174.96.119:31883"
    private val MQTT_CHANNEL_ID = "mqtt_channel"
    private val MQTT_MESSAGE_CHANNEL_ID = "mqtt_new_channel"
    private val TOPIC = "pknu/class207"
    private val CLIENT_ID = "cid1_" + System.currentTimeMillis()

    // 지연 연결 => 현재는 객체가 생성되지 않지만 특정시점에 접속할 예정
    private lateinit var mqttClient: MqttClient

    private val binder = LocalBinder()

    inner class LocalBinder: Binder() {
        fun getService() : MqttService {
            return this@MqttService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("mqtt", "MQTT onCreate")
        // 1. 채널설정
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("mqtt", "MQTT onStartCommand")
        // 2. foreground 알림 설정
        startForegroundWithNotification()

        // 3. 변수가 초기화가 안되어있거나 연결이 되 있지 않다면
        if (!::mqttClient.isInitialized || !mqttClient.isConnected) {
            connectMQTT()
        }

//        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    // 기능을 변경
    override fun onBind(p0: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        try {
            if ( ::mqttClient.isInitialized ) {
                if ( mqttClient.isConnected ) {
                    mqttClient.disconnect()
                }
                mqttClient.close()
            }
        } catch ( e : Exception ) {
            Log.d("mqtt", e.message.toString())
            e.printStackTrace()
        }
        super.onDestroy()
    }

    // android 8.0 부터는 채널 생성 필수
    private fun createNotificationChannel() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            val manager = getSystemService(NotificationManager::class.java)

            // 1. 백그라운드에서 MQTT 연결이 끊어지지 않고 계속 동작 중임을 시스템에 알림
            val serviceChannel = NotificationChannel(
                MQTT_CHANNEL_ID, "MQTT service", NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(serviceChannel)

            // 2. 실제 메시지가 왔을때 알림 채널
            val messageChannel = NotificationChannel(
                MQTT_MESSAGE_CHANNEL_ID, "MQTT 메세지", NotificationManager.IMPORTANCE_HIGH
            ).apply {

            }
            manager.createNotificationChannel(messageChannel)
        }
    }

    private fun startForegroundWithNotification() {
        val binder = NotificationCompat.Builder(this, MQTT_CHANNEL_ID)
            .setContentTitle("MQTT 서비스 실행")
            .setContentText("백그라운드에서 메시지 수신중")
        try {
            binder.setSmallIcon(R.drawable.ic_launcher_foreground)
        } catch (e: Exception) {
            binder.setSmallIcon((R.drawable.ic_home))
        }

        // foreground 서비스 시작

        val notification = binder.build()
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ) {
//            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING)
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }
    }

    private fun connectMQTT() {
        // 비동기로 접속 하기
        Thread {
            try {
                // 1. 임시파일 저장 위치 설정
                val persistence = MqttDefaultFilePersistence(applicationContext.cacheDir.absolutePath)

                // 2. 브로커에 접속하기
                mqttClient = MqttClient(MQTT_URL, CLIENT_ID, persistence)
                val option = MqttConnectOptions().apply {
                    isCleanSession = false // 재연결후 구독유지
                    userName = "kst" // 접속 아이디
                    password = "11".toCharArray()  // 접속 암호
                    connectionTimeout = 10 // 접속시도 10초
                    keepAliveInterval = 30 //30 초 간격으로 연결 유지 ping 전송
                }

                // 3. 연결 확인, 메시지 받기, 전송완료 등의 콜백 설정
                mqttClient.setCallback(object : MqttCallback {

                    override fun connectionLost(cause: Throwable?) {
                        TODO("Not yet implemented")
                    }

                    override fun messageArrived(
                        topic: String?,
                        message: MqttMessage?
                    ) {
                        val messageString = message.toString()
                        val fullMessage = "[${topic}] : $messageString"
                        Log.d("mqtt", fullMessage)
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        TODO("Not yet implemented")
                    }

                })

                mqttClient.connect(option)
                mqttClient.subscribe(TOPIC)
            } catch ( e : Exception) {
                Log.d("mqtt", e.message.toString())
                e.printStackTrace()
            }
        }.start()
    }
}