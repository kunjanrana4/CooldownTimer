package com.kunjan.cooldowntimer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.*

class MainActivity : AppCompatActivity() {
    private var latitude1:Double = 0.0
    private var longitude1:Double = 0.0
    private var latitude2 = 0.0
    private var longitude2:Double = 0.0
    private val regex = "-?\\d+(\\.\\d+)?,\\s?-?\\d+(\\.\\d+)?".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_format.setOnClickListener{
            formatCoordinates()
        }

        btn_calculate_cooldown.setOnClickListener{
            calculateDistance()
        }

        btn_first_paste.setOnClickListener{
            val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val item:ClipData.Item? = clipboard.primaryClip?.getItemAt(0)
            edit_first_coordinates.setText(item?.text.toString())
        }

        btn_second_paste.setOnClickListener{
            val clipboard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val item:ClipData.Item? = clipboard.primaryClip?.getItemAt(0)
            edit_second_coordinates.setText(item?.text.toString())
        }
    }

    private fun formatCoordinates() {
        val coordinate1 = regex.find(edit_first_coordinates.text.toString())
        edit_first_coordinates.setText(coordinate1?.value.toString())
        val coordinate2 = regex.find(edit_second_coordinates.text.toString())
        edit_second_coordinates.setText(coordinate2?.value.toString())
    }

    private fun calculateDistance() {
        if(regex.matches(edit_first_coordinates.text.toString()) && regex.matches(edit_second_coordinates.text.toString())){
            getCoordinates()
            latitude1 = Math.toRadians(latitude1)
            longitude1 = Math.toRadians(longitude1)
            latitude2 = Math.toRadians(latitude2)
            longitude2 = Math.toRadians(longitude2)
            val distanceLatitude = latitude2 - latitude1
            val distanceLongitude = longitude2 - longitude1
            val a = (sin(distanceLatitude / 2).pow(2.0)
                    + (cos(latitude1) * cos(latitude2)
                    * sin(distanceLongitude / 2).pow(2.0)))
            val c = 2 * asin(sqrt(a))
            val r = 6371.0
            val distance = (c*r).toString() + " KMs"
            text_total_distance.text = distance
            calculateCooldownTimer(c*r)
        }
        else{
            Toast.makeText(this,"Click format button to format your coordinates.",Toast.LENGTH_LONG).show()
        }
    }

    private fun getCoordinates() {
        val c1 = edit_first_coordinates.text.toString().replace("\\s".toRegex(),"")
        val coordinate1 = c1.split(",")
        latitude1 = coordinate1[0].toDouble()
        longitude1 = coordinate1[1].toDouble()

        val c2 = edit_second_coordinates.text.toString().replace("\\s".toRegex(),"")
        val coordinate2 = c2.split(",")
        latitude2 = coordinate2[0].toDouble()
        longitude2 = coordinate2[1].toDouble()
        if(!regex.matches(edit_first_coordinates.text.toString())){
            Toast.makeText(this,"Please enter valid coordinates in Latitude, Longitude Format",Toast.LENGTH_LONG).show()
            edit_first_coordinates.setText("")
        }
        if(!regex.matches(edit_second_coordinates.text.toString())){
            Toast.makeText(this,"Please enter valid coordinates in Latitude, Longitude Format",Toast.LENGTH_LONG).show()
            edit_second_coordinates.setText("")
        }
    }

    private fun calculateCooldownTimer(distance:Double) {
        val time:Int = when {
            distance<1 -> 33
            distance<5 -> 120
            distance<10 -> 360
            distance<25 -> 660
            distance<30 -> 840
            distance<65 -> 1320
            distance<81 -> 1500
            distance<100 -> 2100
            distance<250 -> 2700
            distance<500 -> 3600
            distance<750 -> 4500
            distance<1000 -> 5400
            else -> 7200
        }
        val finalTime = when {
            time<60 -> time.toString()+ "seconds"
            time<3600 -> (time/60).toString() + "minutes " + (time%60).toString() + "seconds"
            else -> (time/3600).toString() + "hours " + (time%3600).toString() + "minutes"
        }
        text_cooldown_time.text = finalTime
    }
}