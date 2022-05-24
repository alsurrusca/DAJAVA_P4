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
	Ticket ticket = ticketDAO.getTicket("ABCDEF");

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

		// Initialiser l'heure de sortie et le mettre en paramètre pour la sortie du véhicule
		//WHEN
		long outTime = ticket.getInTime().getTime() + 60 * 60 * 1000;

		//THEN
		parkingService.processExitingVehicle(new Date(outTime));

		assertNotNull(ticket);
		//assertEquals(outTime, ticket.getOutTime().getTime());
		//On initialise pour le prix
		
	}

	@Test
	public void testRecurringDiscount(){

		testParkingLotExit();
		testParkingLotExit();
		testParkingLotExit();

		System.out.println(1.48 * 0.95);
	}

}

