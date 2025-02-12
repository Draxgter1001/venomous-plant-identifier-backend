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
    @Value("${plant.id.api.key}") //API key loaded from applications properties/config
    private String apiKey;

    // Constructor dependency injection for PlantRepository
    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }
    /**
     * Identifies a plant using Plant.Id API and stores the result
     * @param base64Image Base64 encoded plant image
     * @param userEmail Optional user email to associate with the plant record
     * @return Access token for retrieving details later or error message
     */
    public String identifyPlant(String base64Image, String userEmail) {
        // Prepare API request body with image data
        Map<String, Object> body = new HashMap<>();
        body.put("images", new String[]{base64Image});

        // Configure HTTP headers with API key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Api-Key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://plant.id/api/v3/identification?details=common_names,name,url,description,image,synonyms,toxicity";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            String accessToken = root.path("access_token").asText(); // Adjust this based on API response format
            JsonNode suggestion = root.path("result").path("classification").path("suggestions").get(0);

            // Validate if the detected subject is actually a plant
            boolean isPlant = root.path("result").path("is_plant").path("binary").asBoolean();

            if(!isPlant){
                return "The image is not a plant!";
            }

            // Map API response to Details model
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

    /**
     * Retrieves plant details using stored access token
     * @param accessToken Unique identifier for plant record
     * @return JSON string of plant details or error message
     */
    public String plantDetails(String accessToken) {
        Plant plant = plantRepository.findByAccessToken(accessToken);
        if (plant != null && plant.getDetails() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Convert Details object to JSON string
                return objectMapper.writeValueAsString(plant.getDetails());
            } catch (Exception e) {
                System.err.println("Error converting plant details to JSON: " + e.getMessage());
                return "Error retrieving plant details.";
            }
        }
        return "Plant not found.";
    }

    /**
     * Deletes a plant record using its access token
     * @param accessToken Unique identifier for plant record
     * @return Status message about deletion success/failure
     */
    public String deletePlant(String accessToken) {
        Plant plant = plantRepository.findByAccessToken(accessToken);
        if (plant != null) {
            plantRepository.delete(plant);
            return "Plant deleted.";
        }
        return "Plant not found.";
    }

    /**
     * Fetches all plants associated with a user email
     * @param userEmail Email address to search for
     * @return List of plants linked to the provided email
     */
    public List<Plant> getUserPlants(String userEmail) {
        return plantRepository.findByUserEmail(userEmail);
    }
}
