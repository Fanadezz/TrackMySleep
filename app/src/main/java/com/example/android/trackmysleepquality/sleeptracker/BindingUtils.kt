package com.example.android.trackmysleepquality.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight


/*@BindingAdapter Annotation is used to tell data binding this is a binding
adapter fxn

As this fxn is the adapter for sleepDurationFormatted attribute,
 pass sleepDurationFormattedx as an argument to @BindingAdapter*/
@BindingAdapter("sleepDurationFormattedx")
fun TextView.setSleepDurationFormatted(item: SleepNight?) {

    /* because this is an extension fxn on TextView you can directly a
     access the text property even without using 'this' keyword */
    item?.let {  this.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli,
        context.resources) }

}

//2nd Adapter method for the quality_string TextView
@BindingAdapter("sleepQualityStringx")
fun TextView.setSleepQualityString(item: SleepNight?) {

    // you don't have to use 'this' to access text property

    item?.let { text = convertNumericQualityToString(item.sleepQuality, context.resources)  }

}





@BindingAdapter("sleepImagex")
fun ImageView.setSleepImage(item: SleepNight?) {
    item?.let {  this.setImageResource(when (item.sleepQuality) {
        0 -> R.drawable.ic_sleep_0
        1 -> R.drawable.ic_sleep_1
        2 -> R.drawable.ic_sleep_2
        3 -> R.drawable.ic_sleep_3
        4 -> R.drawable.ic_sleep_4
        5 -> R.drawable.ic_sleep_5

        else -> R.drawable.ic_sleep_active
    }

    ) }


}