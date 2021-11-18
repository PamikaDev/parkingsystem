package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public int getNextAvailableSlot(ParkingType parkingType) {
//		Connection con = null;
		int result = 1;
		try (Connection con = dataBaseConfig.getConnection();
				final PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT,
						ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ps.setString(1, parkingType.toString());
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					result = rs.getInt(1);
				}
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return result;
	}

	public boolean updateParking(ParkingSpot parkingSpot) {
		// Mettre à jour la disponibilité de cette place de parking
		try (Connection con = dataBaseConfig.getConnection();
				final PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)) {
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			final int updateRowCount = ps.executeUpdate();
			return updateRowCount == 1;
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);

		}
		return false;
	}

	public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

}