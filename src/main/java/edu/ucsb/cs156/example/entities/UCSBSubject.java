package edu.ucsb.cs156.team02.entities;


import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsb_subjects")
public class UCSBSubject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // This establishes that many UCSBSubjects can belong to one user
  // Only the user_id is stored in the table, and through it we
  // can access the user's details

  // @ManyToOne
  // @JoinColumn(name = "ucsb_subjects")

  private String subjectCode;
  private String subjectTranslation;
  private String deptCode;
  private String collegeCode;
  private String relatedDeptCode;
  private boolean inactive;

  public UCSBSubject(long idInput, UCSBSubjectNoId c) {
        id = idInput;
        subjectCode = c.subjectCode;
        subjectTranslation = c.subjectTranslation;
        deptCode = c.deptCode;
        collegeCode = c.collegeCode;
        relatedDeptCode = c.relatedDeptCode;
        inactive = c.inactive;
    }
}