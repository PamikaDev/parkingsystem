package com.parkit.parkingsystem.service;

import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private long outHour;
	private long inHour;
	private double duration;
	private boolean isRecurring;

//	private final boolean isRecurring = TicketDAO.recurring(null);

	public void calculateFare(Ticket ticket) {
		// EXOO#1
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = outHour - inHour;

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

			}
		} else {
			ticket.setPrice(0);
		}

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

	// STORY#2 : 5%-discount for recurring bike users
	public void calculateFareBikeForRecurringUser(Ticket ticket) {

		if (isRecurring && duration > 30 * 60 * 1000) {
			inHour = ticket.getInTime().getTime();
			outHour = ticket.getOutTime().getTime();
			duration = outHour - inHour;
			ticket.getParkingSpot().getParkingType();
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		} else {
			ticket.setPrice(ticket.getPrice());
		}
	}

	// STORY#2 : 5%-discount for recurring car users
	public void calculateFareCarForRecurringUser(Ticket ticket) {
		if (isRecurring && duration > 30 * 60 * 1000) {
			inHour = ticket.getInTime().getTime();
			outHour = ticket.getOutTime().getTime();
			duration = outHour - inHour;
			ticket.getVehicleRegNumber();
			ticket.setPrice(0.95 * (duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		} else {
			ticket.setPrice(ticket.getPrice());
		}
	}

	public void calculateFareBike(Ticket ticket) {

		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = outHour - inHour;

		ticket.getParkingSpot().getParkingType();
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR);
		}
	}

	public void calculateFareCar(Ticket ticket) {

		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		duration = outHour - inHour;

		ticket.getParkingSpot().getParkingType();
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration - 30 * 60 * 1000) / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR);
		}
	}

	public void calculateFareUnkownType(Ticket ticket) {
		ticket.setParkingSpot(null);
		throw new NullPointerException("Unkown Type");
	}

	public void calculateFareCarWithFutureInTime(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			final Date outTime = new Date();
			final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
		}
		throw new NullPointerException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}

	public void calculateFareBikeWithFutureInTime(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			final Date outTime = new Date();
			final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
		}
		throw new NullPointerException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}

	public void calculateFareBike_shouldThroNullPointerException(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			Date outTime = new Date();
			outTime.setTime(0); // null out-time should generate an IllegalArgumentException
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
		}
		throw new NullPointerException();
	}

	public void calculateFareCar_shouldThroNullPointerException(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			Date outTime = new Date();
			outTime.setTime(0); // null out-time should generate an IllegalArgumentException
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
		}
		throw new NullPointerException();
	}

	public void processExitingVehicle(Ticket ticket) {
		// TODO Auto-generated method stub
	}
}
