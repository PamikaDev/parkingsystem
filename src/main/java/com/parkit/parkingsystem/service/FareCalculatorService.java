package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private final boolean isRecurring = TicketDAO.isRecurring(null);

	public void calculateFare(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		// TODO#1
		// getHours is deprecated.instead use the constructor Time stamp(long millis)
		final long inHour = ticket.getInTime().getTime();
		final long outHour = ticket.getOutTime().getTime();

		// For the duration I use the double type to have a real number.
		final double duration = (double) outHour - inHour;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

		// STORY#1 : Free 30-min parking
		if (duration > 30 * 60 * 1000) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(duration / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}

		// STORY#2 : 5%-discount for recurring users
		if (isRecurring) {
			ticket.setPrice(ticket.getPrice() * 0.95);
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR * 0.95);
				break;
			}
			case BIKE: {
				ticket.setPrice(duration / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR * 0.95);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(ticket.getPrice());
		}

	}
}
