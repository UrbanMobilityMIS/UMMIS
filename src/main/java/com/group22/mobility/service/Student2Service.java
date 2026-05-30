package com.group22.mobility.service;

import com.group22.mobility.model.Rental;
import com.group22.mobility.model.User;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.repository.mariadb.RentalRepository;
import com.group22.mobility.repository.mariadb.UserRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;

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

    public Student2Service(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            VehicleRepository vehicleRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // =========================
    // RENT VEHICLE
    // =========================

    public String rentVehicle(Integer userId, Integer vehicleId) {

        User user = userRepository.findById(userId).orElseThrow();

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();

        // Check availability
        if (!vehicle.isAvailable()) {
            return "Vehicle is already rented.";
        }

        Rental rental = new Rental();

        rental.setUser(user);
        rental.setVehicle(vehicle);
        rental.setStartTime(LocalDateTime.now());

        rentalRepository.save(rental);

        // Mark unavailable
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
                getFilteredRentals(fromDate, toDate)
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
                getFilteredRentals(fromDate, toDate)
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
}