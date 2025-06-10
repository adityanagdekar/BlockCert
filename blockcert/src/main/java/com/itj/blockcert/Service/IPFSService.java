package com.itj.blockcert.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class IPFSService {
    private static final Logger logger = LoggerFactory.getLogger(IPFSService.class);

    @Value("${ipfs.pinata.api.url}")
    private String pinataApiUrl;

    @Value("${ipfs.pinata.api.key}")
    private String pinataApiKey;

    @Value("${ipfs.pinata.secret.key}")
    private String pinataSecretKey;

    @Value("${ipfs.pinata.gateway.url}")
    private String pinataGatewayUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IPFSService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Upload file to IPFS via Pinata
     */
    public String uploadFile(MultipartFile file) throws IOException {
        logger.info("Uploading file to IPFS: {}", file.getOriginalFilename());
        
        // Prepare multipart form data
        String boundary = "Boundary-" + UUID.randomUUID().toString();
        
        // Prepare the request body
        StringBuilder requestBodyBuilder = new StringBuilder();
        
        // Add file part
        requestBodyBuilder.append("--").append(boundary).append("\r\n");
        requestBodyBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(file.getOriginalFilename()).append("\"\r\n");
        requestBodyBuilder.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
        
        byte[] fileData = file.getBytes();
        byte[] headerBytes = requestBodyBuilder.toString().getBytes();
        byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes();
        
        // Combine all parts
        byte[] requestBytes = new byte[headerBytes.length + fileData.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, requestBytes, 0, headerBytes.length);
        System.arraycopy(fileData, 0, requestBytes, headerBytes.length, fileData.length);
        System.arraycopy(footerBytes, 0, requestBytes, headerBytes.length + fileData.length, footerBytes.length);
        
        // Create request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(pinataApiUrl + "/pinning/pinFileToIPFS"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("pinata_api_key", pinataApiKey)
                .header("pinata_secret_api_key", pinataSecretKey)
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes))
                .build();
        
        // Send request and handle response
        // Send request and handle response
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Check if request was successful
            if (response.statusCode() != 200) {
                logger.error("Failed to upload to IPFS: {}", response.body());
                throw new IOException("Failed to upload to IPFS: " + response.body());
            }
            
            // Parse response to get IPFS CID
            JsonNode responseJson = objectMapper.readTree(response.body());
            String ipfsCid = responseJson.get("IpfsHash").asText();
            
            logger.info("File uploaded successfully to IPFS with CID: {}", ipfsCid);
            return ipfsCid;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            logger.error("Request to Pinata was interrupted", e);
            throw new IOException("IPFS upload interrupted", e);
        }
    }

    /**
     * Get the gateway URL for an IPFS CID
     */
    public String getGatewayUrl(String cid) {
        return pinataGatewayUrl + cid;
    }

    public byte[] downloadFileFromCid(String cid) throws IOException {
    String url = pinataGatewayUrl + cid;

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

    try {
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch file from IPFS. Status: " + response.statusCode());
        }
        return response.body();
    } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrupted", e);
        }
    }
}
