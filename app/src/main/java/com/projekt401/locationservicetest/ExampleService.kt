package com.projekt401.locationservicetest

import android.R
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.projekt401.locationservicetest.App.Companion.CHANNEL_ID


class ExampleService : Service() {
    override fun onCreate() {
        super.onCreate()

        // onCreate se ejecuta solamente la primera vez que el servicio se ejecuta

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // onStartCommand se ejecuta cada vez que se llama startService() en el servicio
        // Recibe un intent con informacion

        val input = intent.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Example Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        //do heavy work on a background thread
        //stopSelf();

        // START_NOT_STICKY = El servicio inicia pero no se reinicia otra vez
        // START_STICKY = El servicio inicia y reinicia tan pronto como sea posible
        // START_REDELIVER_INTENT = El servicio inicia y reinicia y se vuelve a pasar el ultimo intent


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // onDestroy se ejecuta cuando el servicio termina onStop()

    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}