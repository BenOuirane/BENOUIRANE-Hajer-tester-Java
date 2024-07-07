package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	 private static ParkingService parkingService;

	    @Mock
	    private static InputReaderUtil inputReaderUtil;
	    @Mock
	    private static ParkingSpotDAO parkingSpotDAO;
	    @Mock
	    private static TicketDAO ticketDAO;
	    Ticket ticket;
	    ParkingSpot parkingSpot;

	    @BeforeEach
	    private void setUpPerTest() {
	        try {
	            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
	            Ticket ticket = new Ticket();
	            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
	            ticket.setParkingSpot(parkingSpot);
	            ticket.setVehicleRegNumber("ABCDEF");
	            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw  new RuntimeException("Failed to set up test mock objects");
	        }
	    }

	    @Test
	    public void processExitingVehicleTest() throws Exception{
	        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	    //    when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
	        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
	        // Arrange
	  	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
	        Ticket ticket = new Ticket(0,parkingSpot,"ABCDEF",0, new Date(System.currentTimeMillis() - (60*60*1000)));
	        // WHEN 
			  when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
	        // Act
	        parkingService.processExitingVehicle();
	        // Assert
	 //       verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	  //      verify(parkingSpotDAO).updateParking(parkingSpot);
	 //       verify(ticketDAO).updateTicket(ticket); 
	    }
	    
	    @Test
	    public void testProcessIncomingVehicle() throws Exception {  
	      //GIVEN
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			//WHEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(false);
			//ACT
			parkingService.processIncomingVehicle();
			//THEN
			verify(ticketDAO).saveTicket(any(Ticket.class));
	    }
	    
	    @Test
	    public void processExitingVehicleTestUnableUpdate() throws Exception{
	            // Arrange
	      	  ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
	            // WHEN 
	      	  when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	          when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
	            // Utilisation de lenient() pour rendre l'interaction avec updateParking non requise
	          lenient().when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
	            // Act
	          parkingService.processExitingVehicle();
	          verify(parkingSpotDAO, never()).updateParking(parkingSpot);
	          }
	    
	    @Test
	    public void testGetNextParkingNumberIfAvailable() throws Exception {
	  	     //Arrage
			  when(inputReaderUtil.readSelection()).thenReturn(1);
		 	  when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			 // Act
			  ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();
		      int number = result.getId();
		      boolean avialable = result.isAvailable();
			 //		THEN
	          assertThat(number).isEqualTo(1);
	   	      assertThat(avialable).isEqualTo(true);
	           }
	   
	    @Test
	    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() throws Exception{
	     	// Arrange  
	          when(inputReaderUtil.readSelection()).thenReturn(1);
	          when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);
	        // Act
	          ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
	        // Assert
	          assertThat(parkingSpot).isEqualTo(null);
	   	 }
	    

	    @Test
	    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
	    	// Arrange
	    	when(inputReaderUtil.readSelection()).thenReturn(3);  	 	
	        // Act
	        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
	        // Assert
	        assertThat(parkingSpot).isEqualTo(null);
	        }

}
