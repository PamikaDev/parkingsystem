package com.parkit.parkingsystem.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

  private DataBaseConfig dataBaseConfig = new DataBaseConfig();
  private static final Logger logger = LogManager.getLogger("TicketDAO");

  public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
    this.dataBaseConfig = dataBaseConfig;
  }

  public boolean saveTicket(Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);

      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(5,
          (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
      return ps.execute();
    } catch (Exception ex) {
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeConnection(con);

    }
    return false;
  }

  public Ticket getTicket(String vehicleRegNumber)
      throws ClassNotFoundException, SQLException, IOException {
    Ticket ticket = null;
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
      ps.setString(1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1),
            ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
        dataBaseConfig.closeResultSet(rs);
      }
    }

    return ticket;
  }

  public boolean updateTicket(Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      logger.error("Error saving ticket info", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return false;
  }

  // Check if a vehicle reg number is for a recurring user
  public boolean isRecurring(String vehicleRegNumber) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_RECURRING_VEHICLE)) {
      ps.setString(1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeResultSet(rs);
        return true;
      }
    } catch (Exception ex) {
      logger.error("Error checking vehicle reg number is for a recurring user", ex);
    }
    return false;
  }

  // Check if vehicle Reg Number is saved
  public boolean isSaved(String vehicleRegNumber) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_SAVED_TICKET)) {
      ps.setString(1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeResultSet(rs);
        return true;
      }
    } catch (Exception ex) {
      logger.error("Error saving vehicle Reg Number", ex);
    }
    return false;
  }
}