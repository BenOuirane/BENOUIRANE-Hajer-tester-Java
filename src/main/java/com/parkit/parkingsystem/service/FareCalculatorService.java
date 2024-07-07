package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        long durationMillis = outTimeMillis - inTimeMillis ; 
        long hours = durationMillis / (1000 * 60 * 60);
        long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((durationMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        double totalHours = hours + (minutes / 60.0) + (seconds / 3600.0);
        switch (ticket.getParkingSpot().getParkingType()){
        	 case CAR: {
             	if (totalHours < 0.50) {
               		 ticket.setPrice(0);
                    }
               	 else {
                   ticket.setPrice(totalHours * Fare.CAR_RATE_PER_HOUR); }
                   break;
             }
             case BIKE: {
             	if (totalHours < 0.50) {
              		 ticket.setPrice(0);
                   }
             else {
               ticket.setPrice(totalHours * Fare.BIKE_RATE_PER_HOUR); }
               break;
             }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        if (discount) {
            ticket.setPrice(ticket.getPrice() * 0.95);
        }
    }
    
    public void calculateFare(Ticket ticket) {
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
    	fareCalculatorService.calculateFare(ticket, false);
    }
}