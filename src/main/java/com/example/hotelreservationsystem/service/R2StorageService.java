package com.example.hotelreservationsystem.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
@Service
public class R2StorageService {
    private final S3Client s3Client;
    private final String bucketName;
    public R2StorageService(S3Client s3Client, @Value("${r2.bucketName}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }


    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String keyName = UUID.randomUUID().toString() + fileExtension;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            return s3Client.utilities().getUrl(getUrlRequest).toString();
        } catch (Exception e) {
            throw new IOException("R2-yə şəkil yüklənməsi uğursuz oldu: " + e.getMessage());
        }
    }


    public String uploadImageFromUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Şəkil URL-i boş ola bilməz");
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 saniyə
            connection.setReadTimeout(10000); // 10 saniyə
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Şəkil URL-indən yüklənə bilmədi. Response code: " + responseCode);
            }

            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = "image/jpeg";
            }
            
            long contentLength = connection.getContentLengthLong();
            if (contentLength <= 0) {
                contentLength = -1;
            }

            inputStream = connection.getInputStream();

            String fileExtension = getFileExtensionFromContentType(contentType);
            if (fileExtension.isEmpty()) {
                // URL-dən uzantı çıxarılmasına cəhd edilir
                String urlPath = url.getPath();
                int lastDot = urlPath.lastIndexOf('.');
                if (lastDot > 0 && lastDot < urlPath.length() - 1) {
                    fileExtension = urlPath.substring(lastDot);
                } else {
                    fileExtension = ".jpg"; // Default
                }
            }

            String keyName = "hotels/" + UUID.randomUUID().toString() + fileExtension;

            PutObjectRequest.Builder putObjectBuilder = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(contentType);

            if (contentLength > 0) {
                putObjectBuilder.contentLength(contentLength);
            }

            PutObjectRequest putObjectRequest = putObjectBuilder.build();
            
            RequestBody requestBody = contentLength > 0 
                    ? RequestBody.fromInputStream(inputStream, contentLength)
                    : RequestBody.fromInputStream(inputStream, -1);

            s3Client.putObject(putObjectRequest, requestBody);

            // R2-dəki şəkilin URL-ini qaytar
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            
            return s3Client.utilities().getUrl(getUrlRequest).toString();

        } catch (Exception e) {
            throw new IOException("URL-dən şəkil yüklənib R2-yə upload edilərkən xəta: " + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Content-Type-dan fayl uzantısını müəyyən edir
     */
    private String getFileExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return ".jpg";
        } else if (contentType.contains("png")) {
            return ".png";
        } else if (contentType.contains("gif")) {
            return ".gif";
        } else if (contentType.contains("webp")) {
            return ".webp";
        } else if (contentType.contains("bmp")) {
            return ".bmp";
        }
        
        return "";
    }

}