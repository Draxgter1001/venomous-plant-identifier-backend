package com.example.taf.VPI.service;

import com.example.taf.VPI.model.Details;
import com.example.taf.VPI.model.Plant;
import com.example.taf.VPI.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    @Value("${plant.id.api.key}")
    private String apiKey;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public String identifyPlant(String base64Image, String userEmail) {
        Map<String, Object> body = new HashMap<>();
        body.put("images", new String[]{base64Image});

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Api-Key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://plant.id/api/v3/identification?details=common_names,name,url,description,image,synonyms,toxicity";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText(); // Adjust this based on API response format
            JsonNode suggestion = root.path("result").path("classification").path("suggestions").get(0);

            boolean isPlant = root.path("result").path("is_plant").path("binary").asBoolean();

            if(!isPlant){
                return "The image is not a plant!";
            }

            Details details = new Details();
            details.setCommon_names(objectMapper.convertValue(suggestion.path("details").path("common_names"), List.class));
            details.setName(suggestion.path("name").asText());
            details.setUrl(suggestion.path("details").path("url").asText());
            details.setDescription(suggestion.path("details").path("description").path("value").asText());
            details.setImage(suggestion.path("details").path("image").path("value").asText());
            details.setSynonyms(objectMapper.convertValue(suggestion.path("details").path("synonyms"), List.class));
            details.setToxicity(suggestion.path("details").path("toxicity").asText());

            // Save plant with details
            Plant plant = new Plant();
            plant.setAccessToken(accessToken);
            plant.setDetails(details);
            if(userEmail != null && !userEmail.isEmpty()){
                plant.setUserEmail(userEmail);
            }
            plantRepository.save(plant);
            return accessToken;
        } catch (Exception e) {
            System.err.println("Error calling Plant Identification API: " + e.getMessage());
            return "Error identifying the plant.";
        }
    }

    public String plantDetails(String accessToken) {
        Plant plant = plantRepository.findByAccessToken(accessToken);
        if (plant != null && plant.getDetails() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(plant.getDetails());
            } catch (Exception e) {
                System.err.println("Error converting plant details to JSON: " + e.getMessage());
                return "Error retrieving plant details.";
            }
        }
        return "Plant not found.";
    }

    public String deletePlant(String accessToken) {
        Plant plant = plantRepository.findByAccessToken(accessToken);
        if (plant != null) {
            plantRepository.delete(plant);
            return "Plant deleted.";
        }
        return "Plant not found.";
    }

    public List<Plant> getUserPlants(String userEmail) {
        return plantRepository.findByUserEmail(userEmail);
    }
}
