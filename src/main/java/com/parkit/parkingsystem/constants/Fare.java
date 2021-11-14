package com.parkit.parkingsystem.constants;

public class Fare {
	private Fare() {
		throw new IllegalStateException("Utility class");
	}

	public static final double BIKE_RATE_PER_HOUR = 1.0;
	public static final double CAR_RATE_PER_HOUR = 1.5;
	public static final double RECURRING_BIKE_RATE_PER_HOUR = (BIKE_RATE_PER_HOUR * 0.95);
	public static final double RECURRING_CAR_RATE_PER_HOUR = (CAR_RATE_PER_HOUR * 0.95);
}
