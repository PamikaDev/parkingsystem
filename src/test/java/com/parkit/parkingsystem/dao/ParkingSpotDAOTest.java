package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

class ParkingSpotDAOTest {

	private static ParkingSpotDAO parkingSpotDAOUnderTest;
	private static DataBaseConfig dataBaseTestConfig = new DataBaseConfig();
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAOTest");
	Connection con = null;

	private static ParkingSpot parkingSpot;

	@BeforeAll
	public static void setUp() throws Exception {
		parkingSpotDAOUnderTest = new ParkingSpotDAO();
		parkingSpotDAOUnderTest.setDataBaseConfig(dataBaseTestConfig);
	}

	@BeforeEach
	public void setUpPerTest() {
		try {
			con = dataBaseTestConfig.getConnection();
		} catch (Exception ex) {
			logger.error("Error connecting to data base", ex);

		}
	}

	@AfterEach
	public void tearDownPerTest() {
		dataBaseTestConfig.closeConnection(con);
	}

	@AfterAll
	public static void tearDown() {
		parkingSpotDAOUnderTest = null;
	}

	@Test
	void getNextAvailableSlotTest_Car() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		// WHEN
		int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		assertThat(parkingId).isEqualTo(1);
	}

	@Test
	void getNextAvailableSlotTest_BIKE() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		// WHEN
		int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType());

		// THEN
		assertThat(parkingId).isEqualTo(1);
	}

	@Test
	void updateParkingTest_forCAR() {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		// WHEN
		parkingSpotDAOUnderTest.updateParking(parkingSpot);

		// THEN
		assertTrue(parkingSpot.isAvailable());
	}

	@Test
	void updateParkingTest_forBIKE() {
		// GIVEN
		parkingSpot = new ParkingSpot(2, ParkingType.BIKE, true);

		// WHEN
		parkingSpotDAOUnderTest.updateParking(parkingSpot);

		// THEN
		assertTrue(parkingSpot.isAvailable());
	}

	@Test
	void updateParkingTestFailour() {
		parkingSpot = new ParkingSpot(0, null, false);
		assertFalse(parkingSpotDAOUnderTest.updateParking(parkingSpot));

	}
}