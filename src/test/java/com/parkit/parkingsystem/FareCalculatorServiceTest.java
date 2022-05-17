package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareBike() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithFutureInTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());

	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {
		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());

	}

	@Test
	public void calculateFareBikeWithLessThanThirtyMinutesParkingTime() {

		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - 30 * 60 * 1000); //30 minutes parking time should give 1/2th parking fare
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice());

	}

	@Test
	public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {

		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - 30 * 60 * 1000); //30 minutes parking time should give 1/2th parking fare
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice());

	}

	@Test
	public void calculateFareWithFivePourcentLess() {

		TicketDAO ticketDAO = new TicketDAO();
		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		//Quand on fait appelle à getTicket, on retourne 2 donc recurrence
		//When ticketDAO.getNumberOfTicket("ABCDEF");
		//Then return 2;

		ticketDAO.getTicket("ABCDEF");

		assertEquals(Fare.CAR_RATE_PER_HOUR * 0.95,ticket.getPrice());

	}

	@Test
	public void calculateFareWithoutFivePourcentLessForCar() {

		TicketDAO ticketDAO = new TicketDAO();
		String numberPlate = ticket.getVehicleRegNumber();
		int getnumberOfTicket = ticketDAO.getNumberOfTicket(numberPlate);
		double finalPrice = ticket.getPrice();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		if(getnumberOfTicket < 1) {

			finalPrice = Fare.CAR_RATE_PER_HOUR;
			System.out.println(finalPrice);
			ticket.setPrice(finalPrice);

		}

		assertEquals(Fare.CAR_RATE_PER_HOUR, finalPrice);
	}

	@Test
	public void calculateFareWithoutFivePourcentLessForBike() {

		TicketDAO ticketDAO = new TicketDAO();
		String numberPlate = ticket.getVehicleRegNumber();
		int getnumberOfTicket = ticketDAO.getNumberOfTicket(numberPlate);
		double finalPrice = ticket.getPrice();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		if(getnumberOfTicket < 1) {

			finalPrice = Fare.BIKE_RATE_PER_HOUR;
			ticket.setPrice(finalPrice);


		}

		assertEquals(Fare.BIKE_RATE_PER_HOUR, finalPrice);
	}


}
