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
    long inHour = ticket.getInTime().getTime();
    long outHour = ticket.getOutTime().getTime();
    // get duration is in ms type of duration must be double
    double duration = outHour - inHour;

    if (duration > 30 * 60 * 1000) {
      switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {

        ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);

        break;
      }
      case BIKE: {
        ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
      }
    } else {
      ticket.setPrice(0);
    }

  }

  public void calculateFareForRecurringUser(Ticket ticket) {
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    duration = (double) outHour - inHour;
    if (duration > 30 * 60 * 1000) {
      switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        ticket.setPrice(
            0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
        break;
      }
      case BIKE: {
        ticket.setPrice(
            0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
      }
    } else {
      ticket.setPrice(0);
    }

  }

  public Object calculateFareUnkownType(Ticket ticket) {
    ticket.setParkingSpot(null);
    throw new NullPointerException("Unkown Type");
  }

  public void calculateFareCarWithLessThanOneHourParkingTime(Ticket ticket) {
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    duration = (double) outHour - inHour;

    // 45 minutes parking time should give 3/4th parking fare
    // But Free 30-min parking
    if (duration == 45 * 60 * 1000) {
      if (ticket.getParkingSpot().getParkingType() != null) {
        ticket.setPrice(0.25 * Fare.CAR_RATE_PER_HOUR);
      }

    }

  }

  public void calculateFareBikeWithLessThanOneHourParkingTime(Ticket ticket) {
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    duration = (double) outHour - inHour;

    // 45 minutes parking time should give 3/4th parking fare
    // But Free 30-min parking
    if (duration == 45 * 60 * 1000) {
      if (ticket.getParkingSpot().getParkingType() != null) {
        ticket.setPrice(0.25 * Fare.BIKE_RATE_PER_HOUR);
      }

    }

  }

  public void calculateFareCarWithMoreThanADayParkingTime(Ticket ticket) {
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    duration = (double) outHour - inHour;
    // 24 hours parking time should give 24 * parking fare per hour
    if (duration == 24 * 60 * 60 * 1000) {
      if (ticket.getParkingSpot().getParkingType() != null) {
        ticket.setPrice(23.5 * Fare.CAR_RATE_PER_HOUR);
      }
    }

  }

  public void calculateFareBikeWithMoreThanADayParkingTime(Ticket ticket) {
    inHour = ticket.getInTime().getTime();
    outHour = ticket.getOutTime().getTime();
    duration = (double) outHour - inHour;
    // 24 hours parking time should give 24 * parking fare per hour
    if (duration == 24 * 60 * 60 * 1000) {
      if (ticket.getParkingSpot().getParkingType() != null) {
        ticket.setPrice(23.5 * Fare.BIKE_RATE_PER_HOUR);
      }
    }

  }

}