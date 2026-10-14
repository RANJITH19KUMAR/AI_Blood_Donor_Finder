
package com.example.demo.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.BloodRequest;
import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorResponse;
import com.example.demo.repository.BloodRequestRepository;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.DonorResponseRepository;

@RestController
@RequestMapping("/blood-requests")
public class BloodRequestController {

    private final BloodRequestRepository repository;
    private final DonorRepository donorRepository;
    private final DonorResponseRepository responseRepository;
    
    public BloodRequestController(
            BloodRequestRepository repository, DonorRepository donorRepository, DonorResponseRepository responseRepository) {
        this.repository = repository;
        this.donorRepository=donorRepository;
		this.responseRepository = responseRepository;
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
    

//    @GetMapping("/{id}/matching-donors")
//    public List<Donor> findMatchingDonors(
//            @PathVariable Long id) {
//
//        BloodRequest request = repository.findById(id)
//            .orElseThrow(() -> new ResponseStatusException(
//                HttpStatus.NOT_FOUND, "Blood request not found"));
//
//        return donorRepository
//            .findByBloodGroupIgnoreCaseAndCityIgnoreCaseAndActiveTrueAndAvailableTrue(
//                request.getBloodGroup(),
//                request.getCity()
//            );
//    }
    

@PostMapping("/{requestId}/responses")
@ResponseStatus(HttpStatus.CREATED)
public DonorResponse respondToRequest(
        @PathVariable Long requestId,
        @RequestParam Long donorId,
        @RequestBody DonorResponse response) {

    // Check that the blood request exists
    repository.findById(requestId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Blood request not found"));

    // Check that the donor exists
    donorRepository.findById(donorId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Donor not found"));

    // Validate the response
    String status = response.getResponseStatus();

    if (status == null ||
        !(status.equalsIgnoreCase("ACCEPTED") ||
          status.equalsIgnoreCase("DECLINED"))) {

        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Response must be ACCEPTED or DECLINED");
    }

    // Prevent duplicate responses
    if (responseRepository
            .existsByBloodRequestIdAndDonorId(requestId, donorId)) {

        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "This donor has already responded to this request");
    }

    response.setBloodRequestId(requestId);
    response.setDonorId(donorId);
    response.setResponseStatus(status.toUpperCase());
    response.setRespondedAt(java.time.LocalDateTime.now());

    return responseRepository.save(response);
}

@GetMapping("/{requestId}/responses")
public List<DonorResponse> getRequestResponses(
        @PathVariable Long requestId) {

    repository.findById(requestId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Blood request not found"));

    return responseRepository.findByBloodRequestId(requestId);
}


@GetMapping("/{id}/matching-donors")
public List<Donor> findMatchingDonors( @PathVariable Long id) {

    BloodRequest request = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Blood request not found"));

    List<Donor> donors = donorRepository
        .findByBloodGroupIgnoreCaseAndActiveTrueAndAvailableTrue(
            request.getBloodGroup());

    String requestedCity = request.getCity();

    donors.sort(
        Comparator.comparing(
            (Donor donor) ->
                donor.getCity() != null &&
                requestedCity != null &&
                donor.getCity().trim()
                    .equalsIgnoreCase(requestedCity.trim())
        ).reversed()
        .thenComparing(
            Donor::getName,
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        )
    );

    return donors;
}

}