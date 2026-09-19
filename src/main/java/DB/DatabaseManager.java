package DB;

import Model.Car;
import Model.CarType;
import Model.Client;
import Model.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DATABASE_URL =
            System.getenv("DATABASE_URL");

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public void initializeDatabase() throws SQLException {
        String carTypesTable = """
                CREATE TABLE IF NOT EXISTS car_types (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """;

        String carsTable = """
                CREATE TABLE IF NOT EXISTS cars (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_type_id INTEGER NOT NULL,
                    FOREIGN KEY (car_type_id) REFERENCES car_types(id)
                )
                """;

        String clientsTable = """
                CREATE TABLE IF NOT EXISTS clients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL
                )
                """;

        String reservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_id INTEGER NOT NULL,
                    client_id INTEGER NOT NULL,
                    start_date_time TEXT NOT NULL,
                    end_date_time TEXT NOT NULL,
                    FOREIGN KEY (car_id) REFERENCES cars(id),
                    FOREIGN KEY (client_id) REFERENCES clients(id)
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(carTypesTable);
            statement.execute(carsTable);
            statement.execute(clientsTable);
            statement.execute(reservationsTable);
        }
        seedCarTypes();
//        seedCars();
//        seedClients();
    }

    public void seedCarTypes() throws SQLException {
        String sql = "INSERT OR IGNORE INTO car_types (name) VALUES (?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String[] carTypes = {"Sedan", "SUV", "Van"};

            for (String carType : carTypes) {
                statement.setString(1, carType);
                statement.executeUpdate();
            }
        }
    }
    public void seedCars() throws SQLException {
        String sql = """
            INSERT INTO cars (car_type_id)
            SELECT id FROM car_types WHERE name = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String[] carTypes = {"Sedan", "Sedan", "SUV", "Van"};

            for (String carType : carTypes) {
                statement.setString(1, carType);
                statement.executeUpdate();
            }
        }
    }
    public void seedClients() throws SQLException {
        String sql = """
            INSERT INTO clients (first_name, last_name)
            VALUES (?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String[][] clients = {
                    {"John", "Smith"},
                    {"Alice", "Johnson"},
                    {"Michael", "Brown"}
            };

            for (String[] client : clients) {
                statement.setString(1, client[0]);
                statement.setString(2, client[1]);
                statement.executeUpdate();
            }
        }
    }


    ////////
    public List<Client> getAllClients() throws SQLException {
        String sql = """
                SELECT id, first_name, last_name
                FROM clients
                ORDER BY id
                """;

        List<Client> clients = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clients.add(new Client(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ));
            }
        }

        return clients;
    }
    public Client getClientById(int id) throws SQLException {
        String sql = """
                SELECT id, first_name, last_name
                FROM clients
                WHERE id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Client(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    );
                }
            }
        }

        return null;
    }

    ////////
    public CarType getCarTypeById(int id) throws SQLException {
        String sql = """
                SELECT id, name
                FROM car_types
                WHERE id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CarType(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            }
        }

        return null;
    }

    ////////
    public List<Car> getAvailableCars(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws SQLException {

        String sql = """
            SELECT c.id,
                   ct.id AS car_type_id,
                   ct.name AS car_type_name
            FROM cars c
            JOIN car_types ct ON c.car_type_id = ct.id
            WHERE NOT EXISTS (
                SELECT 1
                FROM reservations r
                WHERE r.car_id = c.id
                  AND r.start_date_time < ?
                  AND r.end_date_time > ?
            )
            ORDER BY c.id
            """;

        List<Car> cars = new ArrayList<>();

//        check if the connection is only one or connectioning everytime
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, endDateTime.toString());
            statement.setString(2, startDateTime.toString());

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    CarType carType = new CarType(
                            resultSet.getInt("car_type_id"),
                            resultSet.getString("car_type_name")
                    );

                    cars.add(new Car(
                            resultSet.getInt("id"),
                            carType
                    ));
                }
            }
        }

        return cars;
    }
    public List<Car> getAvailableCarsByCarTypeId(
            int carTypeId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws SQLException {

//        String sql = """
//            SELECT c.id,
//                   ct.id AS car_type_id,
//                   ct.name AS car_type_name
//            FROM cars c
//            JOIN car_types ct ON c.car_type_id = ct.id
//            WHERE c.car_type_id = ?
//              AND NOT EXISTS (
//                  SELECT 1
//                  FROM reservations r
//                  WHERE r.car_id = c.id
//                    AND r.start_date_time < ?
//                    AND r.end_date_time > ?
//              )
//            ORDER BY c.id
//            """;
        String sql = """
            SELECT c.id,
                   ct.id AS car_type_id,
                   ct.name AS car_type_name
            FROM cars c
            JOIN car_types ct
                ON c.car_type_id = ct.id
            LEFT JOIN reservations r
                ON r.car_id = c.id
                AND r.start_date_time < ?
                AND r.end_date_time > ?
            WHERE c.car_type_id = ?
              AND r.id IS NULL
            ORDER BY c.id
            """;
        List<Car> cars = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, endDateTime.toString());
            statement.setString(2, startDateTime.toString());
            statement.setInt(3, carTypeId);


            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    CarType carType = new CarType(
                            resultSet.getInt("car_type_id"),
                            resultSet.getString("car_type_name")
                    );

                    cars.add(new Car(
                            resultSet.getInt("id"),
                            carType
                    ));
                }
            }
        }

        return cars;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        String sql = """
                SELECT r.id,
                       r.start_date_time,
                       r.end_date_time,
                       c.id AS client_id,
                       c.first_name,
                       c.last_name,
                       car.id AS car_id,
                       ct.id AS car_type_id,
                       ct.name AS car_type_name
                FROM reservations r
                JOIN clients c ON r.client_id = c.id
                JOIN cars car ON r.car_id = car.id
                JOIN car_types ct ON car.car_type_id = ct.id
                ORDER BY r.id
                """;

        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reservations.add(mapReservation(resultSet));
            }
        }

        return reservations;
    }
    public Reservation insertReservation(Reservation reservation) throws SQLException {
        String sql = """
                INSERT INTO reservations (
                    car_id,
                    client_id,
                    start_date_time,
                    end_date_time
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, reservation.getCar().getId());
            statement.setInt(2, reservation.getClient().getId());
            statement.setString(3, reservation.getStartDateTime().toString());
            statement.setString(4, reservation.getEndDateTime().toString());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    reservation.setId(keys.getInt(1));
                }
            }
        }

        return reservation;
    }
    private Reservation mapReservation(ResultSet resultSet) throws SQLException {
        CarType carType = new CarType(
                resultSet.getInt("car_type_id"),
                resultSet.getString("car_type_name")
        );

        Car car = new Car(
                resultSet.getInt("car_id"),
                carType
        );

        Client client = new Client(
                resultSet.getInt("client_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );

        return new Reservation(
                resultSet.getInt("id"),
                car,
                client,
                java.time.LocalDateTime.parse(
                        resultSet.getString("start_date_time")
                ),
                java.time.LocalDateTime.parse(
                        resultSet.getString("end_date_time")
                )
        );
    }
}