package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  private TicketDAO ticketDAO;

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    /*
     * STORY#1 et #2 : Free 30-min parking + discount 5%
     *
     * getTime() is in milliseconds and type of getTime() is long
     */
    long inHour = ticket.getInTime().getTime();
    long outHour = ticket.getOutTime().getTime();
    // get duration is in milliseconds and type of duration must be double
    double duration = (double) outHour - inHour;
    double parkingTime = (duration - 30 * 60 * 1000) / (60 * 60 * 1000);
    double discount = checkDiscount(ticket);

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
    boolean isDiscount = false;
    double discount = 0.95;

    if (isDiscount) {
      ticketDAO.isRecurring(ticket.getVehicleRegNumber());
      ticket.setDiscount(discount);

    }
    return discount = 1;
  }
}