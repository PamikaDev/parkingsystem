package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  private long outHour;
  private long inHour;
  private double duration;

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    // STORY#1 : Free 30-min parking
    // getTime() is in milliseconds type of getTime() is long
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    // get duration is in ms type of duration must be double
    duration = (double) outHour - inHour;
    double discount = checkDicount(ticket);

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

  public double checkDicount(Ticket ticket) {
    // TODO si discount (ticketDAO.isReccuring(ticket.getVehicleRegNumber()) = vrai alors return
    // 0.95
    // TODO sinon return 1

    return 1;
  }
}
