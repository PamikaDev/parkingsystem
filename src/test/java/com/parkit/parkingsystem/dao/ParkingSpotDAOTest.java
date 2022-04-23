package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
  public void setUpPerTest()
      throws ClassNotFoundException, FileNotFoundException, SQLException, IOException {
    con = dataBaseTestConfig.getConnection();
    logger.error("Error connecting to data base");
  }

  @AfterEach
  public void tearDownPerTest() {
    dataBaseTestConfig.closeConnection(con);
  }

  @AfterAll
  public static void tearDown() {
    parkingSpotDAOUnderTest = null;
  }

  /*
   * Testing of id number parking type CAR should return 1 because 1
   */
  @Test
  void getNextAvailableSlotTest_Car() throws SQLException, Exception {
    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(parkingId).isEqualTo(1);
  }

  /*
   * Testing of id number parking type BIKE return 4 the first id parking available for BIKE in the
   * DB test
   */
  @Test
  void getNextAvailableSlotTest_BIKE() throws SQLException, Exception {
    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(parkingId)
        .isEqualTo(parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType()));
  }

  @Test
  void updateParkingTest_forCAR()
      throws ClassNotFoundException, FileNotFoundException, IOException {
    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertTrue(parkingSpot.isAvailable());
  }

  @Test
  void updateParkingTest_forBIKE()
      throws ClassNotFoundException, FileNotFoundException, IOException {
    // GIVEN
    parkingSpot = new ParkingSpot(2, ParkingType.BIKE, true);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertTrue(parkingSpot.isAvailable());
  }

  @Test
  void updateParkingTestFailour()
      throws ClassNotFoundException, FileNotFoundException, IOException {
    parkingSpot = new ParkingSpot(0, null, false);
    assertFalse(parkingSpotDAOUnderTest.updateParking(parkingSpot));

  }
}