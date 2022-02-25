package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.services.EarthquakeQueryService;
import edu.ucsb.cs156.example.documents.EarthquakeFeature;
import edu.ucsb.cs156.example.collections.EarthquakesCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Api(description="Earthquake info from USGS")
@Slf4j
@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakesController {

    @Autowired
    EarthquakeQueryService earthquakeQueryService;

    @Autowired
    EarthquakesCollection earthquakesCollection;

    @Autowired
    ObjectMapper mapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Get earthquakes a certain distance from UCSB's Storke Tower that are at or above a certain magnitude and add them to the database collection", notes = "JSON return format documented here: https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php")
    @PostMapping("/retrieve")
    public ResponseEntity<Iterable<EarthquakeFeature>> retrieveEarthquakes(
            @ApiParam("distance in km from Storke Tower, e.g. 100") @RequestParam String distanceKm,
            @ApiParam("minimum magnitude, e.g. 2.5") @RequestParam String minMagnitude
    ) throws JsonProcessingException {
        log.info("retrieveEarthquakes: distanceKm={} minMagnitude={}", distanceKm, minMagnitude);

        // query USGS API to get earthquakes
        Iterable<EarthquakeFeature> results = earthquakeQueryService.retrieveEarthquakes(distanceKm, minMagnitude);

        // save results to database
        Iterable<EarthquakeFeature> savedResults = earthquakesCollection.saveAll(results);

         return ResponseEntity.ok().body(savedResults);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "List all earthquakes in the database", notes = "Earthquakes must have been retreived from USGS by an admin first")
    @GetMapping("/all")
    public Iterable<EarthquakeFeature> listEarthquakes() {
        log.info("list earthquakes");

        // get earthquakes from database
        return earthquakesCollection.findAll();
    }
}