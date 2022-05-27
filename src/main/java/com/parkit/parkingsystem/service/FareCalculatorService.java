package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import static com.parkit.parkingsystem.constants.ParkingType.CAR;

public class FareCalculatorService {

	TicketDAO ticketDAO = new TicketDAO();


	public void calculateFare(@NotNull Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long outHour = ticket.getOutTime().getTime();
		long inHour = ticket.getInTime().getTime();

		// Calculate Duration to know price
		long duration1 = TimeUnit.MILLISECONDS.toMinutes(outHour - inHour);
		float duration = ((float) duration1 / 60);

		// First, saw if they came already
		// Second, à partir de la seconde fois, 5%
		//Sinon tarif habituel

		//TODO OK: Some tests are failing here. Need to check if this logic is correct

		// En gros -> on récupère le ticket dans la bdd avec la plaque d'immatriculation,
		//Si on a déja eu un ticket avec le numéro de plaque
		//Alors on fait les 5%
		//sinon on ne fait rien
		String numberPlate = ticket.getVehicleRegNumber();
		int getNumberOfTicket = ticketDAO.getNumberOfTicket(numberPlate);

		if (duration <= 0.50) {

		ticket.setPrice(0);

		} else {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR:
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				break;

			case BIKE:
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				break;

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}

			if (getNumberOfTicket > 1) {

				double priceWithDiscount = ticket.getPrice() * 0.95;
				ticket.setPrice(priceWithDiscount);
				System.out.println("Your loyalty is rewarded with -5% on price ");
				}

		}
	}
}