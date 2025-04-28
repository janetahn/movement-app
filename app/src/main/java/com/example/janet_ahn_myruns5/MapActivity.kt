package com.example.janet_ahn_myruns5

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instance
import weka.core.Instances
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener, ServiceConnection, SensorEventListener {
    val activityTypeArray: Array<String> = arrayOf("Running", "Walking", "Standing", "Cycling",
        "Hiking", "Downhill Skiing", "Cross-Country Skiing",
        "Snowboarding", "Skating", "Swimming", "Mountain Biking",
        "Wheelchair", "Elliptical", "Other")

    private lateinit var typeTitle: TextView
    private lateinit var avgSpeedTitle: TextView
    private lateinit var curSpeedTitle: TextView
    private lateinit var climbTitle: TextView
    private lateinit var calorieTitle: TextView
    private lateinit var distanceTitle: TextView

    private lateinit var mymessagehandler: MyMessageHandler

    private lateinit var mMap: GoogleMap
    private lateinit var mDataset: Instances
    private lateinit var mClassAttribute: Attribute
    private var mLabel: String = ""
    private lateinit var mTask: MyTask

    private var mapCentered = false
    private lateinit var markerOptions: MarkerOptions
    private lateinit var now: Marker
    private lateinit var movingMarker: MarkerOptions
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polylines: ArrayList<Polyline>

    private lateinit var database: AppDatabase
    private lateinit var databaseDao: EntriesDao
    private lateinit var repository: EntryRepository
    private lateinit var vm: RoomViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    var sec: Int = 0
    var latlngArray: ArrayList<LatLng> = arrayListOf()
    private lateinit var unitArray: Array<String>

    var entryEntity = Entries()
    var timeString: String = ""
    var dateString: String = ""
    var avgSpeed: Double = 0.0
    var distance: Double = 0.0
    var calories: Double = 0.0
    var duration: Double = 0.0
    var climb: Double = 0.0 // replace heartrate in database
    var curSpeed: Double = 0.0

    private lateinit var timer: Timer
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private var auto_stand: Int = 0
    private var auto_walk: Int = 0
    private var auto_run: Int = 0

    private var isBind: Boolean = false

    private var type: Int = 0
    private var isAutomatic: Boolean = false

    private lateinit var serviceIntent: Intent

    private var accelerationPrev: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        database = AppDatabase.getInstance(this)
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(this, viewModelFactory).get(RoomViewModel::class.java)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)

        mymessagehandler = MyMessageHandler()
        serviceIntent = Intent(this, TrackingService::class.java)
        applicationContext.startService(serviceIntent)
        if (!isBind) {
            applicationContext.bindService(serviceIntent, this, BIND_AUTO_CREATE)
            isBind = true
        }

        val isItAuto: Int = intent.getIntExtra("position1", 0)
        if (isItAuto == 2) {
            isAutomatic = true
        }

        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)

        val allAttr = ArrayList<Attribute>()

        val df = DecimalFormat("0000")
        for (i in 0 until Globals.ACCELEROMETER_BLOCK_CAPACITY) {
            allAttr.add(Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i.toLong())))
        }
        allAttr.add(Attribute(Globals.FEAT_MAX_LABEL))

        val labelItems = ArrayList<String>(3)
        labelItems.add(Globals.CLASS_LABEL_STANDING)
        labelItems.add(Globals.CLASS_LABEL_WALKING)
        labelItems.add(Globals.CLASS_LABEL_RUNNING)
        labelItems.add(Globals.CLASS_LABEL_OTHER)
        mClassAttribute = Attribute(Globals.CLASS_LABEL_KEY, labelItems)

        allAttr.add(mClassAttribute)

        mDataset = Instances(Globals.FEAT_SET_NAME, allAttr, Globals.FEATURE_SET_CAPACITY)

        mapCentered = false
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        movingMarker = MarkerOptions()

        typeTitle = findViewById(R.id.type_text)
        avgSpeedTitle = findViewById(R.id.avgspeed_text)
        curSpeedTitle = findViewById(R.id.curspeed_text)
        climbTitle = findViewById(R.id.climb_text)
        calorieTitle = findViewById(R.id.calorie_text)
        distanceTitle = findViewById(R.id.distance_text)

        mTask = MyTask()
        timer = Timer()
        timer.scheduleAtFixedRate(mTask, 0, 1000)

        checkPermissions()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.addMarker(markerOptions.position(LatLng(0.0,0.0)))
        mMap.addMarker(movingMarker.position(LatLng(0.0, 0.0)))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0, 0.0)))

        Util.checkPermissions(this)
    }

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder) {
        val myBinder: TrackingService.MyBinder = iBinder as TrackingService.MyBinder
        myBinder.setMessageHandler(mymessagehandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {}

    override fun onSensorChanged(event: SensorEvent) {
        //testing purposes
//        var x = (event.values[0]).toDouble()
//        var y = (event.values[1]).toDouble()
//        var z = (event.values[2]).toDouble()
//
//        var accelerationCurrentValue = Math.sqrt((x*x + y*y + z*z))
//        var changeInAccel = Math.abs(accelerationCurrentValue - accelerationPrev)
//        accelerationPrev = accelerationCurrentValue
//        println("change $changeInAccel")
//
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt(
                (event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2]
                        * event.values[2])).toDouble()
            )
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    inner class MyMessageHandler : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(message: Message) {
            println("inside handle")
            if(::now.isInitialized) {
                now.remove()
            }
            val bundle = message.data
            val lat = bundle.getDouble("lat_key")
            val lng = bundle.getDouble("lnt_key")
            avgSpeed = bundle.getDouble("avg_key")
            curSpeed = bundle.getDouble("cur_key")
            climb = bundle.getDouble("climb_key")
            calories = bundle.getDouble("cal_key")
            distance = bundle.getDouble("dist_key")
            duration = bundle.getDouble("dur_key")

            unitArray = resources.getStringArray(R.array.unit_preferences);
            val unitType: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val unitValue = unitType!!.getString("unit_key","0")

            val position1: Int = intent.getIntExtra("position1", 0)
            val position2: Int = intent.getIntExtra("position2", 0)
            if(isAutomatic) {
                println("inside automatic")
                if (auto_stand >= auto_walk && auto_stand >= auto_run) {
                    type = 2 // standing
                } else if (auto_walk >= auto_stand && auto_walk >= auto_run) {
                    type = 1  // walking
                } else {
                    type = 0 // running
                }
                typeTitle.text = "Type: " + activityTypeArray[type]
            } else if (position1 == 1) { // if GPS
                typeTitle.text = "Type: " + activityTypeArray[position2]
            }
            calorieTitle.text = String.format("Calories: %.2f", calories)

            if(unitValue == "0") {
                distanceTitle.text = String.format("Distance: %.2f kms", distance)
                climbTitle.text = String.format("Climb: %.2f kms", climb)
                curSpeedTitle.text = String.format("Cur speed: %.2f kms/h", curSpeed)
                avgSpeedTitle.text = String.format("Avg speed: %.2f kms/h", avgSpeed)
            } else {
                climbTitle.text = String.format("Climb: %.2f miles", climb)
                distanceTitle.text = String.format("Distance: %.2f miles", distance)
                curSpeedTitle.text = String.format("Cur speed: %.2f m/h", curSpeed)
                avgSpeedTitle.text = String.format("Avg speed: %.2f m/h", avgSpeed)
            }

            val latLng = LatLng(lat, lng)
            latlngArray.add(latLng)
            println("check: " + duration)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            if (!mapCentered) {

                mMap.animateCamera(cameraUpdate)
                markerOptions.position(latLng)
                movingMarker.position(latLng)
                mMap.addMarker(markerOptions)
                mMap.addMarker(movingMarker)
                polylineOptions.add(latLng)
                mapCentered = true
            }
            now = mMap.addMarker(MarkerOptions().position(latLng))!!
            mMap.animateCamera(cameraUpdate)
            markerOptions.position(latLng)

            polylineOptions.add(latLng)
            mMap.addPolyline(polylineOptions)
        }
    }

    override fun onMapClick(latLng: LatLng) {}

    override fun onMapLongClick(latLng: LatLng) {}

    fun onMapSaveClick(view: View) {
        val position1: Int = intent.getIntExtra("position1", 0)
        val position2: Int = intent.getIntExtra("position2", 0)

        val thread = Thread {

            // set entryEntity
            entryEntity.inputType = position1
            println("position $position1")
            println("position2 $position2")

            if(isAutomatic) {
                entryEntity.activityType = type
            } else {
                entryEntity.activityType = position2
            }

            if(timeString == "" && dateString == ""){
                entryEntity.dateTime = SimpleDateFormat("hh:mm:ss MMM dd yyyy").format(Date())
            } else if(dateString == ""){
                dateString = SimpleDateFormat("MMM dd yyyy").format(Date())
                entryEntity.dateTime = timeString + dateString
            } else if(timeString == "") {
                timeString = SimpleDateFormat("hh:mm:ss ").format(Date())
                println("date and time1" + timeString)
                entryEntity.dateTime = timeString + dateString
            } else {
                entryEntity.dateTime = timeString + dateString
            }
            println("date and time" + timeString + dateString)

            entryEntity.duration = duration / 60
            entryEntity.distance = distance
            entryEntity.calorie = calories
            entryEntity.heartRate = climb //climb
            entryEntity.avgSpeed = avgSpeed
//            entryEntity.curSpeed = curSpeed
            entryEntity.locationList = fromLatLngToString(latlngArray)

            println("check check $sec $distance $calories $avgSpeed")

            // insert into database
            vm.insert(entryEntity)
        }
        thread.start()
        this@MapActivity.finish()
    }

    fun onMapCancelClick(view: View) {
        this@MapActivity.finish()
    }

    inner class MyTask : TimerTask() {
        override fun run() {
            val inst: Instance = DenseInstance(Globals.ACCELEROMETER_BLOCK_CAPACITY+2)
            inst.setDataset(mDataset)
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE
            while (true) {
                try {
                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    println("blocksize $blockSize")
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0
                        println("inside if")

                        // time = System.currentTimeMillis();
                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                                    * im[i])
                            inst.setValue(i, mag)
                            im[i] = .0 // Clear the field
                        }

                        // Append max after frequency component
                        inst.setValue(Globals.ACCELEROMETER_BLOCK_CAPACITY, max)
                        mDataset.add(inst)
                        val autoActivityType = WekaClassifier().classify(inst.toDoubleArray().toTypedArray())
                        if (autoActivityType == 1.0) {
                            type = 1 // walking
                            auto_walk += 1
                        } else if (autoActivityType == 2.0) {
                            type = 0 // running
                            auto_run += 1
                        } else {
                            type = 2 // standing
                            auto_stand += 1
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

    fun fromLatLngToString(array: ArrayList<LatLng>): String {
        var string = ""
        for (s in array) string += "${s.latitude}, ${s.longitude}, "
        println("latlng convert" + string)

        return string
    }
}