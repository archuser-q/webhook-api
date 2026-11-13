package com.example.demo.webhook;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/webhooks")
public class GitHubWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);
    private static final Set<String> HANDLED_ACTIONS = Set.of("opened", "synchronize", "reopened");

    private final SignatureVerifier verifier;
    private final JsonMapper mapper;

    public GitHubWebhookController(SignatureVerifier verifier, JsonMapper mapper) {
        this.verifier = verifier;
        this.mapper = mapper;
    }

    @PostMapping("/github")
    public ResponseEntity<Void> handle(
            @RequestBody byte[] body,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader("X-GitHub-Event") String event) {

        if (!verifier.isValid(body, signature)) {
            return ResponseEntity.status(401).build();
        }
        if (!"pull_request".equals(event)) {
            return ResponseEntity.ok().build();
        }

        JsonNode root = mapper.readTree(body);
        String action = root.path("action").asString();
        if (!HANDLED_ACTIONS.contains(action)) {
            return ResponseEntity.ok().build();
        }

        String repo = root.path("repository").path("full_name").asString();
        int number = root.path("pull_request").path("number").asInt();
        String sha = root.path("pull_request").path("head").path("sha").asString();
        log.info("PR event: {} #{} action={} sha={}", repo, number, action, sha);

        // TODO: giao cho ReviewService chạy bất đồng bộ
        return ResponseEntity.accepted().build();
    }
}