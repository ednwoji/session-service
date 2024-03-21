package com.unit.session.services;


import com.paypal.core.PayPalHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.UUID;


@Service
@Slf4j
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.payout}")
    private String payPalPayout;

    @Value("${paypal.token}")
    private String payPalToken;

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    public String createPayout(String userId, String recipientEmail, double amount, String currency) {

        String accessToken = generatePaypalAccessToken();
        log.info("Access token is "+accessToken);

        RestTemplate restTemplate = new RestTemplate();
        // Set headers with access token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        String senderItemId = userId+String.valueOf(System.currentTimeMillis());
        log.info(senderItemId);

        // Construct the request body
        String requestBody = "{\"sender_batch_header\": {\"sender_batch_id\": \"Payouts_" + System.currentTimeMillis() + "\",\"email_subject\": \"You have a payout from Unit Session!\",\"email_message\": \"You have received a payout from Unit-Session! Thanks for using our service!\"},\"items\": [{\"recipient_type\": \"EMAIL\",\"amount\": {\"value\": \"" + amount + "\",\"currency\": \"" + currency + "\"},\"note\": \"Thanks for your patronage!\",\"sender_item_id\": \"" + senderItemId + "\",\"receiver\": \"" + recipientEmail + "\",\"notification_language\": \"en-US\"}]}";

        // Create HttpEntity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request using RestTemplate
        ResponseEntity<String> response = restTemplate.exchange(payPalPayout, HttpMethod.POST, requestEntity, String.class);

        // Handle the response
        if (response.getStatusCode() == HttpStatus.CREATED) {
            log.info("Payout request successful. Response: " + response.getBody());
            return extractBatchStatus(response.getBody());
        } else {
            log.info("Error sending payout request. Response code: " + response.getStatusCode());
            return "Failed";
        }
    }

//    public void createPayout2(String recipientEmail, double amount, String currency) {
//
//
//            String accessToken = generatePaypalAccessToken();
//            String baseUrl = "https://api-m.sandbox.paypal.com/v1/payments/payouts";
//
//            try {
//                URL url = new URL(baseUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
//
//                // Construct the request body
//                String requestBody = "{\"sender_batch_header\": {\"sender_batch_id\": \"Payouts_" + System.currentTimeMillis() + "\",\"email_subject\": \"You have a payout!\",\"email_message\": \"You have received a payout! Thanks for using our service!\"},\"items\": [{\"recipient_type\": \"EMAIL\",\"amount\": {\"value\": \"10.00\",\"currency\": \"USD\"},\"note\": \"Thanks for your patronage!\",\"sender_item_id\": \"201403140001\",\"receiver\": \"davidnwoji@gmail.com\",\"notification_language\": \"en-US\"}]}";
//
//                // Enable output and write the request body
//                connection.setDoOutput(true);
//                try (OutputStream outputStream = connection.getOutputStream()) {
//                    byte[] requestBodyBytes = requestBody.getBytes();
//                    outputStream.write(requestBodyBytes);
//                }
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_CREATED) {
//                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                        String inputLine;
//                        StringBuilder response = new StringBuilder();
//                        while ((inputLine = in.readLine()) != null) {
//                            response.append(inputLine);
//                        }
//                        System.out.println("Payout request successful. Response: " + response.toString());
//                    }
//                } else {
//                    System.err.println("Error sending payout request. Response code: " + responseCode);
//                }
//            } catch (IOException e) {
//                System.err.println("Exception occurred: " + e.getMessage());
//            }
//    }


    private String buildPayoutRequestBody(String recipientEmail, double amount, String currency) {
        return "{ \"sender_batch_header\": { \"sender_batch_id\": \"" + UUID.randomUUID().toString() +
                "\", \"email_subject\": \"You have money!\", \"recipient_type\": \"EMAIL\" }, " +
                "\"items\": [{ \"amount\": { \"value\": \"" + amount + "\", \"currency\": \"" + currency +
                "\" }, \"sender_item_id\": \"" + UUID.randomUUID().toString() + "\", " +
                "\"recipient_wallet\": \"PAYPAL\", \"receiver\": \"" + recipientEmail + "\" }] }";
    }


    public String generatePaypalAccessToken() {



        // Encode client ID and client secret for basic authentication
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64Utils.encodeToString(credentials.getBytes());

        // Create headers with basic authentication and content type
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create form data with grant type
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        // Create HttpEntity with headers and form data
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Send POST request to get access token
        ResponseEntity<String> response = restTemplate.exchange(payPalToken, HttpMethod.POST, requestEntity, String.class);

        // Check response status and return access token or null
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            System.out.println("Access token response: " + responseBody);
            JSONObject jsonResponse = extractJsonData(responseBody);
            return jsonResponse.getString("access_token");
        } else {
            System.err.println("Error getting access token: " + response.getStatusCode());
            return null;
        }

    }


    private JSONObject extractJsonData(String accessTokenResponse) {
        JSONObject jsonResponse = new JSONObject(accessTokenResponse);
        return jsonResponse;
    }

    public String extractBatchStatus(String jsonResponse) {
        // Parse the JSON response string into a JSONObject
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Extract the batch_status value from the JSONObject
        JSONObject batchHeader = jsonObject.getJSONObject("batch_header");
        String batchStatus = batchHeader.getString("batch_status");

        return batchStatus;
    }


}
