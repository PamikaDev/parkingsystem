package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  private TicketDAO ticketDAO = new TicketDAO();

  public void setTicketDAO(TicketDAO ticketDAO) {
    this.ticketDAO = ticketDAO;
  }

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    /**
     * Free 30-min parking + discount 5%. getTime() is in milliseconds and type of getTime() is long
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
      default: {
        throw new IllegalArgumentException("Unkown Parking Type");
      }
      }
    } else {
      // Free 30-min parking
      ticket.setPrice(0);
    }
  }

  /**
   * Check if discount
   *
   * @param ticket
   * @return
   */
  public double checkDiscount(Ticket ticket) {
    String vehicleRegNumber = ticket.getVehicleRegNumber();
    boolean isrecurring = ticketDAO.isRecurring(vehicleRegNumber);
    if (isrecurring) {
      return 0.95;
    } else {
      return 1;
    }
  }
}
