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
@Entity(name = "college_subreddits_no_id")
public class CollegeSubredditNoId {
    @Id
    public String name;
    public String location;
    public String subreddit;
}
