package com.example.janet_ahn_myruns5

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class Converter {
    @TypeConverter
    fun FromLatLngToString(array: ArrayList<LatLng>): String {
//        var newArray: ArrayList<Double> = arrayListOf()
//        var counter: Int = 0
//        for(item in array){
//            newArray.add(item.latitude)
//            newArray.add(item.longitude)
//        }
//        return newArray
        var string = ""
        for (s in array) string += "${s.latitude}, ${s.longitude}"

        return string
    }

    @TypeConverter
    fun FromStringToLatLng(s: String): ArrayList<LatLng> {
//        var newArray: ArrayList<LatLng> = arrayListOf()
//        var counter: Int = 1
//        for(item in array){
//            var temp: LatLng = LatLng(item, array[counter])
//            newArray.add(temp)
//            counter += 2
//        }
//        return newArray
        val newArray: ArrayList<LatLng> = arrayListOf()
//        val string = s.split(",")
        val strs = s.split(",").toTypedArray()
        var odd = 1
        for(num in strs){
            newArray.add(LatLng(num.toDouble(), strs[odd].toDouble()))
            odd += 2
        }

        return newArray;
    }

}