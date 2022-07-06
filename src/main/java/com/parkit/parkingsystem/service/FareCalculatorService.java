package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    TicketDAO ticketDAO = new TicketDAO();

    public void calculateFare(Ticket ticket) {
        if (ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //Calculate duration to know price
        long duration1 = TimeUnit.MILLISECONDS.toMinutes(outHour - inHour);
        float duration = ((float) duration1 / 60);

        String numberPlate = ticket.getVehicleRegNumber();
        int getNumberOfTicket = ticketDAO.getNumberOfTicket(numberPlate);
        float pricePourcent = 1;
        //implement discount

        if (getNumberOfTicket >= 1){
            pricePourcent = 0.95f;
        }
        //implement 30 min free

        if (duration <= 0.50) {
            System.out.println("Thank you for visiting ! See you again soon !");
            ticket.setPrice(0);
        } else {

            switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * pricePourcent);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * pricePourcent);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}