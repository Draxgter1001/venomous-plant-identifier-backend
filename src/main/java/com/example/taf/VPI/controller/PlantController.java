package com.example.taf.VPI.controller;

import com.example.taf.VPI.model.Plant;
import com.example.taf.VPI.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://venomous-plant-identifier-frontend.vercel.app/")
@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping("/identify")
    public ResponseEntity<String> identifyPlant(@RequestBody Map<String, String> request) {
        String base64Image = request.get("base64Image");
        String userEmail = request.get("userEmail");
        String result = plantService.identifyPlant(base64Image, userEmail);
        return ResponseEntity.ok("{\"token\": \"" + result + "\"}");
    }

    @GetMapping("/details/{accessToken}")
    public ResponseEntity<String> getPlantDetails(@PathVariable String accessToken) {
        String result = plantService.plantDetails(accessToken);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user-plants/{email}")
    public ResponseEntity<List<Plant>> getUserPlants(@PathVariable String email){
        List<Plant> plants = plantService.getUserPlants(email);
        return ResponseEntity.ok(plants);
    }

    @DeleteMapping("/delete/{accessToken}")
    public ResponseEntity<String> deletePlant(@PathVariable String accessToken) {
        String result = plantService.deletePlant(accessToken);
        return ResponseEntity.ok(result);
    }
}
