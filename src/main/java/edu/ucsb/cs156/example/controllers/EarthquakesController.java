package edu.ucsb.cs156.example.controllers;

import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.example.collections.EarthquakesCollection;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.example.documents.Feature;
import edu.ucsb.cs156.example.documents.FeatureCollection;
import edu.ucsb.cs156.example.documents.FeatureProperties;
import edu.ucsb.cs156.example.documents.Metadata;
import edu.ucsb.cs156.example.services.EarthquakeQueryService;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

@Api(description="Earthquake info from USGS")
@RequestMapping("/api/earthquakes")
@RestController
public class EarthquakesController extends ApiController{
    
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    EarthquakeQueryService earthquakeQueryService;

    @Autowired
    EarthquakesCollection earthquakesCollection;

    // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @ApiOperation(value = "Get earthquakes a certain distance from UCSB's Storke Tower", notes = "JSON return format documented here: https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php")
    @GetMapping("/get")
    public ResponseEntity<String> getEarthquakes(
        @ApiParam("distance in km, e.g. 100") @RequestParam String distance,
        @ApiParam("minimum magnitude, e.g. 2.5") @RequestParam String minMag
    ) throws JsonProcessingException {
        String result = earthquakeQueryService.getJSON(distance, minMag);
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation(value = "List all Earthquakes")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public ResponseEntity<List<Feature>> index() throws JsonProcessingException{
        return ResponseEntity.ok().body(earthquakesCollection.findAll());
    }

    @ApiOperation(value = "Delete all earthquakes from Earthquakes Collection")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/purge")
    public void purge() throws JsonProcessingException{
        earthquakesCollection.deleteAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "Retrieve Earthquakes from UCSB's Storke Tower that above a magnitude and put them in database collection", notes = "JSON return format documented here: https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php")
    @PostMapping("/retrieve")
    public List<Feature> postEarthquakeFeature(
        @ApiParam("distance in km, e.g. 100") @RequestParam String distance,
        @ApiParam("minimum magnitude, e.g. 2.5") @RequestParam String minMag
    ) throws JsonProcessingException {
        //log.info("getEarthquakes: distance={} minMag={}", distance, minMag);
        String result = earthquakeQueryService.getJSON(distance, minMag);
        FeatureCollection collection = mapper.readValue(result, FeatureCollection.class);
        List<Feature> features = collection.getFeatures();
        earthquakesCollection.saveAll(features);
        return features;
    }

}