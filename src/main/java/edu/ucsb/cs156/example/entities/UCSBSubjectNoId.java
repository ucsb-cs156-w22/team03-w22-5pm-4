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
@Entity(name = "ucsb_subjects_no_id")
public class UCSBSubjectNoId {
    @Id
    public String subjectCode;
    public String subjectTranslation;
    public String deptCode;
    public String collegeCode;
    public String relatedDeptCode;
    public boolean inactive;
}