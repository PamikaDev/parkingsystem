package com.parkit.parkingsystem.dao;

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

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public DataBaseConfig getDataBaseConfig() {
		return dataBaseConfig;
	}

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
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		Ticket ticket = null;
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					ticket = new Ticket();
					ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),
							false);
					ticket.setParkingSpot(parkingSpot);
					ticket.setId(rs.getInt(2));
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(rs.getDouble(3));
					ticket.setInTime(rs.getTimestamp(4));
					ticket.setOutTime(rs.getTimestamp(5));
				}

			} catch (Exception ex) {
				logger.error("Error fetching next available slot", ex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
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
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		}
		return false;
	}

	// Check if a vehicle reg number is for a recurring user
	public boolean isRecurring(String vehicleRegNumber) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_RECURRING_VEHICLE)) {
			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					dataBaseConfig.closePreparedStatement(ps);
					return true;
				}
			} catch (Exception ex) {
				logger.error("Error fetching recurring vehicle ", ex);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	// Check if vehicle Reg Number is saved
	public boolean isSaved(String vehicleRegNumber) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_SAVED_TICKET)) {

			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					dataBaseConfig.closePreparedStatement(ps);
					return true;
				}
			} catch (Exception ex) {
				logger.error("Error fetching recurring vehicle ", ex);
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
