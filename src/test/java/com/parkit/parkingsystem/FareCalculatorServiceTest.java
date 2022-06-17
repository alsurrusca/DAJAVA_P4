package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Date;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingSpotDAO parkingSpotDAO;

	@ExtendWith(MockitoExtension.class)

	@BeforeAll
	private static void setUp() throws Exception {
		fareCalculatorService = new FareCalculatorService();
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();

	}

	@BeforeEach
	private void setUpPerTest() throws Exception {

		ticket = new Ticket();
		dataBasePrepareService.clearDataBaseEntries();
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
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
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
		inTime.setTime(System.currentTimeMillis() - 30 * 60 * 1000); //30 minutes parking time should give 1/2th parking fare
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice());

	}

	@Test
	public void calculateTicketSetPriceTest() {

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 30 * 60 * 1000);
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.getPrice();
		fareCalculatorService.calculateFare(ticket);

		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithFivePourcentLess() {

/*
		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		ticket.getPrice();
		System.out.println(ticket.getPrice());
		ticketDAO.getTicket("ABCDEF");

		ticketDAO.saveTicket(ticket);

		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		ticketDAO.getTicket("ABCDEF");
		ticketDAO.saveTicket(ticket);
		System.out.println(ticket.getPrice());





		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticketDAO.saveTicket(ticket);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		ticketDAO.getTicket("ABCDEF");
		ticketDAO.updateTicket(ticket);
		ticketDAO.saveTicket(ticket);

*/
		when(ticketDAO.getNumberOfTicket(anyString())).thenReturn(3);

		DecimalFormat df = new DecimalFormat("0.00");
		assertEquals(df.format(Fare.CAR_RATE_PER_HOUR * 0.95),df.format(ticket.getPrice()));




	}


	@Test
	public void calculateFareBikeWithFivePourcentLess() {


		Date inTime = new Date();
		inTime.setTime(
				System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticketDAO.saveTicket(ticket);


		ticket.setInTime(inTime);
		ticketDAO.saveTicket(ticket);
		fareCalculatorService.calculateFare(ticket);
		ticketDAO.getTicket("ABCDEF");
		DecimalFormat df = new DecimalFormat("0.00");

		assertEquals(df.format(Fare.BIKE_RATE_PER_HOUR * 0.95),df.format(ticket.getPrice()));



	}


	@Test
	public void calculateFareCarWithoutFivePourcentLessForCar() {

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		DecimalFormat df = new DecimalFormat("0.00");

		assertEquals(df.format(Fare.CAR_RATE_PER_HOUR),df.format(ticket.getPrice()));
		System.out.println(df.format(ticket.getPrice()));
		System.out.println(df.format(Fare.CAR_RATE_PER_HOUR));
	}

	@Test
	public void calculateFareWithoutFivePourcentLessForBike() {

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		DecimalFormat df = new DecimalFormat("0.00");

		assertEquals(df.format(Fare.BIKE_RATE_PER_HOUR),df.format(ticket.getPrice()));

	}

	/*@Test
	public void timeProvidedTest(){

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 30 * 60 * 1000);
		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		//J'appelle la méthode fareCalculatorService dans assertThrows()

		assertThrows();
	}
*/
}
