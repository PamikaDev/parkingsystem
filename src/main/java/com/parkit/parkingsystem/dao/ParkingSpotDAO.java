package com.parkit.parkingsystem.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
  private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

  private DataBaseConfig dataBaseConfig = new DataBaseConfig();

  public DataBaseConfig getDataBaseConfig() {
    return dataBaseConfig;
  }

  public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
    this.dataBaseConfig = dataBaseConfig;
  }

  public int getNextAvailableSlot(ParkingType parkingType) throws Exception, SQLException {
    int result = -1;
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)) {

      ps.setString(1, parkingType.toString());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }

    } finally {
      dataBaseConfig.closeResultSet(null);
    }
    return result;
  }

  public boolean updateParking(ParkingSpot parkingSpot)
      throws ClassNotFoundException, FileNotFoundException, IOException {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)) {

      ps.setBoolean(1, parkingSpot.isAvailable());
      ps.setInt(2, parkingSpot.getId());
      int updateRowCount = ps.executeUpdate();
      return (updateRowCount == 1);
    } catch (SQLException ex) {
      logger.error("Error updating parking info", ex);
    } finally {
      dataBaseConfig.closeConnection(null);
      dataBaseConfig.closePreparedStatement(null);
    }
    return false;
  }

}