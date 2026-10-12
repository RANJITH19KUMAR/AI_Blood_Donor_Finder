
package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Donor;

public interface DonorRepository
        extends JpaRepository<Donor, Long> {

    List<Donor> findByActiveTrue();

    List<Donor> findByBloodGroupAndCityIgnoreCaseAndActiveTrue(
            String bloodGroup, String city);

    List<Donor> findByBloodGroupIgnoreCaseAndActiveTrue(
            String bloodGroup);
}
