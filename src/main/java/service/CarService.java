package service;

import DB.DatabaseManager;
import Model.Car;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CarService {

    private final DatabaseManager databaseManager;

    public CarService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Car> getAvailableCars(
            LocalDateTime startDateTime,
            int numberOfDays) throws SQLException {

//        Business Validation
        if (startDateTime == null) {
            throw new IllegalArgumentException(
                    "Start date/time is required"
            );
        }

        if (numberOfDays <= 0) {
            throw new IllegalArgumentException(
                    "Number of days must be greater than 0"
            );
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Reservation date/time cannot be in the past");
        }

        LocalDateTime endDateTime =
                startDateTime.plusDays(numberOfDays);

        return databaseManager.getAvailableCars(
                startDateTime,
                endDateTime
        );
    }
}