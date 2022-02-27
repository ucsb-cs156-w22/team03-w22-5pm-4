package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBSubject;
import edu.ucsb.cs156.example.entities.UCSBSubjectNoId;
import edu.ucsb.cs156.example.entities.User;
import edu.ucsb.cs156.example.models.CurrentUser;
import edu.ucsb.cs156.example.repositories.UCSBSubjectRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "UCSBSubjects")
@RequestMapping("/api/UCSBSubjects")
@RestController
@Slf4j
public class UCSBSubjectController extends ApiController {

    public class UCSBSubjectOrError {
        Long id;
        UCSBSubject ucsbSubject;
        ResponseEntity<String> error;

        public UCSBSubjectOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    UCSBSubjectRepository ucsbSubjectRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all UCSBSubjects")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBSubject> allUCSBSubjects() {

        ////loggingService.logMethod();

        Iterable<UCSBSubject> ucsbSubject = ucsbSubjectRepository.findAll();
        return ucsbSubject;
    }

    @ApiOperation(value = "Get a single record in UCSBSubject table (if it exists)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getUCSBSubjectById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {

        ////loggingService.logMethod();


        
        UCSBSubjectOrError soe = new UCSBSubjectOrError(id);

        soe = doesUCSBSubjectExist(soe);
        if (soe.error != null) {
            return soe.error;
        }
        String body = mapper.writeValueAsString(soe.ucsbSubject);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Create a new UCSBSubject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBSubject postUCSBSubject(
            @ApiParam("subjectCode") @RequestParam String subjectCode,
            @ApiParam("subjectTranslation") @RequestParam String subjectTranslation,
            @ApiParam("deptCode") @RequestParam String deptCode,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("relatedDeptCode") @RequestParam String relatedDeptCode,
            @ApiParam("inactive") @RequestParam Boolean inactive) {

        ////loggingService.logMethod();


        UCSBSubject ucsbSubject = new UCSBSubject();
        ucsbSubject.setSubjectCode(subjectCode);
        ucsbSubject.setSubjectTranslation(subjectTranslation);
        ucsbSubject.setDeptCode(deptCode);
        ucsbSubject.setCollegeCode(collegeCode);
        ucsbSubject.setRelatedDeptCode(relatedDeptCode);
        ucsbSubject.setInactive(inactive);
        UCSBSubject savedUcsbSubject = ucsbSubjectRepository.save(ucsbSubject);
        return savedUcsbSubject;
    }

    /**
     * Pre-conditions: coe.id is value to look up, coe.collegiatesubreddit and
     * coe.error are null
     * 
     * Post-condition: if todo with id coe.id exists, coe.collegiateSubreddit now
     * refers to it, and
     * error is null.
     * Otherwise, collegiateSubreddit with id coe.id does not exist, and error is a
     * suitable return
     * value to
     * report this error condition.
     */
    public UCSBSubjectOrError doesUCSBSubjectExist(UCSBSubjectOrError soe) {

        Optional<UCSBSubject> optionalUCSBSubject = ucsbSubjectRepository.findById(soe.id);
        if (optionalUCSBSubject.isEmpty()) {
            soe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("UCSBSubject with id %d not found", soe.id));
        } else {
            soe.ucsbSubject = optionalUCSBSubject.get();
        }
        return soe;
    }

    @ApiOperation(value = "Delete a subject by ID")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUCSBSubject(
            @ApiParam("id") @RequestParam Long id) {

        ////loggingService.logMethod();


        UCSBSubjectOrError roe = new UCSBSubjectOrError(id);

        roe = doesUCSBSubjectExistOrDelete(roe);
        if (roe.error != null) {
            return roe.error;
        }

        ucsbSubjectRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }

    @ApiOperation(value = "Update a single ucsb subject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBSubjectById_admin(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBSubjectNoId incomingUCSBSubjectNoId) throws JsonProcessingException {

        //loggingService.logMethod();


        UCSBSubject incomingUCSBSubject = new UCSBSubject(id,incomingUCSBSubjectNoId);
        UCSBSubjectOrError toe = new UCSBSubjectOrError(id);

        toe = doesUCSBSubjectExist(toe);
        if (toe.error != null) {
            return toe.error;
        }

        ucsbSubjectRepository.save(incomingUCSBSubject);

        String body = mapper.writeValueAsString(incomingUCSBSubject);
        return ResponseEntity.ok().body(body);
    }

    public UCSBSubjectOrError doesUCSBSubjectExistOrDelete(UCSBSubjectOrError roe) {

        Optional<UCSBSubject> optionalUCSBSubject = ucsbSubjectRepository.findById(roe.id);

        if (optionalUCSBSubject.isEmpty()) {
            roe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("record %d not found", roe.id));
        } else {
            roe.ucsbSubject = optionalUCSBSubject.get();
        }
        return roe;
    }

}
