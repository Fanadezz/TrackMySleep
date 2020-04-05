

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//entities are the data class
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
//abstract class that can't be instantiated and extends RoomDatabase
abstract class SleepDatabase : RoomDatabase() {

    //needs to know about DAO and this value is used for testing

    abstract val sleepDatabaseDao: SleepDatabaseDao

    /*companion object allows clients to access the methods for getting the database
    without instantiating the class*/
    companion object {

        /*INSTANCE keeps reference to database once created
        it's marked with @Volatile to avoid caching making it always up-to-date*/

        @Volatile
        private var INSTANCE: SleepDatabase? = null

        /* get instance() takes a context which database builder needs
           it returns a Database Type*/

        fun getInstance(context: Context): SleepDatabase {
            //to ensure only one thread enters the block at a time

            synchronized(this) {
                //smart-cast in local variables

                var instance = INSTANCE
                //if-statament to check if instance is null i.e. there is no database yet

                if (instance == null) {
                    //invoking databaseBuilder(context, database class, database name)
                    instance = Room.databaseBuilder(context.applicationContext,
                            SleepDatabase::class.java, "sleep_history_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    //assign INSTANCE = instance as the final step inside if-statement
                    INSTANCE = instance
                }
                //return instance at the end of Synchronized block
                return instance
            }
        }
    }
}

