package com.example.janet_ahn_myruns5

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
class Entries {
    @PrimaryKey(autoGenerate = true) var id: Long = 0L //Primary Key
    @ColumnInfo(name = "InputType") var inputType: Int = 0 //Manual GPS or automatic
    @ColumnInfo(name = "ActivityType") var activityType: Int = 0 // running, cycling, etc..
    @ColumnInfo(name = "DateTime") var dateTime: String = SimpleDateFormat("hh:mm:ss MMM dd yyyy").format(Date())
    @ColumnInfo(name = "Duration") var duration: Double = 0.0
    @ColumnInfo(name = "Distance") var distance: Double = 0.0
    @ColumnInfo(name = "CurSpeed") var curSpeed: String = "n/a"
    @ColumnInfo(name = "AvgSpeed") var avgSpeed: Double = 0.0
    @ColumnInfo(name = "Calorie") var calorie: Double = 0.0
    @ColumnInfo(name = "HeartRate") var heartRate: Double = 0.0
    @ColumnInfo(name = "Comment") var comment: String = ""
    @ColumnInfo(name = "LocationList") var locationList: String = ""
}