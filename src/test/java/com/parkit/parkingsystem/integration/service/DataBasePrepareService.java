package com.parkit.parkingsystem.integration.service;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBasePrepareService {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");
	DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public void clearDataBaseEntries() {
		Connection connection = null;
		try {
			connection = dataBaseConfig.getConnection();

			// set parking entries to available
			connection.prepareStatement("update parking set available = true").execute();

			// clear ticket entries;
			connection.prepareStatement("truncate table ticket").execute();

		} catch (final Exception e) {
			logger.error("Unable to process clearDataBaseEntries", e);

		} finally {
			dataBaseConfig.closeConnection(connection);
		}
	}
}
