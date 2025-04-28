package com.example.janet_ahn_myruns5

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import java.util.*


class ActivityDetailsActivity : AppCompatActivity() {
    private lateinit var inputTypeEdit: EditText
    private lateinit var activityTypeEdit: EditText
    private lateinit var dateAndTimeEdit: EditText
    private lateinit var durationEdit: EditText
    private lateinit var distanceEdit: EditText
    private lateinit var caloriesEdit: EditText
    private lateinit var heartRateEdit: EditText

    private lateinit var database: AppDatabase
    private lateinit var databaseDao: EntriesDao
    private lateinit var repository: EntryRepository
    private lateinit var vm: RoomViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    private lateinit var unitArray: Array<String>
    private var unit: Int = 0

    val inputTypeArray: Array<String> = arrayOf("Manual Entry", "GPS", "Automatic")
    val activityTypeArray: Array<String> = arrayOf("Running", "Walking", "Standing", "Cycling",
        "Hiking", "Downhill Skiing", "Cross-Country Skiing",
        "Snowboarding", "Skating", "Swimming", "Mountain Biking",
        "Wheelchair", "Elliptical", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)

        unitArray = resources.getStringArray(R.array.unit_preferences);
        val unitType: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val unitValue = unitType!!.getString("unit_key","0")
        if(unitValue == "0") {
            unit = 0
        } else {
            unit = 1
        }

        var position: Int = intent.getIntExtra("entryList", 0)

        database = AppDatabase.getInstance(this)
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(this, viewModelFactory).get(RoomViewModel::class.java)

        // set text edits (used EditText for the underline)
        inputTypeEdit = findViewById(R.id.inputType)
        activityTypeEdit = findViewById(R.id.activityType)
        dateAndTimeEdit = findViewById(R.id.dateAndTime)
        durationEdit = findViewById(R.id.duration)
        distanceEdit = findViewById(R.id.distance)
        caloriesEdit = findViewById(R.id.calories)
        heartRateEdit = findViewById(R.id.heartRate)
        inputTypeEdit.isEnabled = false
        activityTypeEdit.isEnabled = false
        dateAndTimeEdit.isEnabled = false
        durationEdit.isEnabled = false
        distanceEdit.isEnabled = false
        caloriesEdit.isEnabled = false
        heartRateEdit.isEnabled = false

        // get all the entities at the position chosen
        vm.allEntriesLiveData.observe(this, Observer { it ->
            val timeString = it.get(position).duration.toString()
            val minuteOfTime = timeString.indexOf(".")
            val minutes: String = timeString.substring(0,minuteOfTime) + "mins "
//          val seconds: String = timeString.substring(minuteOfTime) + "secs"
            val secondsToInt = timeString.substring(minuteOfTime).toDouble()
            val secondsFinalInt = secondsToInt * 60

            val secondsFinal =  secondsFinalInt.toString() + "secs"

            inputTypeEdit.setText(inputTypeArray[it.get(position).inputType])
            activityTypeEdit.setText(activityTypeArray[it.get(position).activityType])
            dateAndTimeEdit.setText(it.get(position).dateTime)
            durationEdit.setText(minutes + secondsFinal)

            println("UNITTYPE: " + unitType)
            if(unit == 0) {
                distanceEdit.setText(it.get(position).distance.toString() + " Km")
            } else {
                distanceEdit.setText(it.get(position).distance.toString() + " Miles")
            }
            caloriesEdit.setText(it.get(position).calorie.toString() + " cals")
            heartRateEdit.setText(it.get(position).heartRate.toString() + " bpm")
        })
    }

    // for delete button in action bar
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
        var position1: Int = intent.getIntExtra("entryList", 0)
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