package edu.ucsb.cs156.example.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.repositories.UserRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.test.context.support.WithMockUser;

import edu.ucsb.cs156.example.collections.EarthquakesCollection;
import edu.ucsb.cs156.example.services.EarthquakeQueryService;
import edu.ucsb.cs156.example.documents.Feature;
import edu.ucsb.cs156.example.documents.FeatureCollection;
import edu.ucsb.cs156.example.documents.FeatureProperties;
import edu.ucsb.cs156.example.documents.Metadata;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.http.HttpHeaders;


@WebMvcTest(value = EarthquakesController.class)
public class EarthquakesControllerTests extends ControllerTestCase {
  private ObjectMapper mapper = new ObjectMapper();
  @Autowired
  private MockMvc mockMvc;
  @MockBean
    UserRepository userRepository;

    @MockBean
    EarthquakeQueryService mockEarthquakeQueryService;

    @MockBean 
    EarthquakesCollection mockEarthquakesCollection;
  


  @Test
  public void test_getEarthquakes() throws Exception {

    String fakeJsonResult = "{ \"fake\" : \"result\" }";
    String distance = "100";
    String minMag = "1.5";
    when(mockEarthquakeQueryService.getJSON(eq(distance), eq(minMag))).thenReturn(fakeJsonResult);

    String url = String.format("/api/earthquakes/get?distance=%s&minMag=%s", distance, minMag);

    MvcResult response = mockMvc
        .perform(get(url).contentType("/application/json"))
        .andExpect(status().isOk()).andReturn();

    String responseString = response.getResponse().getContentAsString();

    assertEquals(fakeJsonResult, responseString);
  }

  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void test_getAll() throws Exception {
    FeatureProperties p1 = FeatureProperties.builder()
        .mag(3.05)
        .place(
            "10km ESE of Ojai, CA")
        .time(
            "time")
        .title(
            "title")
        .build();
    Feature f1 = Feature.builder()
        ._id(
            "621c49b8839227546a2283bd")
        .id("ci40194848")
        .type(
            "Feature")
        .properties(p1)
        .build();

    List<Feature> features = new ArrayList<Feature>();
    features.add(f1);

    // MetaData meta = MetaData.builder()
    //     .generated("generated")
    //     .url("url")
    //     .title("title")
    //     .status("status")
    //     .api("api")
    //     .count(4)
    //     .build();

    // FeatureCollection featureCollection = FeatureCollection.builder()
    //     .id("A")
    //     .metadata("A")
    //     .features(features)
    //     .build();
        
    // String distance = "123";
    // String minMag = "123";
    
    MvcResult response = mockMvc.perform(get("/api/earthquakes/all"))
        .andExpect(status().isOk()).andReturn();
             
    verify(mockEarthquakesCollection, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(features);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
}


  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void test_purge() throws Exception{
    mockMvc.perform(delete("/api/earthquakes/purge").with(csrf())).andExpect(status().isOk()).andReturn();
    verify(mockEarthquakesCollection, times(1)).deleteAll();
  }


  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void test_Retrieve() throws Exception{
    FeatureProperties properties = FeatureProperties.builder()
    .mag(6.5)
    .place("test")
    .time("test")
    .title("test")
    .build();

    List<FeatureProperties> lep = new ArrayList<>();
    lep.add(properties);

    Feature feature = Feature.builder()
    ._id("a")
    .type("test")
    .properties(properties)
    .id("test")
    .build();

    List<Feature> lef = new ArrayList<>();
    lef.add(feature);

    Metadata md = Metadata.builder()
        .generated("123d")
        .url("")
        .title("metadata")
        .status("200")
        .api("")
        .count(1)
        .build();

    FeatureCollection el = FeatureCollection.builder()
        .id("123")
        .metadata(md)
        .features(lef)
        .build();

    String magnitude = "10";
    String distance = "100";

    when(mockEarthquakeQueryService.getJSON(distance, magnitude)).thenReturn(mapper.writeValueAsString(el));
    when(mockEarthquakesCollection.saveAll(lef)).thenReturn(lef);

    MvcResult response = mockMvc.perform(post(String.format("/api/earthquakes/retrieve?distance=%s&minMag=%s", distance, magnitude))
        .with(csrf()))
        .andExpect(status().isOk()).andReturn();

    verify(mockEarthquakesCollection, times(1)).saveAll(lef);
    verify(mockEarthquakeQueryService, times(1)).getJSON(distance, magnitude);
    String expectedJson = mapper.writeValueAsString(lef);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
    }
}       

