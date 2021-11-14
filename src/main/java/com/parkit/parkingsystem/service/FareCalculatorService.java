package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private long outHour;
	private long inHour;

	private final boolean isRecurring = TicketDAO.recurring(null);

	private TicketDAO ticketDAO = new TicketDAO();

	public TicketDAO getTicketDAO() {
		return ticketDAO;
	}

	public void setTicketDAO(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public FareCalculatorService(TicketDAO ticketDAO) {
		super();
		this.ticketDAO = ticketDAO;
	}

	public void calculateFare(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		// EXOO#1
		inHour = ticket.getInTime().getTime();
		outHour = ticket.getOutTime().getTime();
		final double duration = outHour - inHour;

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
				throw new IllegalArgumentException("Unkown Parking free Type");
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
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {

			ticket.setPrice(ticket.getPrice());

		}

		// STORY#2 : 5%-discount for recurring users
		if (isRecurring && duration > 30 * 60 * 1000) {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice((duration / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR));
				break;
			}
			case BIKE: {
				ticket.setPrice((duration / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR));
				break;
			}
			default:
				ticket.setPrice(ticket.getPrice());
				throw new IllegalArgumentException("Unkown Parking Recurring Type");
			}
		}
	}

	public void calculateFareBike(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		final long inHour = ticket.getInTime().getTime();
		final long outHour = ticket.getOutTime().getTime();
		final double duration = (double) outHour - inHour;

		ticket.getParkingSpot().getParkingType();
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration / (60 * 60 * 1000) * Fare.BIKE_RATE_PER_HOUR));
		} else {
			ticket.setPrice(ticket.getPrice());
		}

	}

	public void calculateFareCar(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		final long inHour = ticket.getInTime().getTime();
		final long outHour = ticket.getOutTime().getTime();
		final double duration = (double) outHour - inHour;

		ticket.getParkingSpot().getParkingType();
		if (duration > 30 * 60 * 1000) {
			ticket.setPrice((duration / (60 * 60 * 1000) * Fare.CAR_RATE_PER_HOUR));
		} else {
			ticket.setPrice(ticket.getPrice());
		}

	}

}
