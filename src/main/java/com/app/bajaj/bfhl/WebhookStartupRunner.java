package com.app.bajaj.bfhl;

import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookStartupRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        System.out.println(" init() method started...");  // NEW

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Darsh");
        requestBody.put("regNo", "22UEC036");
        requestBody.put("email", "22UEC036@lnmiit.ac.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            System.out.println(" Webhook API responded with status: " + response.getStatusCode());
            System.out.println(" Full Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody != null) {
                    String webhookUrl = (String) responseBody.get("webhook");
                    String accessToken = (String) responseBody.get("accessToken");

                    System.out.println(" Webhook URL: " + webhookUrl);
                    System.out.println(" Access Token: " + accessToken);

                    submitAnswer(webhookUrl, accessToken);
                } else {
                    System.out.println(" Response body is null.");
                }
            } else {
                System.out.println(" Webhook generation failed. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println(" Exception during webhook generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void submitAnswer(String webhookUrl, String accessToken) {
        System.out.println(" submitAnswer() started...");

        String finalQuery = """
            SELECT 
                E.EMP_ID, 
                E.FIRST_NAME, 
                E.LAST_NAME, 
                D.DEPARTMENT_NAME,
                (
                    SELECT COUNT(*) 
                    FROM EMPLOYEE E2
                    WHERE 
                        E2.DEPARTMENT = E.DEPARTMENT
                        AND E2.DOB > E.DOB
                ) AS YOUNGER_EMPLOYEES_COUNT
            FROM EMPLOYEE E
            JOIN DEPARTMENT D ON E.DEPARTMENT = D.DEPARTMENT_ID
            ORDER BY E.EMP_ID DESC
        """;

        Map<String, String> payload = new HashMap<>();
        payload.put("finalQuery", finalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);


        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> result = restTemplate.postForEntity(webhookUrl, entity, String.class);
            System.out.println(" Submission Status: " + result.getStatusCode());
            System.out.println(" Response: " + result.getBody());
        } catch (Exception e) {
            System.out.println(" Exception during SQL submission: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
