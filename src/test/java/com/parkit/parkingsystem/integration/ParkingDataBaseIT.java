package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.when;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
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
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws Exception{
    	 ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
         parkingService.processIncomingVehicle();
         Ticket savedTicket = ticketDAO.getTicket("ABCDEF");
		 assertNotNull(savedTicket);
		 assertFalse(savedTicket.getParkingSpot().isAvailable());
      }
    
    @Test
    public void testParkingLotExit() throws Exception{
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Date inTime = new Date();
   		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
   		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
   		Ticket ticket = new Ticket();
   		ticket.setInTime(inTime);
   		ticket.setParkingSpot(parkingSpot);
   		ticket.setVehicleRegNumber("ABCDEF");
   		ticket.setPrice(0);
   		ticketDAO.saveTicket(ticket);
   		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        Ticket savedTicket = ticketDAO.getTicket("ABCDEF");
        Date outTime = savedTicket.getOutTime();
  	    double  price = savedTicket.getPrice();
        assertNotNull(outTime);
        assertNotNull(price);
      }
    
    @Test
    public void testParkingLotExitRecurringUser() {
   	Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (22 * 60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis() - (23 * 60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setOutTime(outTime);
       ticket.setPrice(0);
		ticketDAO.saveTicket(ticket);
       ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		Ticket savedTicket = ticketDAO.getTicket("ABCDEF");
		assertNotNull(savedTicket);
		assertEquals(2, ticketDAO.getNbTicket("ABCDEF"));
		assertNotSame(0, savedTicket.getPrice());
       }
    
}
