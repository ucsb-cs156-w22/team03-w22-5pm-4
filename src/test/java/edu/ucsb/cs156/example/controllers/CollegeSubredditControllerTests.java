package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegeSubreddit;
import edu.ucsb.cs156.team02.entities.CollegeSubredditNoId;
import edu.ucsb.cs156.team02.repositories.CollegeSubredditRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CollegeSubredditController.class)
@Import(TestConfig.class)
public class CollegeSubredditControllerTests extends ControllerTestCase{
    @MockBean
    CollegeSubredditRepository collegeSubredditRepository;

    @MockBean
    UserRepository userRepository;

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void api_collegesubreddit_post() throws Exception{
        // arrange

        CollegeSubreddit expectedCollegeSubreddit = CollegeSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(0L)
                .build();

        when(collegeSubredditRepository.save(eq(expectedCollegeSubreddit))).thenReturn(expectedCollegeSubreddit);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegesubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegeSubredditRepository, times(1)).save(expectedCollegeSubreddit);
        String expectedJson = mapper.writeValueAsString(expectedCollegeSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void api_collegesubreddit_get_all_collegesubreddits() throws Exception{
        // arrange

        CollegeSubreddit collegeSubreddit1 = CollegeSubreddit.builder()
            .name("Name 1")
            .location("Location 1")
            .subreddit("Subreddit 1")
            .id(1L)
            .build();
        CollegeSubreddit collegeSubreddit2 = CollegeSubreddit.builder()
            .name("Name 2")
            .location("Location 2")
            .subreddit("Subreddit 2")
            .id(2L)
            .build();

        ArrayList<CollegeSubreddit> expectedCollegeSubreddits = new ArrayList<>();
        expectedCollegeSubreddits.addAll(Arrays.asList(collegeSubreddit1, collegeSubreddit2));
        when(collegeSubredditRepository.findAll()).thenReturn(expectedCollegeSubreddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegesubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegeSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegeSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_collegesubreddits_search_for_collegesubreddit_that_exists() throws Exception {

        CollegeSubreddit collegeSubreddit1 = CollegeSubreddit.builder().name("Test Name").location("Test Location").subreddit("Test Subreddit").id(7L).build();
        when(collegeSubredditRepository.findById(eq(1L))).thenReturn(Optional.of(collegeSubreddit1));

        // act
        MvcResult response = mockMvc.perform(get("/api/collegesubreddits/getbyid?id=1"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegeSubredditRepository, times(1)).findById(eq(1L));
        String expectedJson = mapper.writeValueAsString(collegeSubreddit1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    
    @WithMockUser(roles = { "USER","ADMIN" })
    @Test
    public void api_collegesubreddits_admin_search_for_collegesubreddit_that_does_not_exist() throws Exception {

        // arrange

        when(collegeSubredditRepository.findById(eq(29L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/collegesubreddits/getbyid?id=29"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(collegeSubredditRepository, times(1)).findById(eq(29L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 29 not found", responseString);
    }

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void api_collegesubreddits_put_collegesubreddit() throws Exception {
        // arrange
        CollegeSubreddit collegeSubreddit1 = CollegeSubreddit.builder()
            .name("Test Name 1")
            .location("Test Location 1")
            .subreddit("Test Subreddit 1")
            .id(1L)
            .build();

        CollegeSubredditNoId updatedCollegeSubredditNoId = CollegeSubredditNoId.builder()
            .name("Test Name New")
            .location("Test Location New")
            .subreddit("Test Subreddit New")
            .build();
        
        CollegeSubreddit updatedCollegeSubreddit = new CollegeSubreddit(1, updatedCollegeSubredditNoId);
        String expectedReturn = mapper.writeValueAsString(updatedCollegeSubreddit);
        String requestBody = mapper.writeValueAsString(updatedCollegeSubreddit);
        //String expectedReturn = mapper.writeValueAsString(correctTodo);

        when(collegeSubredditRepository.findById(eq(1L))).thenReturn(Optional.of(collegeSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegesubreddits/put?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegeSubredditRepository, times(1)).findById(1L);
        verify(collegeSubredditRepository, times(1)).save(updatedCollegeSubreddit);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }
    
    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void api_collegesubreddits_user_cannot_put_collegesubreddit_that_does_not_exist() throws Exception {
        // arrange

        CollegeSubredditNoId updatedCollegeSubredditNoId = CollegeSubredditNoId.builder()
            .name("New Name")
            .location("New Location")
            .subreddit("New Subreddit")
            .build();

        CollegeSubreddit updatedCollegeSubreddit = new CollegeSubreddit(67L, updatedCollegeSubredditNoId);

        String requestBody = mapper.writeValueAsString(updatedCollegeSubreddit);

        when(collegeSubredditRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegesubreddits/put?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegeSubredditRepository, times(1)).findById(67L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("id 67 not found", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
        public void api_subjects__delete_collegesubreddits_that_exists() throws Exception {

        // arrange

        CollegeSubreddit req1 = CollegeSubreddit.builder()
        .name("Test Name")
        .location("Test Location")
        .subreddit("Test Subreddit")
        .id(1L)
        .build();

        when(collegeSubredditRepository.findById(eq(1L))).thenReturn(Optional.of(req1));

        // act
        MvcResult response = mockMvc.perform(
            delete("/api/collegesubreddits/delete?id=1")
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();

        // assert

        verify(collegeSubredditRepository, times(1)).findById(1L);
        verify(collegeSubredditRepository, times(1)).deleteById(1L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("record 1 deleted", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_collegesubreddits_delete_collegesubreddits_that_does_not_exist() throws Exception {

    // arrange

    when(collegeSubredditRepository.findById(eq(1L))).thenReturn(Optional.empty());

    MvcResult response = mockMvc.perform(
    delete("/api/collegesubreddits/delete?id=1")
    .with(csrf()))
    .andExpect(status().isBadRequest()).andReturn();

    // assert

    verify(collegeSubredditRepository, times(1)).findById(1L);
    String responseString = response.getResponse().getContentAsString();
    assertEquals("record 1 not found", responseString);
    
    
}}