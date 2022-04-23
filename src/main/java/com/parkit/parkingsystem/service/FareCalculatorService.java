package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

//getTime() is in milliseconds and type of getTime() is long
  private long outHour;
  private long inHour;
  private double duration;
  private double parkingTime;
  private double discount;
  private TicketDAO ticketDAO;
  private boolean checkDiscount;

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    // STORY#1 et #2 : Free 30-min parking + discount 5%

    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    // get duration is in milliseconds and type of duration must be double
    duration = (double) outHour - inHour;
    parkingTime = (duration - 30 * 60 * 1000) / (60 * 60 * 1000);
    discount = checkDiscount(ticket);

    if (duration > 30 * 60 * 1000) {
      switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {

        ticket.setPrice(parkingTime * Fare.CAR_RATE_PER_HOUR * discount);

        break;
      }
      case BIKE: {
        ticket.setPrice(parkingTime * Fare.BIKE_RATE_PER_HOUR * discount);
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
      }
    } else {
      ticket.setPrice(0);
    }

  }

  public double checkDiscount(Ticket ticket) {
    if (checkDiscount) {
      ticketDAO.isRecurring(ticket.getVehicleRegNumber());
      return 0.95;
    } else {
      return 1;
    }
  }

}