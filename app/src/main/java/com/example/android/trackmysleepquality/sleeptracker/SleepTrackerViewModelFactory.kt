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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 */


//SleepTrackerViewModelFactory takes the same arguments as the ViewMode
//class SleepTrackerViewModelFactory(
//
//        private val dataSource: SleepDatabaseDao,
//        private val application: Application) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
//            return SleepTrackerViewModel(dataSource, application) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

//SleepTrackerViewModelFactory extends ViewModelProvider.Factory



  class SleepTrackerViewModelFactory (
        private val dataSource: SleepDatabaseDao,
        private val app:Application) :ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")

    // overrides create() generic function
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        /*isAssignableFrom() determines if the modelClass is either
        the same as, or is a superclass of the specified Class parameter.*/
        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)){
            return SleepTrackerViewModel(dataSource, app) as T
        }
        throw  IllegalArgumentException("Unknown View Model Class")
    }
  }