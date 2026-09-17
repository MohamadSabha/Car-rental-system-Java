package Model;

public class Car {

    private int id;
    private CarType carType;

    public Car(int id, CarType carType) {
        this.id = id;
        this.carType = carType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }
}