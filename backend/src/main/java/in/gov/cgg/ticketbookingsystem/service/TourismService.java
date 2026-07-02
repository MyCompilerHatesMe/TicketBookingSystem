package in.gov.cgg.ticketbookingsystem.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import in.gov.cgg.ticketbookingsystem.model.operations.CityTourismData;
import in.gov.cgg.ticketbookingsystem.repository.CityTourismRepo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import in.gov.cgg.ticketbookingsystem.exception.TourismApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourismService {

    private final CityTourismRepo cityTourismRepo;
    private final ObjectMapper objectMapper;

    @Value("${foursquare.api.key}")
    private String foursquareApiKey;

    // can be changed as needs
    private static final int CACHE_DURATION_DAYS = 30;

    public CityTourismData getTourismData(String city) {
        Optional<CityTourismData> existingDataOpt = cityTourismRepo.findByCityIgnoreCase(city);

        if (existingDataOpt.isPresent()) {
            CityTourismData existingData = existingDataOpt.get();
            if (existingData.getLastUpdated().isAfter(LocalDateTime.now().minusDays(CACHE_DURATION_DAYS))) {
                return existingData;
            } else {
                return fetchAndUpdateFoursquareData(existingData);
            }
        } else {
            return fetchAndSaveNewFoursquareData(city);
        }
    }

    private CityTourismData fetchAndSaveNewFoursquareData(String city) {
        String jsonResponse = callFoursquareApi(city);

        Double lat = null;
        Double lon = null;
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode contextNode = root.path("context");
            if (!contextNode.isMissingNode()) {
                JsonNode centerNode = contextNode.path("geo_bounds").path("circle").path("center");
                if (!centerNode.isMissingNode()) {
                    lat = centerNode.path("latitude").asDouble();
                    lon = centerNode.path("longitude").asDouble();
                }
            }
        } catch (Exception e) {
            throw new TourismApiException("Failed to parse Foursquare response for lat/lon", e);
        }

        CityTourismData newData = CityTourismData.builder()
                .city(city.toLowerCase())
                .lat(lat)
                .lon(lon)
                .placesData(jsonResponse)
                .lastUpdated(LocalDateTime.now())
                .build();

        return cityTourismRepo.save(newData);
    }

    private CityTourismData fetchAndUpdateFoursquareData(CityTourismData existingData) {
        String jsonResponse = callFoursquareApi(existingData.getCity());

        // Update everything except lat and long
        existingData.setPlacesData(jsonResponse);
        existingData.setLastUpdated(LocalDateTime.now());

        return cityTourismRepo.save(existingData);
    }

    private final RestTemplate restTemplate = new RestTemplate();

    private String callFoursquareApi(String city) {
        String encodedCity = java.net.URLEncoder.encode(city, StandardCharsets.UTF_8);

        String authHeader = foursquareApiKey.startsWith("Bearer ") ? foursquareApiKey : "Bearer " + foursquareApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Places-Api-Version", "2025-06-17");
        headers.set("accept", "application/json");
        headers.set("authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://places-api.foursquare.com/places/search?fsq_category_ids=4d4b7104d754a06370d81259&near=" + encodedCity + "&limit=10",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getBody() == null) {
                throw new TourismApiException("Empty response body from Foursquare API");
            }
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new TourismApiException("Unexpected code from Foursquare API: " + e.getStatusCode() + ", body: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new TourismApiException("Error calling Foursquare API: " + e.getMessage(), e);
        }
    }
}
