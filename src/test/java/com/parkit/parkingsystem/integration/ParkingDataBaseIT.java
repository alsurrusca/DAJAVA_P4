package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import junit.framework.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	Ticket ticket;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();

	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {

		//Given
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		//Previous Slot
		int previousSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		//When
		parkingService.processIncomingVehicle();
		ticket = ticketDAO.getTicket("ABCDEF");
		//TODO OK: check that a ticket is actualy saved in DB and Parking table is updated with availability

		//THEN
		assertNotNull(ticket);

		//Check if Parking Table is updated with avaibility
		int newSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertNotSame(previousSlot, newSlot);
	}

	@Test
	public void testParkingLotExit() {
		//Given

		testParkingACar();

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		//TODO OK: check that the fare generated and out time are populated correctly in the database

		//THEN
		long outTime = ticket.getInTime().getTime() + 60 * 60 * 1000;
		parkingService.processExitingVehicle(new Date(outTime));

		//Rappel du ticket pour ne pas avoir de décalage dans les millisecondes
		ticket = ticketDAO.getTicket("ABCDEF");
		assertNotNull(ticket);
		assertNotNull(ticket.getOutTime());
		//On initialise pour le prix

	}

	@Test
	public void testRecurringDiscount(){


		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		ticket = ticketDAO.getTicket("ABCDEF");
		long outTime = ticket.getInTime().getTime() + 60 * 60 * 1000;
		parkingService.processExitingVehicle();

		parkingService.processIncomingVehicle();
		ticket = ticketDAO.getTicket("ABCDEF");
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ticket.setInTime(inTime);
		ticketDAO.saveTicket(ticket);
		parkingService.processExitingVehicle();

		assertEquals(Fare.CAR_RATE_PER_HOUR * 0.95,ticketDAO.getTicket("ABCDEF").getPrice());



		//System.out.println(ticket.getPrice() * 0.95);
	}

}

