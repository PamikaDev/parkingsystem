package com.parkit.parkingsystem.service;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private final InputReaderUtil inputReaderUtil;
	private final ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO = new TicketDAO();

	private boolean isRecurring = false;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		ParkingService.ticketDAO = ticketDAO;
	}

	public void processIncomingVehicle() {
		try {
			final ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				final String vehicleRegNumber = getVehichleRegNumber();

				// check if incoming véhicle is for a recurring user
				isRecurring = TicketDAO.recurring(vehicleRegNumber);
				if (isRecurring) {
					System.out.println(
							" Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
				} else {
					System.out.println(" Welcome");
				}
			}
		} catch (final Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}

		try {
			final ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			parkingSpot.setAvailable(false);
			// Allot this parking space and mark it's availability as false
			parkingSpotDAO.updateParking(parkingSpot);

			final Date inTime = new Date();
			final Ticket ticket = new Ticket();
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber(getVehichleRegNumber());
			ticket.setPrice(0);
			ticket.setInTime(inTime);
			ticket.setOutTime(null);
			ticketDAO.saveTicket(ticket);
			logger.info("Generated Ticket and saved in DB");
			System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
			System.out.println("Recorded in-time for vehicle number:" + getVehichleRegNumber() + " is:" + inTime);

		} catch (final Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	public String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			final ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new SQLException("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (final IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (final SQLException e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	public ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		final int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			logger.info("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	public void getVehichleTypeTest_shouldThrowIllegalArgumentException_forUnknowParkingType() {
		int input = inputReaderUtil.readSelection();
		if (input != 1 && input != 2) {
			throw new IllegalArgumentException("Entered input is invalid");
		}

	}

	public void processExitingVehicle() {
		try {
			final Ticket ticket = new Ticket();
			final Date outTime = new Date();

			ticket.setOutTime(outTime);
			fareCalculatorService.processExitingVehicle(ticket);

			// for recurring user
			if (isRecurring) {
				ticket.setPrice(ticket.getPrice() * 0.95);
			}

			if (ticketDAO.updateTicket(ticket)) {
				final ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				System.out.println("Please pay the parking fare:" + ticket.getPrice());
				logger.info(
						"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
			} else {
				logger.info("Unable to update ticket information. Error occurred");
			}
		} catch (final Exception e) {
			logger.error("Unable to process exiting vehicle", e);

		}
	}
}
