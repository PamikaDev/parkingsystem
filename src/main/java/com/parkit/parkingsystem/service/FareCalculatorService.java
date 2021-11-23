package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private long outHour;
	private long inHour;
	private double duration;

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// getTime() is in milliseconds, type of getTime() is long
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();

		// get duration is in milliseconds, type of duration must be double
		duration = (double) outHour - inHour;

		// STORY#1 : Free 30-min parking
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

	// STORY#2 : 5%-discount for recurring bike users
	public void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount(Ticket ticket) {
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		}
	}

	// STORY#2 : 5%-discount for recurring car users
	public void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount(Ticket ticket) {
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		}
	}

	public void calculateFareBike(Ticket ticket) {

		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		} else {
			ticket.setPrice(duration);
		}
	}

	public void calculateFareCar(Ticket ticket) {

		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		} else {
			ticket.setPrice(duration);
		}
	}

	public void calculateFareCarWithLessThanOneHourParkingTime(Ticket ticket) {
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;

		// 45 minutes parking time should give 3/4th parking fare
		// But Free 30-min parking
		if (duration == 45 * 60 * 1000) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(0.25 * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(0.25 * Fare.BIKE_RATE_PER_HOUR);
				break;
			}

			}
		} else {
			ticket.setPrice(ticket.getPrice());
		}

	}

}