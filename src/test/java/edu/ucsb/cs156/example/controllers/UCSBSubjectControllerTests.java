package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {
    @MockBean
    UCSBSubjectRepository ucsbSubjectRepository;

    @MockBean
    UserRepository userRepository; 


    

    @Test
    public void api_ucsbSubject_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk());
    }

    

    @Test
    public void api_ucsbSubject_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBSubjects/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject_post__logged_out__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject_all__user_logged_in() throws Exception {

        
        UCSBSubject ucsbSubject1 = UCSBSubject.builder().subjectCode("SubjectCode 1").subjectTranslation("SubjectTranslation 1").deptCode("DeptCode 1").collegeCode("CollegeCode 1").relatedDeptCode("RelatedDeptCode 1").inactive(true).id(1L).build();
        UCSBSubject ucsbSubject2 = UCSBSubject.builder().subjectCode("SubjectCode 2").subjectTranslation("SubjectTranslation 2").deptCode("DeptCode 2").collegeCode("CollegeCode 2").relatedDeptCode("RelatedDeptCode 2").inactive(true).id(1L).build();

        ArrayList<UCSBSubject> expectedUCSBSubjects = new ArrayList<>();
        expectedUCSBSubjects.addAll(Arrays.asList(ucsbSubject1, ucsbSubject2));
        when(ucsbSubjectRepository.findAll()).thenReturn(expectedUCSBSubjects);

        
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects/all"))
                .andExpect(status().isOk()).andReturn();

        
        verify(ucsbSubjectRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBSubjects);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject_post__user_logged_in() throws Exception {
        

        UCSBSubject expectedUcsb = UCSBSubject.builder()
                .subjectCode("Test Code")
                .subjectTranslation("Test Translation")
                .deptCode("Test DeptCode")
                .collegeCode("Test CollegeCode")
                .relatedDeptCode("Test RelatedDeptCode")
                .inactive(true)
                .id(0L)
                .build();

        when(ucsbSubjectRepository.save(eq(expectedUcsb))).thenReturn(expectedUcsb);

        
        MvcResult response = mockMvc.perform(
                post("/api/UCSBSubjects/post?subjectCode=Test Code&subjectTranslation=Test Translation&deptCode=Test DeptCode&collegeCode=Test CollegeCode&relatedDeptCode=Test RelatedDeptCode&inactive=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        
        verify(ucsbSubjectRepository, times(1)).save(expectedUcsb);
        String expectedJson = mapper.writeValueAsString(expectedUcsb);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }    


    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject__user_logged_in__returns_a_ucsbSubject_that_exists() throws Exception {

        

        UCSBSubject ucsbSubject7 = UCSBSubject.builder().subjectCode("UCSBSubject Code 7")
                .subjectTranslation("UCSBsubject Translation 7").deptCode("UCSBSubject deptCode 7").collegeCode("UCSBSubject collegeCode 7").relatedDeptCode("UCSB relatedDeptCode 7").inactive(false).id(7L).build();
        when(ucsbSubjectRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbSubject7));

        
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isOk()).andReturn();

        

        verify(ucsbSubjectRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(ucsbSubject7);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject__user_logged_in__search_for_ucsbSubject_that_does_not_exist()
            throws Exception {

        

        when(ucsbSubjectRepository.findById(eq(7L))).thenReturn(Optional.empty());

        
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        

        verify(ucsbSubjectRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 7 not found", responseString);
    }
    @Test
    public void api_subjects__delete_subjects() throws Exception {

    // arrange

    UCSBSubject req1 = UCSBSubject.builder()
    .subjectCode("subjectCode")
    .subjectTranslation("subjectTranslation")
    .collegeCode("collegeCode")
    .deptCode("deptCode")
    .collegeCode("collegeCode")
    .relatedDeptCode("relatedDeptCode")
    .inactive(false)
    .id(123L).build();

    when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.of(req1));

    // act
    MvcResult response = mockMvc.perform(
        delete("/api/UCSBSubjects?id=123")
                .with(csrf()))
        .andExpect(status().isOk()).andReturn();

    // assert

    verify(ucsbSubjectRepository, times(1)).findById(123L);
    verify(ucsbSubjectRepository, times(1)).deleteById(123L);
    String responseString = response.getResponse().getContentAsString();
    assertEquals("record 123 deleted", responseString);
    }

    @Test
    public void api_subjects__delete_subjects_that_does_not_exist() throws Exception {

    // arrange

    when(ucsbSubjectRepository.findById(eq(123L))).thenReturn(Optional.empty());

    MvcResult response = mockMvc.perform(
    delete("/api/UCSBSubjects?id=123")
    .with(csrf()))
    .andExpect(status().isBadRequest()).andReturn();

    // assert

    verify(ucsbSubjectRepository, times(1)).findById(123L);
    String responseString = response.getResponse().getContentAsString();
    assertEquals("record 123 not found", responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_subjects__user_logged_in__put_subject() throws Exception {
        // arrange

        
        UCSBSubject ucsbSubject7 = UCSBSubject.builder().subjectCode("UCSBSubject Code 7")
                .subjectTranslation("UCSBsubject Translation 7").deptCode("UCSBSubject deptCode 7").collegeCode("UCSBSubject collegeCode 7").relatedDeptCode("UCSB relatedDeptCode 7").inactive(false).id(7L).build();
        
        UCSBSubject updatedUCSBSubject = UCSBSubject.builder().subjectCode("New UCSBSubject Code 7")
                .subjectTranslation("New UCSBsubject Translation 7").deptCode("New UCSBSubject deptCode 7").collegeCode("New UCSBSubject collegeCode 7").relatedDeptCode("New UCSB relatedDeptCode 7").inactive(false).id(7L).build();
        
        UCSBSubject correctUCSBSubject = UCSBSubject.builder().subjectCode("New UCSBSubject Code 7")
                .subjectTranslation("New UCSBsubject Translation 7").deptCode("New UCSBSubject deptCode 7").collegeCode("New UCSBSubject collegeCode 7").relatedDeptCode("New UCSB relatedDeptCode 7").inactive(false).id(7L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);
        String expectedReturn = mapper.writeValueAsString(correctUCSBSubject);

        when(ucsbSubjectRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbSubject7));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbSubjectRepository, times(1)).findById(7L);
        verify(ucsbSubjectRepository, times(1)).save(correctUCSBSubject); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_subjects__user_logged_in__cannot_put_subject_that_does_not_exist() throws Exception {
        // arrange

        UCSBSubject updatedUCSBSubject = UCSBSubject.builder().subjectCode("UCSBSubject Code 7")
                .subjectTranslation("UCSBsubject Translation 7").deptCode("UCSBSubject deptCode 7").collegeCode("UCSBSubject collegeCode 7").relatedDeptCode("UCSB relatedDeptCode 7").inactive(false).id(7L).build();
        
        String requestBody = mapper.writeValueAsString(updatedUCSBSubject);

        when(ucsbSubjectRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBSubjects?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(ucsbSubjectRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 7 not found", responseString);
    }

}
