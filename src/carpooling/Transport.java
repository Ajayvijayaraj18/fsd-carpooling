package carpooling;

interface Transport {
    void offerTrip(String destination);
    boolean bookTrip(String userName, String destination, int numSeats);
}
