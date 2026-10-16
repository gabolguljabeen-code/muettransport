package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BoardingPassDao
import com.example.data.dao.RouteDao
import com.example.data.dao.ShuttleDao
import com.example.data.dao.StopRequestDao
import com.example.data.dao.TrafficAlertDao
import com.example.data.model.BoardingPassEntity
import com.example.data.model.RouteEntity
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.data.model.TrafficAlertEntity

@Database(
    entities = [
        ShuttlePoint::class,
        RouteEntity::class,
        StopPointEntity::class,
        BoardingPassEntity::class,
        StopRequestEntity::class,
        TrafficAlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shuttleDao(): ShuttleDao
    abstract fun routeDao(): RouteDao
    abstract fun boardingPassDao(): BoardingPassDao
    abstract fun stopRequestDao(): StopRequestDao
    abstract fun trafficAlertDao(): TrafficAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muet_transit.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
