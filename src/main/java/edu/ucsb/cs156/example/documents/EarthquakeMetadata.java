package edu.ucsb.cs156.example.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Metadata {
    private int generated;
    private String url;
    private String title;
    private int status;
    private String api;
    private int count;
}