package com.example.demo.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.github.ChangedFile;

@Component
public class DiffFilter {

    private static final int MAX_FILES = 20;
    private static final int MAX_PATCH_CHARS = 12_000;
    private static final Set<String> IGNORED_NAMES = Set.of(
            "package-lock.json", "yarn.lock", "pnpm-lock.yaml", "pom.xml.lock", "go.sum");
    private static final Set<String> IGNORED_SUFFIXES = Set.of(
            ".min.js", ".svg", ".png", ".jpg", ".gif", ".pdf", ".lock");

    public List<ChangedFile> filter(List<ChangedFile> files) {
        List<ChangedFile> result = new ArrayList<>();
        for (ChangedFile f : files) {
            if (result.size() >= MAX_FILES) break;
            if (f.patch() == null || f.patch().isBlank()) continue; // file nhị phân hoặc diff quá lớn
            if (isIgnored(f.filename())) continue;
            String patch = f.patch().length() > MAX_PATCH_CHARS
                    ? f.patch().substring(0, MAX_PATCH_CHARS) + "\n...[truncated]"
                    : f.patch();
            result.add(new ChangedFile(f.filename(), f.status(), f.additions(), f.deletions(), patch));
        }
        return result;
    }

    private boolean isIgnored(String name) {
        String base = name.substring(name.lastIndexOf('/') + 1);
        if (IGNORED_NAMES.contains(base)) return true;
        return IGNORED_SUFFIXES.stream().anyMatch(name::endsWith);
    }
}