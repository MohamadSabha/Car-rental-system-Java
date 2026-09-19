

package controller;

import Model.Car;
import service.CarService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javalin.http.Context;

public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    public void getAvailableCars(Context ctx) throws SQLException {


//        Format Validation
        LocalDateTime startDateTime;
        try {
            startDateTime = LocalDateTime.parse(ctx.queryParam("startDateTime"));
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid start date/time format");
        }

        int numberOfDays;
        try {
            numberOfDays = Integer.parseInt(ctx.queryParam("numberOfDays"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Number of days must be a number");
        }
        List<Car> availableCars =
                carService.getAvailableCars(
                        startDateTime,
                        numberOfDays
                );


        Map<Integer, AvailableCarTypeCount> availabilityMap =
                new HashMap<>();

        for (Car car : availableCars) {

            int carTypeId =
                    car.getCarType().getId();

            AvailableCarTypeCount existing =
                    availabilityMap.get(carTypeId);

            if (existing == null) {

                availabilityMap.put(
                        carTypeId,
                        new AvailableCarTypeCount(
                                carTypeId,
                                car.getCarType().getName(),
                                1
                        )
                );

            } else {

                existing.availableCount++;

            }
        }

        ctx.json(
                new ArrayList<>(availabilityMap.values())
        );
    }

    private static class AvailableCarTypeCount {

        // data transfer object (DTO)
        private int carTypeId;
        private String carTypeName;
        private int availableCount;

        public AvailableCarTypeCount(
                int carTypeId,
                String carTypeName,
                int availableCount) {

            this.carTypeId = carTypeId;
            this.carTypeName = carTypeName;
            this.availableCount = availableCount;
        }
    }
}