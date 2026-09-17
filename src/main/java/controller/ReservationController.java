package controller;

import Model.Reservation;
import service.ReservationService;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public void getAllReservations(Context ctx) throws SQLException {
        List<Reservation> reservations =
                reservationService.getAllReservations();

        List<ReservationResponse> result = new java.util.ArrayList<>();

        for (Reservation reservation : reservations) {
            result.add(new ReservationResponse(
                    reservation.getId(),
                    reservation.getCar().getId(),
                    reservation.getClient().getId(),
                    reservation.getStartDateTime().toString(),
                    reservation.getEndDateTime().toString(),
                    reservation.getClient().getFirstName(),
                    reservation.getClient().getLastName(),
                    reservation.getCar().getCarType().getName()
            ));
        }

        ctx.json(result);
    }

    public void createReservation(Context ctx) throws SQLException {
        ReservationRequest request =
                ctx.bodyAsClass(ReservationRequest.class);

        Reservation reservation =
                reservationService.createReservation(
                        request.clientId,
                        request.carTypeId,
                        LocalDateTime.parse(request.startDateTime),
                        request.numberOfDays
                );

        ctx.status(201).json(
                new ReservationResponse(
                        reservation.getId(),
                        reservation.getCar().getId(),
                        reservation.getClient().getId(),
                        reservation.getStartDateTime().toString(),
                        reservation.getEndDateTime().toString(),
                        reservation.getClient().getFirstName(),
                        reservation.getClient().getLastName(),
                        reservation.getCar().getCarType().getName()
                )
        );
    }

    // data transfer object (DTO)
    private static class ReservationRequest {

        private int clientId;
        private int carTypeId;
        private String startDateTime;
        private int numberOfDays;
    }

    // data transfer object (DTO)
    private static class ReservationResponse {

        private int reservationId;
        private int carId;
        private int clientId;
        private String startDateTime;
        private String endDateTime;
        private String clientFirstName;
        private String clientLastName;
        private String carTypeName;

        public ReservationResponse(int reservationId, int carId, int clientId, String startDateTime, String endDateTime,
                String clientFirstName, String clientLastName, String carTypeName) {

            this.reservationId = reservationId;
            this.carId = carId;
            this.clientId = clientId;
            this.clientFirstName = clientFirstName;
            this.clientLastName = clientLastName;
            this.carTypeName = carTypeName;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }
    }
}