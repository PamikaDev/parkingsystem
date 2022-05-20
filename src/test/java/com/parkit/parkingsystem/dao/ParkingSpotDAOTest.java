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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {

  private static final Logger logger = LogManager.getLogger("ParkingSpotDAOTest");

  private static ParkingSpotDAO parkingSpotDAOUnderTest;
  private static DataBaseConfig dataBaseTestConfig;
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

  @BeforeAll
  public static void setUp() throws Exception {
  }

  @BeforeEach
  public void setUpPerTest() {
    logger.error("Error connecting to data base");
    logcaptor = LogCaptor.forName("ParkingSpotDAO");
    logcaptor.setLogLevelToInfo();
    dataBaseTestConfig = new DataBaseConfig();
    parkingSpotDAOUnderTest = new ParkingSpotDAO();
    parkingSpotDAOUnderTest.setDataBaseConfig(dataBaseTestConfig);
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
  void getNextAvailableSlotTest_For_Car() throws SQLException, ClassNotFoundException {

    // GIVEN
    when(parkingSpot.getParkingType()).thenReturn(ParkingType.CAR);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertFalse(parkingSpot.isAvailable());
    verify(ps, Mockito.times(0)).executeQuery();
    verify(rs, times(0)).next();
    assertThat(parkingId).isEqualTo(2);
    assertThat(parkingId)
        .isEqualTo(parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType()));

  }

  @Test
  void getNextAvailableSlotTest_For_Car_KO() throws SQLException, ClassNotFoundException {

    // GIVEN

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(null);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(-1);
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  void getNextAvailableSlotTest_For_Bike() throws SQLException, ClassNotFoundException {

    // GIVEN
    when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(4);
    assertThat(parkingId)
        .isEqualTo(parkingSpotDAOUnderTest.getNextAvailableSlot(parkingSpot.getParkingType()));

  }

  @Test
  void getNextAvailableSlotTest_For_Bike_KO() throws SQLException, ClassNotFoundException {

    // GIVEN

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest.getNextAvailableSlot(null);

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(parkingId).isEqualTo(-1);
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  void getNextAvailableSlotFailour() throws SQLException, ClassNotFoundException {

    // GIVEN

    when(parkingSpot.getParkingType()).thenReturn(null);

    // WHEN
    final int parkingId = parkingSpotDAOUnderTest
        .getNextAvailableSlot(parkingSpot.getParkingType());

    // THEN
    assertThat(parkingId).isEqualTo(-1);
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  void updateParkingTest() throws SQLException {

    // GIVEN
    int updateRowCount = ps.executeUpdate();
    when(parkingSpot.isAvailable()).thenReturn(true);
    when(parkingSpot.getId()).thenReturn(updateRowCount);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(parkingSpot);

    // THEN
    assertTrue(parkingSpot.isAvailable());
    verify(ps, times(1)).executeUpdate();

  }

  @Test
  void updateParkingTestFailour() throws SQLException {

    // GIVEN
    int updateRowCount = ps.executeUpdate();
    when(parkingSpot.isAvailable()).thenReturn(true);
    when(parkingSpot.getId()).thenReturn(updateRowCount);

    // WHEN
    parkingSpotDAOUnderTest.updateParking(null);

    // THEN
    assertFalse(parkingSpotDAOUnderTest.updateParking(parkingSpot));
    assertThat(logcaptor.getErrorLogs().contains("Error updating parking info"));

  }
}