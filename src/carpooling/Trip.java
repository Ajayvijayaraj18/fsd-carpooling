package carpooling;

import java.util.*;
class Trip {
    private String destination;
    private Car car;
    private List<User> passengers;

    public Trip(String destination, Car car) {
        this.destination = destination;
        this.car = car;
        this.passengers = new ArrayList<>();
    }

    public String getDestination() {
        return destination;
    }

    public Car getCar() {
        return car;
    }

    public void addPassenger(User user) {
        passengers.add(user);
    }
}