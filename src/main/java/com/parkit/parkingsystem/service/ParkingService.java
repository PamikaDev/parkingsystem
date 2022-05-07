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

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDAO parkingSpotDAO;
  private TicketDAO ticketDAO;

  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO,
      TicketDAO ticketDAO) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDAO = parkingSpotDAO;
    this.ticketDAO = ticketDAO;
  }

  /*
   * Method for bringing a vehicle into the parking lot Process for incoming a vehicle
   */
  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehichleRegNumber();

        // check if (vehicleInside) then return;
        // boolean vehicleInside = ticketDAO.getVehicleInside(vehicleRegNumber);
        if (ticketDAO.getVehicleInside(vehicleRegNumber)) {
          System.out.println("The vehicle is already inside");
          return;
        }

        // check if incoming vehicle is for a recurring user
        // boolean isRecurring = ticketDAO.isRecurring(vehicleRegNumber);
        if (ticketDAO.isRecurring(vehicleRegNumber)) {
          System.out.println(
              "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount. ");
          logger.info("L'utilisateur est recurrent");
        }

        // assign parking space and mark false availability
        parkingSpot.setAvailable(false);
        parkingSpotDAO.updateParking(parkingSpot);

        Date inTime = new Date();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        // ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket);
        System.out.println("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out
            .println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
      }
    } catch (Exception e) {
      logger.error("Unable to process incoming vehicle", e);
    }
  }

  String getVehichleRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehichleType();
      parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      logger.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      logger.error("Error fetching next available parking slot", e);
    }

    return parkingSpot;
  }

  ParkingType getVehichleType() {
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
      System.out.println("Incorrect input provided");
      throw new IllegalArgumentException("Entered input is invalid");
    }
    }
  }

  /*
   * Method for getting a vehicle out of the parking lot Process for exiting a vehicle
   */
  public void processExitingVehicle() {
    try {
      String vehicleRegNumber = getVehichleRegNumber();

      // if (vehicleOutside) then return
      // boolean vehicleOutside = ticketDAO.getVehicleOutside(vehicleRegNumber);
      if (!ticketDAO.getVehicleOutside(vehicleRegNumber)) {
        System.out.println("The vehicle is already outside");
        return;
      }

      Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
      Date outTime = new Date();
      ticket.setOutTime(outTime);

      // fareCalculatorService.calculateFare(ticket);
      if (ticketDAO.updateTicket(ticket)) {
        // ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
        // ticket.getParkingSpot();
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot);
        // ticket.setParkingSpot(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber()
            + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      logger.error("Unable to process exiting vehicle", e);
    }
  }
}