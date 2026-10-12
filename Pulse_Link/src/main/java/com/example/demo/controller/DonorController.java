package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.demo.entity.Donor;
import com.example.demo.service.DonorService;

@RestController
@RequestMapping("/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    // POST: Register donor
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Donor registerDonor(
            @Valid @RequestBody Donor donor) {

        return donorService.registerDonor(donor);
    }

    // GET: Get all active donors
    @GetMapping
    public List<Donor> getAllDonors() {
        return donorService.getAllDonors();
    }

    // GET: Get donor by ID
    @GetMapping("/{id}")
    public Donor getDonorById(@PathVariable Long id) {
        return donorService.getDonorById(id);
    }
    

@GetMapping("/search")
public ResponseEntity<List<Donor>> searchDonors(
        @RequestParam(required = false) String bloodGroup,
        @RequestParam(required = false) String city) {

    return ResponseEntity.ok(
        donorService.searchDonors(bloodGroup, city)
    );
}


    // PUT: Update donor
    @PutMapping("/{id}")
    public Donor updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody Donor donor) {

        return donorService.updateDonor(id, donor);
    }

    // PATCH: Deactivate donor
    @PatchMapping("/{id}/deactivate")
    public Donor deactivateDonor(@PathVariable Long id) {
        return donorService.deactivateDonor(id);
    }

    // PATCH: Reactivate donor
    @PatchMapping("/{id}/activate")
    public Donor activateDonor(@PathVariable Long id) {
        return donorService.activateDonor(id);
    }
}