package com.example.janet_ahn_myruns5

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class ManualEntryActivity : AppCompatActivity() {
    private var m_Text: String = ""

    private lateinit var database: AppDatabase
    private lateinit var databaseDao: EntriesDao
    private lateinit var repository: EntryRepository
    private lateinit var vm: RoomViewModel
    private lateinit var viewModelFactory: EntryViewModelFactory

    var entryEntity = Entries()
    var timeString: String = ""
    var dateString: String = ""
    var duration: Double = 0.0
    var distance: Double = 0.0
    var calories: Double = 0.0
    var heart_rate: Double = 0.0
    var comment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        database = AppDatabase.getInstance(this)
        databaseDao = database.entriesDao()
        repository = EntryRepository(databaseDao)
        viewModelFactory = EntryViewModelFactory(repository)
        vm = ViewModelProvider(this, viewModelFactory).get(RoomViewModel::class.java)
        val list = resources.getStringArray(R.array.manual_entries);

        val adapter = ArrayAdapter(this,
            R.layout.listview_item, list)

        val listView: ListView = findViewById(R.id.manualList)
        listView.adapter = adapter

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
        }

        var thisAYear = cal.get(Calendar.YEAR).toInt()
        var thisAMonth = cal.get(Calendar.MONTH).toInt()
        var thisADay = cal.get(Calendar.DAY_OF_MONTH).toInt()

        var thisAHour = cal.get(Calendar.HOUR_OF_DAY).toInt()
        var thisAMinute = cal.get(Calendar.MINUTE).toInt()

        listView.setOnItemClickListener { _, _, position, _ ->
            if (position == 0){
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, thisMonth, day ->
                    thisAMonth = thisMonth + 1
                    thisADay = day
                    thisAYear = year


                    val month: Int = thisMonth + 1
                    val monthString = convertToString(month)

                    var singleDay = day.toString()

                    if(day<10){
                        singleDay = "0" + day.toString()
                    }

                    dateString = "$monthString " + singleDay + " $year"
                    val newDate:Calendar = Calendar.getInstance()
                    newDate.set(year, thisMonth, day)
//                    mh.entryDate = newDate.timeInMillis // setting new date
                }, thisAYear, thisAMonth, thisADay)
                dpd.show()
                println("DATE IS :" + dateString)
            } else if (position == 1) {
                val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    thisAHour = hour
                    thisAMinute = minute


                    val hour: Int = hour
                    val minute = minute
                    val seconds = cal.get(Calendar.SECOND)

                    var hourString = hour.toString()
                    var minuteString = minute.toString()
                    var secondString = seconds.toString()

                    if(hour<10){
                        hourString = "0" + hour.toString()
                    }
                    if(minute<10) {
                        minuteString = "0" + minute.toString()
                    }
                    if(seconds<10) {
                        secondString = "0" + seconds.toString()
                    }

                    timeString = hourString + ":" + minuteString + ":" + secondString + " "
                    val newTime:Calendar = Calendar.getInstance()
                    newTime.set(Calendar.HOUR_OF_DAY, hour)
                    newTime.set(Calendar.MINUTE, minute)
                    newTime.set(Calendar.SECOND, seconds)
                }, thisAHour, thisAMinute, true)
                tpd.show()
                println("TIME IS: " + timeString)

            } else {
                showInputPopup(list[position], position)
            }
        }
    }

    private fun showInputPopup(title: String, position: Int) {
        val textInput = AlertDialog.Builder(this)
        textInput.setTitle(title)

        // show input dialog
        val input = EditText(this)
        when(position){
            2,3,4,5 -> input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL // takes in decimals
            6 -> input.inputType = InputType.TYPE_CLASS_TEXT
        }
        textInput.setView(input)

        textInput.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
            m_Text = input.text.toString()
            if(m_Text == "") {
                dialog.cancel()
            } else {
                when(position){
                    2-> duration = m_Text.toDouble()
                    3-> distance = m_Text.toDouble()
                    4-> calories = m_Text.toDouble()
                    5-> heart_rate = m_Text.toDouble()
                    6-> comment = m_Text
                }
            }
        })
        textInput.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        textInput.show()
    }

    fun onEntrySaveClick(view: View) {
        val position1: Int = intent.getIntExtra("position1", 0)
        val position2: Int = intent.getIntExtra("position2", 0)
        val thread = Thread {

            // set entryEntity
            entryEntity.inputType = position1
            entryEntity.activityType = position2
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
            entryEntity.duration = duration
            entryEntity.distance = distance
            entryEntity.calorie = calories
            entryEntity.heartRate = heart_rate
            entryEntity.comment = comment

            // insert into database
            vm.insert(entryEntity)
        }
        thread.start()
        // Toast to message on the screen
        Toast.makeText(this, "New entry added to database", Toast.LENGTH_LONG).show()
        this@ManualEntryActivity.finish()
    }

    fun onEntryCancelClick(view: View) {
        Toast.makeText(this@ManualEntryActivity, "Entry discarded", Toast.LENGTH_SHORT).show()
        this@ManualEntryActivity.finish()
    }

    fun convertToString(month: Int): String {
        var monthString = "default"
        when(month){
            1 -> monthString = "Jan"
            2 -> monthString = "Feb"
            3 -> monthString = "Mar"
            4 -> monthString = "Apr"
            5 -> monthString = "May"
            6 -> monthString = "June"
            7 -> monthString = "July"
            8 -> monthString = "Aug"
            9 -> monthString = "Sept"
            10 -> monthString = "Oct"
            11 -> monthString = "Nov"
            12 -> monthString = "Dec"
        }
        return monthString
    }
}


