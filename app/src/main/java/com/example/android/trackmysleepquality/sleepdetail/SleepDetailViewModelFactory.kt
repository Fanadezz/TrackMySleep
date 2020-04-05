package com.example.android.trackmysleepquality.sleepdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import java.security.Provider
import javax.sql.DataSource


class SleepDetailViewModelFactory (val sleepNightKey:Long, val dataSource:SleepDatabaseDao)
    :ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepDetailViewModel::class.java)){

            return SleepDetailViewModel(sleepNightKey, dataSource) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}