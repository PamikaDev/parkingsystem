package com.parkit.parkingsystem.model;

import java.util.Date;

import com.parkit.parkingsystem.dao.TicketDAO;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Date inTime;
	private Date outTime;
	private boolean isRecurring;
	private TicketDAO ticketDAO;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}

	public boolean isRecurring() {
		return isRecurring;
	}

	public void setRecurring(boolean isRecurring) {
//		this.isRecurring = isRecurring;
//		this.isRecurring = TicketDAO.isRecurring(vehicleRegNumber);
		this.isRecurring = TicketDAO.isRecurring(vehicleRegNumber);

	}
}
