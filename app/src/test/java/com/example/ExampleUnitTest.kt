package com.example

import com.example.data.model.ShuttlePoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun shuttlePoint_seatAvailabilityCalculation() {
        val bus = ShuttlePoint(
            id = "POINT_27",
            pointNumber = "Point 27",
            busPlate = "EB-0109",
            routeId = "R_LATIFABAD",
            routeName = "Route 1",
            driverName = "Ustad Ghulam Rasool",
            driverPhone = "+92 300 7862700",
            totalSeats = 52,
            occupiedSeats = 38,
            standingPassengers = 4
        )

        assertEquals(14, bus.availableSeats)
        assertTrue(bus.occupancyPercentage > 0.7f)
        assertEquals("14 Seats Available", bus.seatStatus)
    }

    @Test
    fun shuttlePoint_busFullStatus() {
        val bus = ShuttlePoint(
            id = "POINT_23",
            pointNumber = "Point 23",
            busPlate = "EB-0102",
            routeId = "R_SADDAR",
            routeName = "Route 2",
            driverName = "Ustad Allah Dino",
            driverPhone = "+92 301 5552300",
            totalSeats = 52,
            occupiedSeats = 52,
            standingPassengers = 10
        )

        assertEquals(0, bus.availableSeats)
        assertEquals(1.0f, bus.occupancyPercentage, 0.001f)
        assertEquals("Bus Full", bus.seatStatus)
    }
}
