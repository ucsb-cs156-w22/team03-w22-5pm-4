package edu.ucsb.cs156.team02.entities;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import javax.persistence.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "college_subreddits")
public class CollegeSubreddit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String location;
    private String subreddit;

    public CollegeSubreddit(long idInput, CollegeSubredditNoId c) {
        id = idInput;
        name = c.name;
        location = c.location;
        subreddit = c.subreddit;
    }
}

