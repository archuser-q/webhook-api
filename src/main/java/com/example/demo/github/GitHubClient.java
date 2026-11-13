package com.example.demo.github;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubClient {

    private final RestClient client;

    public GitHubClient(
            @Value("${github.api-url}") String apiUrl,
            @Value("${github.token}") String token) {
        this.client = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    public List<ChangedFile> getChangedFiles(String repo, int prNumber) {
        return client.get()
                .uri("/repos/{repo}/pulls/{n}/files?per_page=100", repo, prNumber)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ChangedFile>>() {});
    }
}