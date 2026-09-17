package service;

import DB.DatabaseManager;
import Model.Car;
import Model.CarType;
import Model.Client;
import Model.Reservation;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private final DatabaseManager databaseManager;

    public ReservationService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return databaseManager.getAllReservations();
    }


    public Reservation createReservation(int clientId, int carTypeId, LocalDateTime startDateTime, int numberOfDays) throws SQLException {

        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("Number of days must be greater than 0");
        }

        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date/time is required");
        }

        Client client = databaseManager.getClientById(clientId);

        if (client == null) {
            throw new IllegalArgumentException("Client not found");
        }

        CarType carType = databaseManager.getCarTypeById(carTypeId);

        if (carType == null) {
            throw new IllegalArgumentException("Car type not found");
        }

        LocalDateTime endDateTime = startDateTime.plusDays(numberOfDays);

//        replaced with this alternatives
        List<Car> availableCars =
                databaseManager.getAvailableCarsByCarTypeId(
                        carTypeId,
                        startDateTime,
                        endDateTime
                );

        if (availableCars.isEmpty()) {
            throw new IllegalArgumentException(
                    "No car available for the requested period"
            );
        }

        Car car = availableCars.get(0);

        Reservation reservation = new Reservation(
                0,
                car,
                client,
                startDateTime,
                endDateTime
        );

        return databaseManager.insertReservation(reservation);
    }
}