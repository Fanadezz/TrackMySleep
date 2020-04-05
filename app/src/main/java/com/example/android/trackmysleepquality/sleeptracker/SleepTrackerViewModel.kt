/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

/**
 * ViewModel for SleepTrackerFragment.
 */


   class SleepTrackerViewModel(val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

private val _navigateToSleepDetail = MutableLiveData<Long>()
    val navigatetoSleepDetail
    get() = _navigateToSleepDetail

    /*job allows you to cancel all coroutines started by this ViewModel
    when viewModel is destroyed*/
    private var viewModelJob = Job()


  /* defining CoroutineScope(context) - A scope combines information about a
   coroutine's job and dispatcher. */
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)








//getting all nights from the database

   val nights = database.getAllNights()

    //formatting the Night object reference using the Util

    val nightString = Transformations.map(nights){

        nights -> formatNights(nights, application.resources)
    }


private val tonight = MutableLiveData<SleepNight>()


    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }


    private suspend fun getTonightFromDatabase(): SleepNight? {
        return  withContext(IO){


            var night = database.getTonight()
            if (night?.endTimeMilli!=night?.startTimeMilli){
                night =null
            }

            night
        }
    }

    //click handler for Start Button
   fun onStartTracking() {
        uiScope.launch {

            val newNight = SleepNight()

            insert(newNight)

            tonight.value = getTonightFromDatabase()
        }



    }



    private suspend fun insert(night:SleepNight){

        withContext(Dispatchers.IO){

            database.insert(night)
        }
    }

    //Click Handler for Stop Button

    fun onStopTracking() {
        uiScope.launch {
            //specifies the fxn from which this statement returns amongst the several nested fxns
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value =oldNight
        }
    }


    private suspend fun update(night: SleepNight){

        withContext(Dispatchers.IO ){
            database.update(night)
        }
    }


    //Click Handler for Clear Button

    fun onClear() {
        uiScope.launch {

            clear()
            tonight.value = null
        }
    }

    suspend fun clear(){

        withContext(Dispatchers.IO ){

            database.clear()
        }

        _showSnackBarEvent.value= true
    }


    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality :LiveData<SleepNight>
    get() {

        return _navigateToSleepQuality
    }


    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }


    val startButtonVisible = Transformations.map(tonight){it==null}
    val stopButtonVisible = Transformations.map(tonight){it!= null}
    val clearButtonVisible = Transformations.map(nights){
        it?.isNotEmpty()
    }



    private val _showSnackBarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
    get() =  _showSnackBarEvent


    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }
    //cancelling all coroutines when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
    }


    fun onSleepNightClicked(id: Long) {

        _navigateToSleepDetail.value = id
    }


    fun onSleepDetailNavigated() {
        _navigateToSleepDetail.value = null
    }

}

