package com.group22.mobility.service;
import com.group22.mobility.model.Rental;
import com.group22.mobility.model.User;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.repository.mariadb.RentalRepository;
import com.group22.mobility.repository.mariadb.UserRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.model.mangodb.UserDocument;
import com.group22.mobility.model.mangodb.VehicleDocument;
import com.group22.mobility.repository.mariadb.mangodb.UserMongoRepository;
import com.group22.mobility.repository.mariadb.mangodb.VehicleMongoRepository;
import com.group22.mobility.model.mangodb.RentalDocument;
import com.group22.mobility.repository.mariadb.mangodb.RentalMongoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class Student2Service {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalMongoRepository rentalMongoRepository;
    private final UserMongoRepository userMongoRepository;
    private final VehicleMongoRepository vehicleMongoRepository;

    public Student2Service(
        RentalRepository rentalRepository,
        UserRepository userRepository,
        VehicleRepository vehicleRepository,
        RentalMongoRepository rentalMongoRepository,
        UserMongoRepository userMongoRepository,
        VehicleMongoRepository vehicleMongoRepository
       ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalMongoRepository = rentalMongoRepository;
        this.userMongoRepository = userMongoRepository;
        this.vehicleMongoRepository = vehicleMongoRepository;
        }

    // =========================
    // RENT VEHICLE
    // =========================

    public String rentVehicle(Integer userId, Integer vehicleId) {

    User user = userRepository.findById(userId)
            .orElseThrow();

    Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow();

    // Check availability
    if (!vehicle.isAvailable()) {
        return "Vehicle is already rented.";
    }

    Rental rental = new Rental();
    rental.setUser(user);
    rental.setVehicle(vehicle);
    rental.setStartTime(LocalDateTime.now());
    rentalRepository.save(rental);

    // Mark vehicle as rented
    vehicle.setAvailable(false);
    vehicleRepository.save(vehicle);
    return "Vehicle rented successfully.";
}

    // =========================
    // FILTERED RENTALS
    // =========================

    public List<Rental> getFilteredRentals(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        List<Rental> rentals = rentalRepository.findAll();

        if (fromDate != null && toDate != null) {

            return rentals.stream()
                    .filter(rental -> {

                        LocalDate rentalDate =
                                rental.getStartTime().toLocalDate();

                        return !rentalDate.isBefore(fromDate)
                                && !rentalDate.isAfter(toDate);
                    })
                    .collect(Collectors.toList());
        }

        return rentals;
    }

    // =========================
    // TOTAL RENTALS
    // =========================

    public long getFilteredTotalRentals(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        return getFilteredRentals(fromDate, toDate).size();
    }

    // =========================
    // MOST ACTIVE USER
    // =========================

    public String getFilteredMostActiveUser(
        LocalDate fromDate,
        LocalDate toDate
) {

    Map<String, Long> rentalsPerUser =
            rentalRepository.findByEndTimeIsNull()
                    .stream()
                    .collect(Collectors.groupingBy(
                            rental -> rental.getUser().getEmailAddress(),
                            Collectors.counting()
                    ));

    long maxRentals = rentalsPerUser.values()
            .stream()
            .max(Long::compare)
            .orElse(0L);

    return rentalsPerUser.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(maxRentals))
            .map(Map.Entry::getKey)
            .collect(Collectors.joining(", "));
}

    // =========================
    // MOST RENTED VEHICLE
    // =========================

    public String getFilteredMostRentedVehicle(
        LocalDate fromDate,
        LocalDate toDate
) {

    Map<String, Long> rentalsPerVehicle =
            rentalRepository.findByEndTimeIsNull()
                    .stream()
                    .collect(Collectors.groupingBy(
                            rental -> rental.getVehicle().getModelType(),
                            Collectors.counting()
                    ));

    long maxRentals = rentalsPerVehicle.values()
            .stream()
            .max(Long::compare)
            .orElse(0L);

    return rentalsPerVehicle.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(maxRentals))
            .map(Map.Entry::getKey)
            .collect(Collectors.joining(", "));
}

    // =========================
    // RENTALS PER VEHICLE
    // =========================

    public Map<String, Long> getFilteredRentalsPerVehicle(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        return getFilteredRentals(fromDate, toDate)
                .stream()
                .collect(Collectors.groupingBy(
                        rental -> rental.getVehicle().getModelType(),
                        Collectors.counting()
                ));
    }

       // =========================
      // MONGODB RENT VEHICLE
      // =========================

        public String rentVehicleMongo(Integer userId, Integer vehicleId) {

        UserDocument user =
                userMongoRepository.findByUserId(userId)
                        .orElseThrow();

        VehicleDocument vehicle =
                vehicleMongoRepository.findByVehicleId(vehicleId)
                        .orElseThrow();

        if (!vehicle.isAvailable()) {
                return "Vehicle is already rented.";
        }

        vehicle.setAvailable(false);
        vehicleMongoRepository.save(vehicle);

        RentalDocument doc = new RentalDocument();

      List<Integer> ids = rentalMongoRepository.findAll()
        .stream()
        .map(RentalDocument::getRentalId)
        .sorted()
        .toList();

        Integer nextRentalId;

        if (ids.isEmpty()) {
        nextRentalId = 1;
        } else {

        nextRentalId = ids.get(ids.size() - 1) + 1;

        for (int i = ids.get(0); i < ids.get(ids.size() - 1); i++) {

                if (!ids.contains(i)) {
                nextRentalId = i;
                break;
                }
        }
        }

       // System.out.println("================================");
       // System.out.println("Current IDs: " + ids);
       // System.out.println("Next Rental ID: " + nextRentalId);
       // System.out.println("================================");

        doc.setRentalId(nextRentalId);

        doc.setUserId(user.getUserId());
        doc.setUserEmail(user.getEmailAddress());

        doc.setVehicleId(vehicle.getVehicleId());
        doc.setVehicleModel(vehicle.getModelType());

        doc.setStartTime(LocalDateTime.now());

        rentalMongoRepository.save(doc);

        return "Vehicle rented successfully in MongoDB.";
        }

        public List<Rental> getActiveRentals() {
        return rentalRepository.findByEndTimeIsNull();
        }

        public String returnVehicle(Integer rentalId) {

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow();

        if (rental.getEndTime() != null) {
        return "Rental already returned.";

        }

        rental.setEndTime(LocalDateTime.now());

        Vehicle vehicle = rental.getVehicle();
        vehicle.setAvailable(true);
        
        rentalRepository.save(rental);
        vehicleRepository.save(vehicle);

        return "Vehicle returned successfully.";

        }
        // =========================
       // MONGODB RETURN VEHICLE
       // =========================

        public String returnVehicleMongo(Integer rentalId) {

        RentalDocument rental =
                rentalMongoRepository.findByRentalId(rentalId)
                        .orElseThrow();

        if (rental.getEndTime() != null) {
                return "Rental already returned.";
        }

        rental.setEndTime(LocalDateTime.now());

        VehicleDocument vehicle =
                vehicleMongoRepository.findByVehicleId(
                        rental.getVehicleId()
                ).orElseThrow();

        vehicle.setAvailable(true);

        rentalMongoRepository.save(rental);
        vehicleMongoRepository.save(vehicle);

        return "Vehicle returned successfully in MongoDB.";
        }
        public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
        }

        }