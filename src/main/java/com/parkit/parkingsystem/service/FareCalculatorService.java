package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

//getTime() is in milliseconds and type of getTime() is long
  private long outHour;
  private long inHour;
  private double duration;
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
    discount = checkDiscount(ticket);

    if (duration > 30 * 60 * 1000) {
      switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {

        ticket.setPrice(
            (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR * discount);

        break;
      }
      case BIKE: {
        ticket.setPrice(
            (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR * discount);
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
    // TODO si discount (ticketDAO.isRecurring(ticket.getVehicleRegNumber()) = vrai alors return
    // 0.95
    // TODO sinon return 1
    if (checkDiscount) {
      ticketDAO.isRecurring(ticket.getVehicleRegNumber());
      return 0.95;

    } else {

      return 1;

    }

  }
}
