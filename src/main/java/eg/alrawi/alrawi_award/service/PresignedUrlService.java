package eg.alrawi.alrawi_award.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private final S3Presigner s3Presigner;
    private final static String bucketName="alrawi-awards-app";

    public String generatePreSignedUrl(String key, long expirationMinutes) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedObject = s3Presigner.presignGetObject(presignRequest);

        URL url = presignedObject.url();
        return url.toString();
    }


    public URL generateUploadUrl(Long userId, String fileName, String contentType) {

        long maxBytes = 500 * 1024 * 1024;

        String key = "Al_RAWI/" + userId + "/" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket("ets-media")
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // URL valid 10 min
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        return presigned.url();
    }


    public List<String> generateUploadLinks(List<String> filenames) {
        List<String> links = new ArrayList<>();

        for (String name : filenames) {
            String key = "Al_RAWI/" + name;

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("ets-media")
                    .key(key)
                    .contentType("image/png")
                    .build();

            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                    b -> b.signatureDuration(Duration.ofMinutes(70))
                            .putObjectRequest(objectRequest)
            );


            links.add(presigned.url().toString());
        }

        return links;
    }


    public String generateVideoUploadLink(String key,String contentType) {

        String link = "";

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                    b -> b.signatureDuration(Duration.ofMinutes(70))
                            .putObjectRequest(objectRequest)
            );
            link=presigned.url().toString();


        return link;
    }




}
