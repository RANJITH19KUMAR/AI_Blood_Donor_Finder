
package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.BloodRequest;
import com.example.demo.repository.BloodRequestRepository;

@RestController
@RequestMapping("/blood-requests")
public class BloodRequestController {

    private final BloodRequestRepository repository;

    public BloodRequestController(
            BloodRequestRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BloodRequest createRequest(
            @RequestBody BloodRequest request) {

        request.setStatus("PENDING");
        return repository.save(request);
    }

    @GetMapping
    public List<BloodRequest> getAllRequests() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public BloodRequest getRequestById(
            @PathVariable Long id) {

        return repository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Blood request not found"));
    }
}
