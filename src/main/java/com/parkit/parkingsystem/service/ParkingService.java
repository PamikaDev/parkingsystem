package com.parkit.parkingsystem.service;

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

  private final InputReaderUtil inputReaderUtil;
  private final ParkingSpotDAO parkingSpotDAO;
  private final TicketDAO ticketDAO;
//  private boolean isRecurring;

  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO,
      TicketDAO ticketDAO) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDAO = parkingSpotDAO;
    this.ticketDAO = ticketDAO;
  }

  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehichleRegNumber();

        // Check if incoming vehicle is for a recurring user
        boolean isRecurring = ticketDAO.isRecurring(vehicleRegNumber);
        if (isRecurring) {
          System.out.println("Welcome back you'll benefit from a 5% discount");
          // logger.info("Welcome back you'll benefit from a 5% discount");
        } else {
          System.out.println("Welcome our parking system");
//          logger.info("Welcome our parking system");
        }

        parkingSpot.setAvailable(false);
        // allot this parking space and mark it's availability as false
        parkingSpotDAO.updateParking(parkingSpot);

        final Date inTime = new Date();
        final Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket);
        logger.info("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
//        logger.info("Please park your vehicle in spot number:" + parkingSpot.getId());
        logger.info("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }
  }

  public String getVehichleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  public ParkingSpot getNextParkingNumberIfAvailable() throws Exception {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    ParkingType parkingType = getVehichleType();
    parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
    if (parkingNumber > 0) {
      parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
    }

    return parkingSpot;
  }

  public ParkingType getVehichleType() throws IllegalArgumentException {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");

    int input = inputReaderUtil.readSelection();
    switch (input) {
    case 1: {
      return ParkingType.CAR;
    }
    case 2: {
      return ParkingType.BIKE;
    }
    default: {
      logger.info("Incorrect input provided");
    }

    }
//    throw new IllegalArgumentException("Entered input is invalid");
    return null;
  }

  public void processExitingVehicle() {
    try {
      final String vehicleRegNumber = getVehichleRegNumber();
      final Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
      final Date outTime = new Date();
      ticket.setOutTime(outTime);

      if (ticketDAO.updateTicket(ticket)) {
        final ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        // logger.info("Please pay the parking fare:" + ticket.getPrice());
        logger.info("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:"
            + outTime);
      } else {
        logger.error("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }

  }

}