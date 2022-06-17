package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {

  private static DataBaseConfig dataBaseTestConfig = new DataBaseConfig();
  private static ParkingSpotDAO parkingSpotDAOUnderTest;
  private static LogCaptor logcaptor;
  private ParkingSpot parkingSpot;
  private ParkingType parkingType;

  @Mock
  private DataBaseConfig databaseConfig;
  @Mock
  private Connection con;
  @Mock
  private PreparedStatement ps;
  @Mock
  private ResultSet rs;

  @BeforeEach
  public void setUpPerTest() {
    logcaptor = LogCaptor.forName("ParkingSpotDAO");
    logcaptor.setLogLevelToInfo();
    parkingSpotDAOUnderTest = new ParkingSpotDAO();
    parkingSpotDAOUnderTest.setDataBaseConfig(dataBaseTestConfig);

  }

  @AfterAll
  public static void tearDown() {
    parkingSpotDAOUnderTest = null;

  }

  @Test
  void getNextAvailableSlotTestShouldReturnTrue() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    parkingType = ParkingType.CAR;

    // WHEN
    int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(parkingType);

    // THEN
    assertTrue(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(1);
  }

  @Test
  void getNextAvailableSlotTestShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    parkingType = ParkingType.BIKE;

    // WHEN
    int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(parkingType);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(4);
  }

  @Test
  void getNextAvailableSlotTestShouldassertException() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, false);

    // WHEN
    parkingSpotDAOUnderTest.getNextAvailableSlot(parkingType);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  void updateParkingTestShouldReturnTrue() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, true);

    // WHEN
    boolean isUpdateParking = parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertTrue(parkingSpot.isAvailable());
    assertTrue(isUpdateParking);
  }

  @Test
  void updateParkingTestFailourShouldassertException() throws SQLException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, false);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(null);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error updating parking info"));
  }

  @Test
  void updateParkingTestShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, false);
    parkingSpot.setAvailable(false);
    parkingSpot.setId(0);

    // WHEN
    boolean isUpdateParking = parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertFalse(isUpdateParking);
  }

}