package carpooling;
class Car {
    private String model;
    private int capacity;
    private int availableSeats;

    public Car(String model, int capacity) {
        this.model = model;
        this.capacity = capacity;
        this.availableSeats = capacity;
    }

    public String getModel() {
        return model;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean bookSeats(int numSeats) {
        if (numSeats <= availableSeats) {
            availableSeats -= numSeats;
            return true;
        }
        return false;
    }
}

