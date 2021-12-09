package com.parkit.parkingsystem.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

  private DataBaseConfig dataBaseConfig = new DataBaseConfig();

  public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
    this.dataBaseConfig = dataBaseConfig;
  }

  public boolean saveTicket(Ticket ticket) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {
      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(5,
          ticket.getOutTime() == null ? null : new Timestamp(ticket.getOutTime().getTime()));
      return ps.execute();
    } catch (final Exception ex) {
    }
    return false;
  }

  public Ticket getTicket(String vehicleRegNumber)
      throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
    Ticket ticket = null;
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
      ps.setString(1, vehicleRegNumber);
      final ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        final ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1),
            ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
        dataBaseConfig.closeResultSet(rs);
      }
    } // catch (final Exception ex) {
//		}
    return ticket;
  }

  public boolean updateTicket(Ticket ticket) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (final Exception ex) {
    }
    return false;
  }

  // Check if a vehicle reg number is for a recurring user
  public boolean isRecurring(String vehicleRegNumber) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_RECURRING_VEHICLE)) {
      ps.setString(1, vehicleRegNumber);
      final ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeResultSet(rs);
        return true;
      }
    } catch (final Exception ex) {
    }
    return false;
  }

  // Check if vehicle Reg Number is saved
  public boolean isSaved(String vehicleRegNumber) {
    try (Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(DBConstants.GET_SAVED_TICKET)) {
      ps.setString(1, vehicleRegNumber);
      final ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeResultSet(rs);
        return true;
      }
    } catch (final Exception ex) {
    }
    return false;
  }
}
