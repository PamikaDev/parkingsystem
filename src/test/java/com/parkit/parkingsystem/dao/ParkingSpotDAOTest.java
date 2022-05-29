package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

  @Mock
  private DataBaseConfig databaseConfig;
  @Mock
  private Connection con;
  @Mock
  private PreparedStatement ps;
  @Mock
  private ResultSet rs;
  @Mock
  private ParkingSpot parkingSpot;

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
  void getNextAvailableSlotTest_For_Bike_ShouldReturnTrue()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, true);
    // when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(parkingId).isEqualTo(4);
    verify(ps, times(0)).executeQuery();
    verify(rs, times(0)).next();

  }

  @Test
  void getNextAvailableSlotTest_For_Bike_ShouldassertException()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, false);
    // when(parkingSpot.getParkingType()).thenReturn(null);

    // WHEN
    parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  void getNextAvailableSlotTest_For_Bike_ShouldReturnFalse()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    // when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

    // WHEN
    parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.BIKE);
    verify(ps, times(0)).executeQuery();
    verify(rs, times(0)).next();
  }

  @Test
  void getNextAvailableSlotTestrsNextFalse() throws SQLException, ClassNotFoundException {

    // GIVEN
    // when(rs.next()).thenReturn(false);
    // WHEN
    parkingSpotDAOUnderTest.getNextAvailableSlot(null);

    // THEN
    assertFalse(parkingSpot.isAvailable());

  }

  @Test
  void getNextAvailableSlotTest_For_Car_ShouldReturnTrue()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN

    assertTrue(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(2);

  }

  @Test
  void getNextAvailableSlotTest_For_Car_ShouldassertException()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, null, false);

    // WHEN
    parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));
  }

  @Test
  void getNextAvailableSlotTest_For_Car_ShouldReturnFalse()
      throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(2);
  }

  @Test
  void updateParkingTestShouldReturnTrue() throws SQLException, ClassNotFoundException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // WHEN
    boolean isUpdateParking = parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertTrue(isUpdateParking);
  }

  @Test
  void updateParkingTestFailourShouldassertException() throws SQLException {

    // GIVEN
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(null);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error updating parking info"));
  }

  @Test
  void updateParkingTestShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // GIVEN
    int updateRowCount = ps.executeUpdate();
    when(parkingSpot.isAvailable()).thenReturn(true);
    when(parkingSpot.getId()).thenReturn(updateRowCount);

    // WHEN
    boolean isUpdateParking = parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertFalse(isUpdateParking);
    verify(ps, times(1)).executeUpdate();
  }

}