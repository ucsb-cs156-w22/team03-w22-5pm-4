package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegeSubreddit;
import edu.ucsb.cs156.team02.entities.CollegeSubredditNoId;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegeSubredditRepository;
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

@Api(description = "CollegeSubreddits")
@RequestMapping("/api/collegesubreddits")
@RestController
@Slf4j
public class CollegeSubredditController extends ApiController {
    /**
     * This inner class helps us factor out some code for checking
     * whether college subreddits exist, and whether they belong to the current user,
     * along with the error messages pertaining to those situations. It
     * bundles together the state needed for those checks.
     */
     public class CollegeSubredditOrError {
        Long id;
        CollegeSubreddit collegesubreddit;
        ResponseEntity<String> error;

        public CollegeSubredditOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    CollegeSubredditRepository collegeSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all collegesubreddits")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')") 
    @GetMapping("/all")
    public Iterable<CollegeSubreddit> allUsersCollegeSubreddits() {
        loggingService.logMethod();
        Iterable<CollegeSubreddit> collegesubreddits = collegeSubredditRepository.findAll();
        return collegesubreddits;
    }

    @ApiOperation(value = "Create a new CollegeSubreddit")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/post")
    public CollegeSubreddit postCollegeSubreddit(
            @ApiParam("College Name") @RequestParam String name,
            @ApiParam("College Location") @RequestParam String location,
            @ApiParam("College Subreddit") @RequestParam String subreddit)
    {
        loggingService.logMethod();

        CollegeSubreddit collegesubreddit = new CollegeSubreddit();
        collegesubreddit.setName(name);
        collegesubreddit.setLocation(location);
        collegesubreddit.setSubreddit(subreddit);

        CollegeSubreddit savedCollegeSubreddit = collegeSubredditRepository.save(collegesubreddit);
        return savedCollegeSubreddit;
    }

    @ApiOperation(value = "Get a single college subreddit")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/getbyid")
    public ResponseEntity<String> getCollegeSubredditById(
            @ApiParam("id of college to look up subreddit") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        CollegeSubredditOrError coe = new CollegeSubredditOrError(id);

        coe = doesCollegeSubredditExist(coe);
        if (coe.error != null) {
            return coe.error;
        }

        String body = mapper.writeValueAsString(coe.collegesubreddit);
        return ResponseEntity.ok().body(body);
    }

    /**
     * Pre-conditions: coe.id is value to look up, coe.todo and coe.error are null
     * 
     * Post-condition: if collegesubreddit with id coe.id exists, coe.todo now refers to it, and
     * error is null.
     * Otherwise, collegesubreddit with id coe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegeSubredditOrError doesCollegeSubredditExist(CollegeSubredditOrError coe) {

        Optional<CollegeSubreddit> optionalCollegeSubreddit = collegeSubredditRepository.findById(coe.id);

        if (optionalCollegeSubreddit.isEmpty()) {
            coe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("id %d not found", coe.id));
        } else {
            coe.collegesubreddit = optionalCollegeSubreddit.get();
        }
        return coe;
    }

    @ApiOperation(value = "Update a single college subreddit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("put")
    public ResponseEntity<String> putCollegeSubredditById(
            @ApiParam("id of college subreddit to edit") @RequestParam Long id,
            @RequestBody @Valid CollegeSubredditNoId incomingCollegeSubredditNoId) throws JsonProcessingException {
        loggingService.logMethod();

        CollegeSubreddit incomingCollegeSubreddit = new CollegeSubreddit(id, incomingCollegeSubredditNoId);
        CollegeSubredditOrError coe = new CollegeSubredditOrError(id);

        coe = doesCollegeSubredditExist(coe);
        if (coe.error != null) {
            return coe.error;
        }

        incomingCollegeSubreddit.setId(id);
        collegeSubredditRepository.save(incomingCollegeSubreddit);

        String body = mapper.writeValueAsString(incomingCollegeSubreddit);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Delete a college subreddit by ID")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("delete")
    public ResponseEntity<String> deleteCollegeSubreddit(
            @ApiParam("id of college subreddit to delete") @RequestParam Long id) {
        loggingService.logMethod();

        CollegeSubredditOrError coe = new CollegeSubredditOrError(id);

        coe = doesCollegeSubredditExistOrDelete(coe);
        if (coe.error != null) {
            return coe.error;
        }

        collegeSubredditRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("record %d deleted", id));

    }

    public CollegeSubredditOrError doesCollegeSubredditExistOrDelete(CollegeSubredditOrError coe) {

        Optional<CollegeSubreddit> optionalCollegeSubreddit = collegeSubredditRepository.findById(coe.id);

        if (optionalCollegeSubreddit.isEmpty()) {
            coe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("record %d not found", coe.id));
        } else {
            coe.collegesubreddit = optionalCollegeSubreddit.get();
        }
        return coe;
    }
}