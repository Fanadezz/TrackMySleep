package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassesTracker

//variables to distinguish between item's view type
private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

/*using ListAdapter that helps you build a RecyclerView adapter backed by a list
The adapter extends ListAdapter which take data class and ViewHolder as generic
parameters*/
class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(SleepNightDiffCallBack()) {
private val adapterScope = CoroutineScope(Dispatchers.Default)

    /*var data = listOf<SleepNight>()
        *//*To tell the RecyclerView when the data that it's displaying has changed,
         add a custom setter to the data variable, in the setter, give data a new
         value, then call notifyDataSetChanged() to trigger redrawing the list
         with the new data.*//*
        set(value) {
            field = value
            notifyDataSetChanged()
        }*/


    /*inflates an xml layout and returns a viewHolder, called when the
    RecyclerView needs a view holder to represent an item.*/

    /*The parent parameter, which is the view group that holds the view holder,
     is always the RecyclerView. The viewType parameter is used when there are
      multiple views in the same RecyclerView. e.g. if you put a list of
      text views, an image, and a video all in the same RecyclerView, the
      onCreateViewHolder() function would need to know what type of view to use.*/


    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){

            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){

            ITEM_VIEW_TYPE_HEADER ->TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else ->throw ClassCastException("Unknown viewType $viewType")
        }
    }


    fun addHeaderAndSubmitList(list:List<SleepNight>?) {

        adapterScope.launch{

            val items = when(list){
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }

            }
withContext(Main){
    submitList(items)
}

        }

    }
/*
    //returns the size of the list
    override fun getItemCount(): Int {

*//*when using ListAdapter, getItemCount is not neede/d as ListAdapter implements this method for
you*//*
        return data.size
    }*/


    //called to display the data for one-list-item at a specified position
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //getItem() is provided by ListAdapter
     //   val item = getItem(position)!!

      //  holder.bind(item, clickListener)

        when(holder){
            is ViewHolder -> {

                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }

    }

    //2nd ViewHolder for the Header

class TextViewHolder(view: View):RecyclerView.ViewHolder(view){

    companion object{

        fun from(parent: ViewGroup):TextViewHolder{

            val layoutInflater= LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header,parent,false)
            return TextViewHolder(view)
        }
    }
}

    //1st ViewHolder for the SleepNight Items
    //binding.root is the root ConstraintLayout in the itemLayout.xml
    class ViewHolder private constructor(val binding: ListItemSleepNightBinding) :
        RecyclerView.ViewHolder(binding.root) {


        /*If RecyclerView needs to access the views stored in the ViewHolder,
         it does so using the view holder's itemView property.*/


        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            /*assign sleep to item because you need to tell the binding
            object about your new SleepNight*/
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            //these not needed as data binding and new adapters have taken care of this
            /*binding.sleepLength.text =
                   convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
           binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
           binding.qualityImage.setImageResource(when (item.sleepQuality) {

               0 -> R.drawable.ic_sleep_0
               1 -> R.drawable.ic_sleep_1
               2 -> R.drawable.ic_sleep_2
               3 -> R.drawable.ic_sleep_3
               4 -> R.drawable.ic_sleep_4
               5 -> R.drawable.ic_sleep_5
               else -> R.drawable.ic_sleep_active
           })*/
            //res variable holds a reference for this view
            //val res = itemView.context.resources
            //assign sleep to item because you need to tell the binding object about your new SleepNight
            //binding.sleep = item
            /*this is an optimization call that asks data binding to execute any pending bindings
             right away- it is always a good idea t call it*/
            binding.executePendingBindings()

        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //layout inflater instance
                val layoutInflater = LayoutInflater.from(parent.context)

                //creating the view
                /*(the 3rd arguments argument needs to be false, because RecyclerView adds
                this item to the view hierarchy for you when it's time.)*/

                //delete this line inside the onCreateViewHolder()
                val view = layoutInflater.inflate(
                    R.layout.list_item_sleep_night,
                    parent, false
                )


                /*Get a reference to the binding object(DataBinding Utils not applicable here)
                ListItemSleepNightBinding is from the layout fragment file
                list_item_sleep_night.xml*/

                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)


                //instead of returning the view, return binding and change the ViewHolder Signature
                /*return ViewHolder(view)*/

                return ViewHolder(binding)
            }
        }
    }
}


/*Upgrading adapter to use DiffUtil callback to optimize the RecyclerView
   The callback takes SleepNight as a generic parameter*/
class SleepNightDiffCallBack : DiffUtil.ItemCallback<DataItem>() {

    //checking if an item was added
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    /*check whether oldItem and newItem contain the same data; that is, whether
    they are equal. This equality check will check all the fields, because SleepNight
     is a data class. Data classes automatically define equals and a few other methods
      for you. If there are differences between oldItem and newItem, this code tells
      DiffUtil that the item has been updated.*/
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}


class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {

    fun onClick(night: SleepNight) = clickListener(night.nightId)
}



/* sealed class - subclasses are defined in this class. As a result, the number of subclasses
 is known to the compiler. It's not possible for another part of your code to define a new
 type of DataItem that could break your adapter*/
sealed class DataItem {

    //Diff needs to know the id of each item - subclasses have to override variable id
    abstract val id:Long



    //wrapper class for SleepNights - to make it part of the sealed class it extends DataItem
    data class SleepNightItem(val sleepNight:SleepNight): DataItem(){
        override val id =sleepNight.nightId
    }


   /* wrapper class for the Header - defined as an 'Object' so that there will be only one
    instance of Header*/
    object Header:DataItem(){
        //this id will never conflict with the nightId - i.e negative 2 to power 63
        override val id = Long.MIN_VALUE
    }


}
/*//assign sleep to item because you need to tell the binding object about your new SleepNight
            binding.sleep = item



            //these not needed as data binding and new adapters have taken care of this
             /*binding.sleepLength.text =
                    convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(when (item.sleepQuality) {

                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })*/
            //res variable holds a reference for this view
            //val res = itemView.context.resources
            //assign sleep to item because you need to tell the binding object about your new SleepNight
            binding.sleep = item
            /*this is an optimization call that asks data binding to execute any pending bindings
             right away- it is always a good idea t call it*/
            binding.executePendingBindings()*/





