package service;

import DB.DatabaseManager;
import Model.Car;
import Model.CarType;
import Model.Client;
import Model.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Test
    void shouldCreateReservationWhenCarIsAvailable()
            throws Exception {

        // Arrange
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        ReservationService reservationService =
                new ReservationService(databaseManager);

        Client client =
                new Client(1, "John", "Smith");

        CarType carType =
                new CarType(1, "Sedan");

        Car car =
                new Car(1, carType);

        when(databaseManager.getClientById(1))
                .thenReturn(client);

        when(databaseManager.getCarTypeById(1))
                .thenReturn(carType);

        when(databaseManager.getAvailableCarsByCarTypeId(
                eq(1),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(car));

        Reservation savedReservation =
                new Reservation(
                        1,
                        car,
                        client,
                        LocalDateTime.of(2026, 10, 15, 10, 0),
                        LocalDateTime.of(2026, 10, 17, 10, 0)
                );

        when(databaseManager.insertReservation(any(Reservation.class)))
                .thenReturn(savedReservation);

        // Act
        Reservation result =
                reservationService.createReservation(
                        1,
                        1,
                        LocalDateTime.of(2026, 10, 15, 10, 0),
                        2
                );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getCar().getId());
        assertEquals(1, result.getClient().getId());
        assertEquals(
                LocalDateTime.of(2026, 10, 17, 10, 0),
                result.getEndDateTime()
        );
    }
    @Test
    void shouldRejectReservationWhenAllCarsAreOccupied()
            throws Exception {

        // Arrange
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        ReservationService reservationService =
                new ReservationService(databaseManager);

        Client client =
                new Client(1, "John", "Smith");

        CarType carType =
                new CarType(1, "Sedan");

        when(databaseManager.getClientById(1))
                .thenReturn(client);

        when(databaseManager.getCarTypeById(1))
                .thenReturn(carType);

        when(databaseManager.getAvailableCarsByCarTypeId(
                eq(1),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        1,
                        1,
                        LocalDateTime.of(2026, 10, 16, 10, 0),
                        1
                )
        );

        verify(databaseManager, never())
                .insertReservation(any(Reservation.class));
    }
    @Test
    void shouldRejectReservationWhenNumberOfDaysIsInvalid()
            throws Exception {

        // Arrange
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        ReservationService reservationService =
                new ReservationService(databaseManager);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        1,
                        1,
                        LocalDateTime.of(2026, 10, 15, 10, 0),
                        0
                )
        );

        verify(databaseManager, never())
                .insertReservation(any(Reservation.class));
    }


    @Test
    void shouldRejectReservationWhenStartDateIsInThePast()
            throws Exception {

        // Arrange
        DatabaseManager databaseManager = mock(DatabaseManager.class);

        ReservationService reservationService =
                new ReservationService(databaseManager);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        1,
                        1,
                        LocalDateTime.now().minusDays(1),
                        2
                )
        );

        verify(databaseManager, never())
                .insertReservation(any(Reservation.class));
    }
}