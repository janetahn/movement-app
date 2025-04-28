package com.example.janet_ahn_myruns5

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import java.util.*

class TrackingService : Service() {
    private lateinit var timer: Timer
    var sec: Int = 0

    private lateinit var notificationManager: NotificationManager
    private val Notification_ID = 1
    private val CHANNEL_ID = "channel_id"
    private lateinit var locationManager: LocationManager
    private lateinit var location: Location
    private lateinit var prevLocation: Location
    var initAltitude: Double = 0.0

    var avgSpeed: Double = 0.0
    var distance: Double = 0.0
    var calories: Double = 0.0
    var duration: Double = 0.0
    var climb: Double = 0.0 // replace heartrate in database
    var lat: Double = 0.0
    var lng: Double = 0.0
    var curSpeed: Double = 0.0

    private var KM_TO_MILES = 0.621371

    private var locationServiceMsgHandler: MapActivity.MyMessageHandler? = null

    private lateinit var unitArray: Array<String>

    override fun onCreate() {
        super.onCreate()
        timer = Timer()
        val timerTask = CounterTask()
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
        initLocationManager()
    }

    inner class CounterTask : TimerTask() {
        override fun run() {
            sec++
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer.cancel()
        }
        if(::notificationManager.isInitialized) {
            notificationManager.cancel(Notification_ID) // cancel the notification
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        val intent = Intent(this, MapActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(CHANNEL_ID, "channel 1", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MyRuns")
                .setContentText("Your location is being tracked right now")
                .setContentIntent(pendingIntent) // implements onclick show app
                .setSmallIcon(R.drawable.location_img)
                .setAutoCancel(true)
        val notification = builder.build()
        notificationManager.notify(Notification_ID, notification)
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean { return true }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // START_NOT_STICKY, START_REDELIVER_INTENT
    }

    inner class MyBinder : Binder() {
        fun setMessageHandler(msgHandler: MapActivity.MyMessageHandler) {
            locationServiceMsgHandler = msgHandler
            if(::location.isInitialized){
                newLocationUpdate(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationManager() {
        println("inside initlocationmanager")
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider : String? = locationManager.getBestProvider(criteria, true)
            if(provider != null) {
                location = locationManager.getLastKnownLocation(provider)!!
                prevLocation = location!!
                initAltitude = location!!.altitude

                locationManager.requestLocationUpdates(provider, 0, 0f, locationListener)
            }
        } catch (e: SecurityException) {
        }
    }


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            print("inside location")
            if (location != null) {
                newLocationUpdate(location)
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    private fun newLocationUpdate(location: Location?) {
        // since it is an optional
        if (location != null) {
            lat = location.latitude
            lng = location.longitude

            var distanceToLast = location.distanceTo(prevLocation);
            distance += distanceToLast/1000;
            unitArray = resources.getStringArray(R.array.unit_preferences);
            val unitType: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val unitValue = unitType!!.getString("unit_key","0")
            if(unitValue == "0") {
                distance = distance
            } else {
                distance = distance * KM_TO_MILES
            }

            prevLocation = location;

            println("location: $lat, $lng")

            curSpeed = location.speed.toDouble()

            duration = sec.toDouble()
            println("sec $duration")

            var curSpeedAdd = 0.0
            curSpeedAdd += curSpeed
            avgSpeed = curSpeedAdd / duration * 50
            climb = location.altitude

            calories = distance * 0.62
            val bundle = Bundle()
            bundle.putDouble("lat_key", lat)
            bundle.putDouble("lnt_key", lng)
            bundle.putDouble("avg_key", avgSpeed)
            bundle.putDouble("cur_key", curSpeed)
            bundle.putDouble("climb_key", climb)
            bundle.putDouble("cal_key", calories)
            bundle.putDouble("dist_key", distance)
            bundle.putDouble("dur_key", duration)
            if(locationServiceMsgHandler != null){
                val message: Message = locationServiceMsgHandler!!.obtainMessage()
                message.data = bundle
                locationServiceMsgHandler!!.sendMessage(message)
            }
        }
    }
}