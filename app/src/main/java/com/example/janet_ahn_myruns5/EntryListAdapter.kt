package com.example.janet_ahn_myruns5

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager

class EntryListAdapter(private val context: Context, private var entryList: List<Entries>) : BaseAdapter(){

    val inputTypeArray: Array<String> = arrayOf("Manual Entry", "GPS", "Automatic")
    val activityTypeArray: Array<String> = arrayOf("Running", "Walking", "Standing", "Cycling",
                                                    "Hiking", "Downhill Skiing", "Cross-Country Skiing",
                                                    "Snowboarding", "Skating", "Swimming", "Mountain Biking",
                                                    "Wheelchair", "Elliptical", "Other")

    private lateinit var unitArray: Array<String>
    private var unit: Int = 0

    private var KM_TO_MILES = 0.621371

    override fun getItem(position: Int): Any {
        return entryList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return entryList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.entrylist_adapter,null)

        val firstLineID = view.findViewById(R.id.firstLine) as TextView
        val secondLineID = view.findViewById(R.id.secondLine) as TextView

        val distanceString: String = entryList.get(position).distance.toString()
        unitArray = context.resources.getStringArray(R.array.unit_preferences);
        val unitType: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val unitValue = unitType!!.getString("unit_key","0")
        var distanceFinal = ""
        if(unitValue == "0") {
            val formatedDistance = String.format("%.2f", distanceString.toDouble())
            distanceFinal = formatedDistance + " Kms, "
        } else {
            var distanceDouble: Double = entryList.get(position).distance
            distanceDouble = distanceDouble * KM_TO_MILES
            val roundedDistance: String = String.format("%.2f", distanceDouble)
            distanceFinal = roundedDistance + " Miles, "
        }


        val timeString = entryList.get(position).duration.toString()
        val minuteOfTime = timeString.indexOf(".")
        val minutes: String = timeString.substring(0,minuteOfTime) + "mins "
//        val seconds: String = timeString.substring(minuteOfTime) + "secs"
        val secondsToInt = timeString.substring(minuteOfTime).toDouble()
        val secondsFinalInt = secondsToInt * 60
        println("INTCHECK" + secondsFinalInt)
        val secondsFinal =  String.format("%.2f", secondsFinalInt) + "secs"//seconds.replace(".","")

        firstLineID.text = inputTypeArray[entryList.get(position).inputType] + ": " +
                activityTypeArray[entryList.get(position).activityType] + ", " + entryList.get(position).dateTime
        secondLineID.text = distanceFinal+ minutes + secondsFinal
        return view
    }

    fun replace(newEntryList: List<Entries>){
        entryList = newEntryList
    }
}