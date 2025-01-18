package com.example.taf.VPI.repository;

import com.example.taf.VPI.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Plant findByAccessToken(String accessToken);
    List<Plant> findByUserEmail(String userEmail);
}
