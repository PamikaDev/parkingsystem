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
	}

	// STORY#1 : Free 30-min parking for Bike
	public void calculateFareBike(Ticket ticket) {

		// getTime() is in milliseconds, type of getTime() is long
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();

		// get duration is in milliseconds, type of duration must be double
		duration = (double) outHour - inHour;

		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		} else {
			ticket.setPrice(ticket.getPrice());
		}
	}

	// STORY#1 : Free 30-min parking for Car
	public void calculateFareCar(Ticket ticket) {

		// getTime() is in milliseconds, type of getTime() is long
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();

		// get duration is in milliseconds, type of duration must be double
		duration = (double) outHour - inHour;

		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		} else {
			ticket.setPrice(ticket.getPrice());
		}
	}

	// STORY#2 : 5%-discount for recurring bike users
	public void calculateFareBike_forRecurringUsers_shouldGetA5PerCentDisount(Ticket ticket) {
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		}
	}

	// STORY#2 : 5%-discount for recurring car users
	public void calculateFareCar_forRecurringUsers_shouldGetA5PerCentDisount(Ticket ticket) {
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = (double) outHour - inHour;
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		}
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
			} else {
				ticket.setPrice(ticket.getPrice());
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
			} else {
				ticket.setPrice(ticket.getPrice());
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
			} else {
				ticket.setPrice(ticket.getPrice());
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
			} else {
				ticket.setPrice(ticket.getPrice());
			}
		}
	}

	public void calculateFareUnkownType(Ticket ticket) {
		ticket.setParkingSpot(null);
		throw new NullPointerException("Unkown Type");
	}
}