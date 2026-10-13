
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.DonorResponse;

public interface DonorResponseRepository
        extends JpaRepository<DonorResponse, Long> {

    List<DonorResponse> findByBloodRequestId(Long bloodRequestId);

    boolean existsByBloodRequestIdAndDonorId(
        Long bloodRequestId, Long donorId
    );
}
