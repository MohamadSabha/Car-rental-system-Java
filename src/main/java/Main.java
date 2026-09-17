import DB.DatabaseManager;
import controller.CarController;
import controller.ClientController;
import controller.ReservationController;

import service.CarService;
import service.ClientService;
import service.ReservationService;

import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

public class Main {

    public static void main(String[] args) throws Exception {

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.initializeDatabase();


        CarService carService =
                new CarService(databaseManager);
     ClientService clientService =
                new ClientService(databaseManager);
        ReservationService reservationService =
                new ReservationService(databaseManager);

        CarController carController =
                new CarController(carService);
    ClientController clientController =
                new ClientController(clientService);
        ReservationController reservationController =
                new ReservationController(reservationService);


        Javalin app = Javalin.create(config -> {

            config.jsonMapper(new JavalinGson());

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:3000");
                });
            });

            config.routes.exception(Exception.class, (exception, ctx) -> {
                exception.printStackTrace();
                ctx.status(500).result(exception.getMessage());
            });


            config.routes.get("/api/availability", ctx -> carController.getAvailableCars(ctx));

            config.routes.get(
                    "/api/clients",
                    clientController::getAllClients
            );

            config.routes.get(
                    "/api/reservations",
                    reservationController::getAllReservations
            );

            config.routes.post(
                    "/api/reservations",
                    reservationController::createReservation
            );



        });

        app.start(7070);

    }
}