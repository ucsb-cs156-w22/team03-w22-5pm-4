package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.collections.EarthquakesCollection;
import edu.ucsb.cs156.example.documents.EarthquakeFeature;
import edu.ucsb.cs156.example.documents.EarthquakeFeatureGeometry;
import edu.ucsb.cs156.example.documents.EarthquakeFeatureProperties;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.services.EarthquakeQueryService;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@WebMvcTest(value = EarthquakesController.class)
public class EarthquakesControllerTests extends ControllerTestCase {

  @MockBean
  EarthquakesCollection earthquakesCollection;

  @MockBean
  EarthquakeQueryService earthquakeQueryService;

  @MockBean
  UserRepository userRepository;

  @WithMockUser(roles = { "ADMIN" })
  @Test
  public void api_earthquakes_retrieve__admin_logged_in__saves_earthquakes_to_collection() throws Exception {

    String distanceKm = "100";
    String minMagnitude = "1.5";
    String url = String.format("/api/earthquakes/retrieve?distanceKm=%s&minMagnitude=%s",distanceKm,minMagnitude);

    // create dummy Earthquake data
    EarthquakeFeatureProperties props = EarthquakeFeatureProperties.builder()
            .mag(5.6)
            .place("testPlace")
            .time(0x1000000000L)
            .updated(0x1000000050L)
            .tz(4)
            .url("testPropertiesUrl")
            .detail("testPropertiesDetail")
            .felt(24)
            .cdi(2.6)
            .mmi(5.2)
            .alert("testPropertiesAlert")
            .status("testPropertiesStatus")
            .tsunami(0)
            .sig(114)
            .net("testPropertiesNet")
            .code("testPropertiesCode")
            .ids("testPropertiesIds")
            .sources("testPropertiesSources")
            .types("testPropertiesTypes")
            .nst(79)
            .dmin(0.0207)
            .rms(0.4)
            .gap(65)
            .magType("testPropertiesMagType")
            .type("testPropertiesType")
            .title("testPropertiesTitle")
            .build();

    EarthquakeFeatureGeometry geometry = EarthquakeFeatureGeometry.builder()
            .type("Point")
            .coordinates(List.of(0.0, 1.0, 2.0))
            .id("testGeometryId")
            .build();

    EarthquakeFeature quake1 = EarthquakeFeature.builder()
            ._id("")
            .id("testQuakeId1")
            .type("Feature")
            .geometry(geometry)
            .properties(props)
            .build();

    EarthquakeFeature quake2 = EarthquakeFeature.builder()
            ._id("")
            .id("testQuakeId2")
            .type("Feature")
            .geometry(geometry)
            .properties(props)
            .build();

    List<EarthquakeFeature> quakeList = List.of(quake1, quake2);

    List<String> quakeListAsJSON = new ArrayList<>();
    for (EarthquakeFeature q : quakeList)
      quakeListAsJSON.add(mapper.writeValueAsString(q));

    List<EarthquakeFeature> savedQuakes = new ArrayList<>();
    for (String q : quakeListAsJSON)
      savedQuakes.add(mapper.readValue(q, EarthquakeFeature.class));

    for (EarthquakeFeature q : savedQuakes)
      q.set_id(q.get_id() + "_saved");

    String savedQuakesAsJSON = mapper.writeValueAsString(savedQuakes);

    when(earthquakesCollection.saveAll(eq(quakeList))).thenReturn(savedQuakes);

    when(earthquakeQueryService.retrieveEarthquakes(eq(distanceKm), eq(minMagnitude))).thenReturn(quakeList);

    MvcResult response = mockMvc.perform(post(url).with(csrf()))
            .andExpect(status().isOk()).andReturn();

    verify(earthquakeQueryService, times(1)).retrieveEarthquakes(eq(distanceKm), eq(minMagnitude));
    verify(earthquakesCollection, times(1)).saveAll(eq(quakeList));
    String responseString = response.getResponse().getContentAsString();
    assertEquals(mapper.writeValueAsString(savedQuakes), responseString);
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void api_earthquakes_retrieve__non_admin_user_logged_in__returns_403() throws Exception {
    mockMvc.perform(post("/api/earthquakes/retrieve?distanceKm=10000&minMagnitude=0"))
            .andExpect(status().is(403));
  }

  @Test
  public void api_earthquakes_retrieve__logged_out__returns_403() throws Exception {
    mockMvc.perform(post("/api/earthquakes/retrieve?distanceKm=10000&minMagnitude=0"))
            .andExpect(status().is(403));
  }

  @WithMockUser(roles = { "USER" })
  @Test
  public void api_earthquakes_all__user_logged_in__returns_all_earthquakes() throws Exception {
    // create dummy Earthquake data
    EarthquakeFeatureProperties props = EarthquakeFeatureProperties.builder()
            .mag(5.6)
            .place("testPlace")
            .time(0x1000000000L)
            .updated(0x1000000050L)
            .tz(4)
            .url("testPropertiesUrl")
            .detail("testPropertiesDetail")
            .felt(24)
            .cdi(2.6)
            .mmi(5.2)
            .alert("testPropertiesAlert")
            .status("testPropertiesStatus")
            .tsunami(0)
            .sig(114)
            .net("testPropertiesNet")
            .code("testPropertiesCode")
            .ids("testPropertiesIds")
            .sources("testPropertiesSources")
            .types("testPropertiesTypes")
            .nst(79)
            .dmin(0.0207)
            .rms(0.4)
            .gap(65)
            .magType("testPropertiesMagType")
            .type("testPropertiesType")
            .title("testPropertiesTitle")
            .build();

    EarthquakeFeatureGeometry geometry = EarthquakeFeatureGeometry.builder()
            .type("Point")
            .coordinates(List.of(0.0, 1.0, 2.0))
            .id("testGeometryId")
            .build();

    EarthquakeFeature quake1 = EarthquakeFeature.builder()
            ._id("")
            .id("testQuakeId1")
            .type("Feature")
            .geometry(geometry)
            .properties(props)
            .build();

    EarthquakeFeature quake2 = EarthquakeFeature.builder()
            ._id("")
            .id("testQuakeId2")
            .type("Feature")
            .geometry(geometry)
            .properties(props)
            .build();

    List<EarthquakeFeature> quakeList = new ArrayList<>();
    quakeList.add(quake1); quakeList.add(quake2);

    when(earthquakesCollection.findAll()).thenReturn(quakeList);

    MvcResult response = mockMvc.perform(get("/api/earthquakes/all"))
            .andExpect(status().isOk()).andReturn();

    verify(earthquakesCollection, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(quakeList);
    String responseJson = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseJson);
  }

  @Test
  public void api_earthquakes_all__logged_out__returns_403() throws Exception {
    mockMvc.perform(get("/api/earthquakes/all")).andExpect(status().is(403));
  }

}