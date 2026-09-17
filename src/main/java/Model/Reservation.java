package Model;

import java.time.LocalDateTime;

public class Reservation {

    private int id;
    private Car car;
    private Client client;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Reservation(
            int id,
            Car car,
            Client client,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {

        this.id = id;
        this.car = car;
        this.client = client;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}