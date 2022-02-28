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
  
    String fakeJsonResult="{ \"fake\" : \"result\" }";
    String distance = "100";
    String minMag = "1.5";
    when(mockEarthquakeQueryService.getJSON(eq(distance),eq(minMag))).thenReturn(fakeJsonResult);

    String url = String.format("/api/earthquakes/get?distance=%s&minMag=%s",distance,minMag);

    MvcResult response = mockMvc
        .perform( get(url).contentType("/application/json"))
        .andExpect(status().isOk()).andReturn();

    String responseString = response.getResponse().getContentAsString();

    assertEquals(fakeJsonResult, responseString);
  }

  @Test
  public void test_getAll() throws Exception {
    FeatureProperties p1 = FeatureProperties.builder()
        .mag(2.5)
        .place("Chris")
        .build();
    Feature f1 = Feature.builder()
        ._id("0")
        .id("0")
        .properties(p1)
        .build();

    FeatureProperties p2 = FeatureProperties.builder()
        .mag(2.6)
        .place("Bob")
        .build();
    Feature f2 = Feature.builder()
        ._id("1")
        .id("1")
        .properties(p2)
        .build();

    List<Feature> list = new ArrayList<Feature>();
    list.add(f1);
    list.add(f2);

    mockEarthquakesCollection.saveAll(list);
    assertEquals(list, mockEarthquakesCollection.findAll());
  }


  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void test_purge() throws Exception{
    mockMvc.perform(post("/api/earthquakes/purge").with(csrf())).andExpect(status().isOk()).andReturn();
    verify(mockEarthquakesCollection, times(1)).deleteAll();
  }

  @Test
  public void test_Retrieve() throws Exception{
    //distace = 100, magnitude = 2.5 - this returns 5
  MvcResult response = mockMvc.perform(
              post("/api/earthquakes/retrieve?distance=100&minMag=2.5")
                              .with(csrf()))
              .andExpect(status().isOk()).andReturn();

    String responseString = response.getResponse().getContentAsString();
    assertEquals("5", responseString);
  }
}

