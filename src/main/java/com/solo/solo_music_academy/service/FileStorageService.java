package com.solo.solo_music_academy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.github.token}")
    private String githubToken;

    @Value("${app.github.owner}")
    private String owner;

    @Value("${app.github.repo}")
    private String repo;

    @Value("${app.github.branch:main}")
    private String branch;

    private final RestTemplate restTemplate = new RestTemplate();

    // ========= UPLOAD =========
    public String saveAttendanceImage(MultipartFile file, Long slotId, LocalDate date) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.lastIndexOf('.') != -1) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }

        String path = "attendance/slot_" + slotId + "_" + date + ext;
        return uploadToGitHub(file, path);
    }

    @SuppressWarnings("unchecked")
    private String uploadToGitHub(MultipartFile file, String path) throws IOException {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Upload attendance image " + path);
        body.put("content", Base64.getEncoder().encodeToString(file.getBytes()));
        body.put("branch", branch);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                Map.class
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Upload to GitHub failed: " + resp.getStatusCode());
        }

        Map<String, Object> respBody = resp.getBody();
        Map<String, Object> content = (Map<String, Object>) respBody.get("content");
        if (content == null) {
            throw new IllegalStateException("GitHub response has no 'content' field");
        }

        String downloadUrl = (String) content.get("download_url");
        if (downloadUrl != null && !downloadUrl.isBlank()) {
            return downloadUrl;
        }

        // Fallback raw URL
        return "https://raw.githubusercontent.com/"
                + owner + "/" + repo + "/" + branch + "/" + path;
    }

    // ========= DELETE THEO URL =========
    public void deleteAttendanceImageByUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        // Tìm đoạn owner/repo/ trong URL để lấy path
        String marker = owner + "/" + repo + "/";
        int idx = imageUrl.indexOf(marker);
        if (idx == -1) {
            log.warn("Cannot extract path from imageUrl: {}", imageUrl);
            return;
        }

        String after = imageUrl.substring(idx + marker.length()); // ví dụ: main/attendance/slot_1_2025-12-03.jpg
        int slash = after.indexOf('/');
        if (slash == -1) {
            log.warn("Cannot extract branch/path from imageUrl: {}", imageUrl);
            return;
        }

        String branchFromUrl = after.substring(0, slash);
        String path = after.substring(slash + 1);

        String effectiveBranch = (branchFromUrl != null && !branchFromUrl.isBlank())
                ? branchFromUrl
                : this.branch;

        deleteFromGitHub(path, effectiveBranch);
    }

    @SuppressWarnings("unchecked")
    private void deleteFromGitHub(String path, String branchName) throws IOException {
        // 1) Lấy sha của file
        String getUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + path + "?ref=" + branchName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> getResp;
        try {
            getResp = restTemplate.exchange(getUrl, HttpMethod.GET, getEntity, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            // File không tồn tại coi như đã xoá xong
            log.warn("GitHub file not found when deleting: {}", path);
            return;
        }

        if (!getResp.getStatusCode().is2xxSuccessful() || getResp.getBody() == null) {
            throw new IllegalStateException("Cannot get file info from GitHub: " + getResp.getStatusCode());
        }

        Map<String, Object> body = getResp.getBody();
        String sha = (String) body.get("sha");
        if (sha == null || sha.isBlank()) {
            throw new IllegalStateException("GitHub file has no sha: " + path);
        }

        // 2) Gửi DELETE
        String deleteUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + path;

        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.setContentType(MediaType.APPLICATION_JSON);
        deleteHeaders.setBearerAuth(githubToken);

        Map<String, Object> deleteBody = new HashMap<>();
        deleteBody.put("message", "Delete attendance image " + path);
        deleteBody.put("sha", sha);
        deleteBody.put("branch", branchName);

        HttpEntity<Map<String, Object>> deleteEntity = new HttpEntity<>(deleteBody, deleteHeaders);

        ResponseEntity<Map> deleteResp = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                deleteEntity,
                Map.class
        );

        if (!deleteResp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Delete from GitHub failed: " + deleteResp.getStatusCode());
        }
    }
}
