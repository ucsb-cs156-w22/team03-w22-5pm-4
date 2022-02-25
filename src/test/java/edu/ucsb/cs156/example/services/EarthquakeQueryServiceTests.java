package edu.ucsb.cs156.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.example.documents.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(EarthquakeQueryService.class)
public class EarthquakeQueryServiceTests {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private EarthquakeQueryService earthquakeQueryService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void test_retrieveEarthquakes() throws JsonProcessingException {

        String distanceKm = "10";
        String minMagnitude = "1.5";
        String ucsbLat = "34.4140"; // hard coded params for Storke Tower
        String ucsbLong = "-119.8489";
        String expectedURL = EarthquakeQueryService.ENDPOINT.replace("{distanceKm}", distanceKm)
                .replace("{minMagnitude}", minMagnitude).replace("{latitude}", ucsbLat).replace("{longitude}", ucsbLong);

        // create dummy Earthquake data
        EarthquakeFeatureProperties props = EarthquakeFeatureProperties.builder()
                .mag(5.6)
                .place("testPlace")
                .time(0x1000000000L)
                .updated(0x1000000050L)
                .tz(4)
                .url("testUrl")
                .detail("testDetail")
                .felt(24)
                .cdi(2.6)
                .mmi(5.2)
                .alert("testAlert")
                .status("testStatus")
                .tsunami(0)
                .sig(114)
                .net("testNet")
                .code("testCode")
                .ids("testIds")
                .sources("testSources")
                .types("testTypes")
                .nst(79)
                .dmin(0.0207)
                .rms(0.4)
                .gap(65)
                .magType("testMagType")
                .type("testType")
                .title("testTitle")
                .build();

        EarthquakeFeatureListingMetadata metadata = EarthquakeFeatureListingMetadata.builder()
                .generated(0xFFFFFFFFFFFFFFFFL)
                .url("testUrl")
                .title("testTitle")
                .api("testApi")
                .count(4)
                .status(1)
                .build();

        EarthquakeFeatureGeometry geometry = EarthquakeFeatureGeometry.builder()
                .type("testType")
                .coordinates(List.of(0.0, 1.0, 2.0))
                .id("testId")
                .build();

        EarthquakeFeature quake = EarthquakeFeature.builder()
                ._id("123456789")
                .id("testId")
                .type("Feature")
                .geometry(geometry)
                .properties(props)
                .build();

        EarthquakeFeatureListing listing = EarthquakeFeatureListing.builder()
                .type("FeatureListing")
                .features(List.of(quake))
                .metadata(metadata)
                .build();


        String fakeJsonResult = mapper.writeValueAsString(listing);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andRespond(withSuccess(fakeJsonResult, MediaType.APPLICATION_JSON));

        Iterable<EarthquakeFeature> actualResult = earthquakeQueryService.retrieveEarthquakes(distanceKm, minMagnitude);
        String actualResultJSON = mapper.writeValueAsString(actualResult);
        String expectedResultJSON = mapper.writeValueAsString(listing.getFeatures());

        assertEquals(expectedResultJSON, actualResultJSON);
    }
}