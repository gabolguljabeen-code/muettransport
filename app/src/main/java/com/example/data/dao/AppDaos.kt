package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BoardingPassEntity
import com.example.data.model.RouteEntity
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.data.model.TrafficAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShuttleDao {
    @Query("SELECT * FROM shuttle_points ORDER BY pointNumber ASC")
    fun getAllShuttles(): Flow<List<ShuttlePoint>>

    @Query("SELECT * FROM shuttle_points WHERE id = :id")
    fun getShuttleById(id: String): Flow<ShuttlePoint?>

    @Query("SELECT * FROM shuttle_points WHERE routeId = :routeId")
    fun getShuttlesByRoute(routeId: String): Flow<List<ShuttlePoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShuttles(shuttles: List<ShuttlePoint>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateShuttle(shuttle: ShuttlePoint)

    @Update
    suspend fun updateShuttle(shuttle: ShuttlePoint)

    @Query("UPDATE shuttle_points SET occupiedSeats = :occupied, standingPassengers = :standing, lastUpdated = :timestamp WHERE id = :pointId")
    suspend fun updateSeatAvailability(pointId: String, occupied: Int, standing: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE shuttle_points SET currentStopName = :currentStop, nextStopName = :nextStop, latitude = :lat, longitude = :lng, speedKmph = :speed, delayMinutes = :delay, lastUpdated = :timestamp WHERE id = :pointId")
    suspend fun updateLivePosition(pointId: String, currentStop: String, nextStop: String, lat: Double, lng: Double, speed: Int, delay: Int, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes ORDER BY code ASC")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getRouteById(id: String): RouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<RouteEntity>)

    @Query("SELECT * FROM stop_points WHERE routeId = :routeId ORDER BY orderIndex ASC")
    fun getStopsForRoute(routeId: String): Flow<List<StopPointEntity>>

    @Query("SELECT * FROM stop_points ORDER BY routeId, orderIndex ASC")
    fun getAllStops(): Flow<List<StopPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<StopPointEntity>)
}

@Dao
interface BoardingPassDao {
    @Query("SELECT * FROM boarding_passes WHERE studentRollNo = :rollNo LIMIT 1")
    fun getPassByRollNo(rollNo: String): Flow<BoardingPassEntity?>

    @Query("SELECT * FROM boarding_passes LIMIT 1")
    fun getPrimaryStudentPass(): Flow<BoardingPassEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPass(pass: BoardingPassEntity)
}

@Dao
interface StopRequestDao {
    @Query("SELECT * FROM stop_requests ORDER BY timestamp DESC")
    fun getAllStopRequests(): Flow<List<StopRequestEntity>>

    @Query("SELECT * FROM stop_requests WHERE pointId = :pointId ORDER BY timestamp DESC")
    fun getRequestsForPoint(pointId: String): Flow<List<StopRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: StopRequestEntity)

    @Query("UPDATE stop_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Long, status: String)
}

@Dao
interface TrafficAlertDao {
    @Query("SELECT * FROM traffic_alerts WHERE isActive = 1 ORDER BY timestamp DESC")
    fun getActiveAlerts(): Flow<List<TrafficAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: TrafficAlertEntity)

    @Query("UPDATE traffic_alerts SET isActive = 0 WHERE id = :id")
    suspend fun dismissAlert(id: Long)
}
