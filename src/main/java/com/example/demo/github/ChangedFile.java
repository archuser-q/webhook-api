package com.example.demo.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangedFile(String filename, String status, int additions, int deletions, String patch) {}