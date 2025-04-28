package com.example.janet_ahn_myruns5

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.janet_ahn_myruns5.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class HistoryMapActivity : AppCompatActivity(), OnMapReadyCallback,
GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
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

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    private lateinit var markerOptions: MarkerOptions
    private lateinit var secondMarker: MarkerOptions
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polylines: ArrayList<Polyline>
    private lateinit var latLngArray: ArrayList<LatLng>

    private lateinit var database: AppDatabase
    private lateinit var databaseDao: EntriesDao
    private lateinit var repository: EntryRepository
    private lateinit var vm: RoomViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    private lateinit var unitArray: Array<String>
    private var unit: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_history_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapSecond) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        database = AppDatabase.getInstance(this)
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(this, viewModelFactory).get(RoomViewModel::class.java)

        typeTitle = findViewById(R.id.type_text_history)
        avgSpeedTitle = findViewById(R.id.avgspeed_text_history)
        curSpeedTitle = findViewById(R.id.curspeed_text_history)
        climbTitle = findViewById(R.id.climb_text_history)
        calorieTitle = findViewById(R.id.calorie_text_history)
        distanceTitle = findViewById(R.id.distance_text_history)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setOnMapClickListener(this)
        mMap.setOnMapLongClickListener(this)
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        secondMarker = MarkerOptions()

        unitArray = resources.getStringArray(R.array.unit_preferences);
        val unitType: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val unitValue = unitType!!.getString("unit_key","0")
        if(unitValue == "0") {
            unit = 0
        } else {
            unit = 1
        }

        vm.allEntriesLiveData.observe(this, Observer { it ->
            var position: Int = intent.getIntExtra("entryPosition", 0)
            var temp = it.get(position).locationList

            latLngArray = fromStringToLatLng(temp)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngArray.first(), 17f)
            mMap.animateCamera(cameraUpdate)
            markerOptions.position(latLngArray.last())
            secondMarker.position(latLngArray.first())
            mMap.addMarker(markerOptions)
            mMap.addMarker(secondMarker)

            for (i in 0 until latLngArray.size - 1) {
                val src: LatLng = latLngArray[i]
                val dest: LatLng = latLngArray[i + 1]

                // mMap is the Map Object
                mMap.addPolyline(
                    PolylineOptions().add(
                        LatLng(src.latitude, src.longitude),
                        LatLng(dest.latitude, dest.longitude)
                    ).color(Color.BLACK).geodesic(true)
                )
            }

//            inputTypeEdit.setText(inputTypeArray[it.get(position).inputType])
            typeTitle.setText("Type: " + activityTypeArray[it.get(position).activityType])


            println("UNITTYPE: " + unitType)
            if(unit == 0) {
                distanceTitle.text = String.format("Distance: %.2f Km", it.get(position).distance)
                avgSpeedTitle.text = String.format("Avg speed: %.2f Km", it.get(position).avgSpeed)
                climbTitle.text = String.format("Climb: %.2f Km", it.get(position).heartRate)
                curSpeedTitle.text = "Cur speed: " + it.get(position).curSpeed + " Kms/h"
            } else {
                distanceTitle.text = String.format("Distance: %.2f Miles", it.get(position).distance)
                avgSpeedTitle.text = String.format("Avg speed: %.2f Miles", it.get(position).avgSpeed)
                climbTitle.text = String.format("Climb: %.2f Miles", it.get(position).heartRate)
                curSpeedTitle.text = "Cur speed: " + it.get(position).curSpeed + " Miles"
            }
            calorieTitle.text = String.format("Calories: %.2f Miles", it.get(position).calorie)

        })
    }

    override fun onMapClick(latLng: LatLng) {
        for (i in polylines.indices) polylines[i].remove()
        polylineOptions.points.clear()
    }

    override fun onMapLongClick(latLng: LatLng) {
        markerOptions.position(latLng!!)
        mMap.addMarker(markerOptions)
        polylineOptions.add(latLng)
        polylines.add(mMap.addPolyline(polylineOptions))
    }

    fun fromStringToLatLng(s: String): ArrayList<LatLng> {
        val newArray: ArrayList<LatLng> = arrayListOf()
//        val string = s.split(",")
        val strs = s.split(",")
        println("strings:" + strs)
        var odd = 1
        var even = 0
        var length = strs.size
        println("length: " + length)
        while(odd < length){
            newArray.add(LatLng(strs[even].toDouble(), strs[odd].toDouble()))

            even += 2
            odd += 2
            println("odd: " + odd)
        }

        return newArray;
    }

    // for delete button in top right corner
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        database = AppDatabase.getInstance(this)
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(this, viewModelFactory).get(RoomViewModel::class.java)
        var position1: Int = intent.getIntExtra("entryPosition", 0)
        var entryID: Long = 0
        vm.allEntriesLiveData.observe(this, Observer { it ->
            entryID = it.get(position1).id
        })
        val id: Int = item.itemId
        if (id == R.id.deleteButton) {
            val thread = Thread {
                vm.delete(entryID)
            }
            thread.start()
        }
        return true
    }
}