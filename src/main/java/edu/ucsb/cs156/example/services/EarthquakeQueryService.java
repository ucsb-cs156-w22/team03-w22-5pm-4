package edu.ucsb.cs156.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.RestTemplate;

import edu.ucsb.cs156.example.documents.EarthquakeFeatureListing;
import edu.ucsb.cs156.example.documents.EarthquakeFeature;

import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class EarthquakeQueryService {

    ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    public EarthquakeQueryService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public static final String ENDPOINT = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&minmagnitude={minMagnitude}&maxradiuskm={distanceKm}&latitude={latitude}&longitude={longitude}";

    public Iterable<EarthquakeFeature> retrieveEarthquakes(String distanceKm, String minMagnitude) throws HttpClientErrorException, JsonProcessingException {
        log.info("distanceKm={}, minMagnitude={}", distanceKm, minMagnitude);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        final String ucsbLat = "34.4140"; // hard coded params for Storke Tower
        final String ucsbLong = "-119.8489";
        Map<String, String> uriVariables = Map.of("minMagnitude", minMagnitude, "distanceKm", distanceKm, "latitude", ucsbLat,
                "longitude", ucsbLong);

        ResponseEntity<String> re = restTemplate.exchange(ENDPOINT, HttpMethod.GET, entity, String.class,
                uriVariables);

        String jsonResult = re.getBody();

        EarthquakeFeatureListing featureListing = mapper.readValue(jsonResult, EarthquakeFeatureListing.class);

        return featureListing.getFeatures();
    }

   

}