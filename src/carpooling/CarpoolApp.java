package carpooling;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class CarpoolApp {
    private static List<User> users = new ArrayList<>();
    private static List<Car> cars = new ArrayList<>();
    private static List<Trip> trips = new ArrayList<>();
    private static int userIdCounter = 1; // To assign unique user IDs

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Connected to the database!");

            while (!exit) {
                System.out.println("1. Add User");
                System.out.println("2. Add Car");
                System.out.println("3. Offer Trip");
                System.out.println("4. Book Trip");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        addUser(scanner);
                        break;
                    case 2:
                        addCar(scanner);
                        break;
                    case 3:
                        offerTrip(scanner);
                        break;
                    case 4:
                        bookTrip(scanner);
                        break;
                    case 5:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertUserIntoDatabase(String name) {
        String insertQuery = "INSERT INTO user (id, name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            int userId = userIdCounter++; // Assuming you are incrementing userIdCounter properly
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void addUser(Scanner scanner) {
        System.out.print("Enter user name: ");
        String name = scanner.next();
        users.add(new User(name));
        insertUserIntoDatabase(name); // Insert the user into the database
        System.out.println("User added successfully!");
    }


    private static void addCar(Scanner scanner) {
        System.out.print("Enter car model: ");
        String model = scanner.next();
        System.out.print("Enter car capacity: ");
        int capacity = scanner.nextInt();
        cars.add(new Car(model, capacity));
        System.out.println("Car added successfully!");
    }

    private static Car findCarByModel(String model) {
        for (Car car : cars) {
            if (car.getModel().equalsIgnoreCase(model)) {
                return car;
            }
        }
        return null;
    }

    private static void offerTrip(Scanner scanner) {
        System.out.print("Enter trip destination: ");
        String destination = scanner.next();
        System.out.print("Enter car model: ");
        String carModel = scanner.next();

        try (Connection connection = DatabaseConnection.getConnection()) {
            Car car = findCarByModel(carModel);

            if (car == null) {
                System.out.println("Car model not found. Please add the car first.");
                return;
            }

            Trip trip = new Trip(destination, car);
            trips.add(trip);

            // Insert trip details into the database
            String insertQuery = "INSERT INTO trip (destination, car_model) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, destination);
                statement.setString(2, carModel);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Trip offered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static User findUserByName(String name) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    private static Trip findTripByDestination(String destination) {
        for (Trip trip : trips) {
            if (trip.getDestination().equalsIgnoreCase(destination)) {
                return trip;
            }
        }
        return null;
    }

    private static void bookTrip(Scanner scanner) {
        System.out.print("Enter your name: ");
        String userName = scanner.next();
        User user = findUserByName(userName);

        if (user == null) {
            System.out.println("User not found. Please add the user first.");
            return;
        }

        System.out.print("Enter trip destination: ");
        String destination = scanner.next();
        Trip trip = findTripByDestination(destination);

        if (trip == null) {
            System.out.println("Trip not found. Please offer the trip first.");
            return;
        }

        System.out.print("Enter number of seats to book: ");
        int numSeats = scanner.nextInt();

        Car car = trip.getCar();
        if (car.getAvailableSeats() < numSeats) {
            System.out.println("Not enough available seats in the car.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Insert booking information into the database
            String insertQuery = "INSERT INTO bookings (user_id, trip_id, num_seats) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setInt(1, user.getId()); // Assuming the User class has an 'id' field
                // Assuming the Trip class has an 'id' field
                statement.setInt(2, trips.indexOf(trip) + 1);
                statement.setInt(3, numSeats);
                statement.executeUpdate();
            }

            // Update the available seats in the car
            int updatedAvailableSeats = car.getAvailableSeats() - numSeats;
            String updateQuery = "UPDATE car SET available_seats = ? WHERE model = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setInt(1, updatedAvailableSeats);
                statement.setString(2, car.getModel());
                statement.executeUpdate();
            }

            // Add the user to the trip's passengers list
            trip.addPassenger(user);

            System.out.println("Trip booked successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}