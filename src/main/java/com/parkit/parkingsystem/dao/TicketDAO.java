package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	private static DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public boolean saveTicket(Ticket ticket) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, ticket.getOutTime() == null ? null : new Timestamp(ticket.getOutTime().getTime()));
			return ps.execute();
		} catch (final Exception ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		Ticket ticket = null;
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET, ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE)) {
			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					ticket = new Ticket();
					final ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),
							false);
					ticket.setParkingSpot(parkingSpot);
					ticket.setId(rs.getInt(2));
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(rs.getDouble(3));
					ticket.setInTime(rs.getTimestamp(4));
					ticket.setOutTime(rs.getTimestamp(5));
				}
			}
		} catch (final Exception ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET, ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);) {
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (final Exception ex) {
			logger.error("Error saving ticket info", ex);
		}
		return false;
	}

	// Check if a vehicle reg number is for a recurring user
	public static boolean isRecurring(String vehicleRegNumber) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_RECURRING_VEHICLE,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		} catch (final Exception ex) {
			logger.error("Error fetching recurring vehicle ", ex);
		}
		return false;
	}

	// Check if vehicle Reg Number is saved
	public boolean isSaved(String vehicleRegNumber) {
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_SAVED_TICKET,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ps.setString(1, vehicleRegNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		} catch (final Exception ex) {
			logger.error("Error fetching recurring vehicle ", ex);
		}
		return false;
	}

	public DataBaseConfig getDataBaseConfig() {
		return dataBaseConfig;
	}

	public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
		TicketDAO.dataBaseConfig = dataBaseConfig;
	}

}
