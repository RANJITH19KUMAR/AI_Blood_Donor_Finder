package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.demo.entity.Donor;
import com.example.demo.repository.DonorRepository;

@Service
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    // Register donor
    public Donor registerDonor(Donor donor) {
        donor.setId(null);
        donor.setActive(true);

        return donorRepository.save(donor);
    }

    // Get all active donors
    public List<Donor> getAllDonors() {
        return donorRepository.findByActiveTrue();
    }

    // Get donor by ID
    public Donor getDonorById(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Donor not found"));

        if (!donor.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Donor not found");
        }

        return donor;
    }

    // Update donor
    public Donor updateDonor(Long id, Donor newDonor) {
        Donor donor = getDonorById(id);

        donor.setName(newDonor.getName());
        donor.setPhone(newDonor.getPhone());
        donor.setEmail(newDonor.getEmail());
        donor.setBloodGroup(newDonor.getBloodGroup());
        donor.setCity(newDonor.getCity());
        donor.setLastDonationDate(
                newDonor.getLastDonationDate());
        donor.setAvailable(newDonor.isAvailable());

        return donorRepository.save(donor);
    }

    // Deactivate donor instead of deleting
    public Donor deactivateDonor(Long id) {
        Donor donor = getDonorForAdmin(id);

        donor.setActive(false);
        donor.setAvailable(false);

        return donorRepository.save(donor);
    }

    // Reactivate donor
    public Donor activateDonor(Long id) {
        Donor donor = getDonorForAdmin(id);

        donor.setActive(true);
        donor.setAvailable(false);

        return donorRepository.save(donor);
    }

    // Internal helper for account status changes
    private Donor getDonorForAdmin(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Donor not found"));
    }

public List<Donor> searchDonors(String bloodGroup, String city) {

    if (bloodGroup != null && city != null) {
        return donorRepository
            .findByBloodGroupAndCityIgnoreCaseAndActiveTrue(
                bloodGroup, city);
    }

    if (bloodGroup != null) {
        return donorRepository
            .findByBloodGroupIgnoreCaseAndActiveTrue(bloodGroup);
    }

    return donorRepository.findByActiveTrue();
}


public Donor updateAvailability(Long id, boolean available) {

    Donor donor = donorRepository.findById(id)
        .orElseThrow(() ->
            new RuntimeException("Donor not found with id: " + id));

    donor.setAvailable(available);

    return donorRepository.save(donor);
}

}