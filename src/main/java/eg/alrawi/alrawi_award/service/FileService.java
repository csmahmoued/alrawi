package eg.alrawi.alrawi_award.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class FileService {

    private final Logger log= LoggerFactory.getLogger(FileService.class);
    private final static String bucketName="alrawi-awards-app";
    private final S3Client s3Client;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String bucketPrefix,MultipartFile file,String key)  {

    try {

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key+"."+getExtension(file))
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        }catch (Exception e){
            log.info("an error has been occurred while upload image  ",e);
        }
    }

    public void uploadFile(MultipartFile file,String key)  {

        try {

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

        }catch (Exception e){
            log.info("an error has been occurred while upload image  ",e);
        }
    }



    public byte[] downloadFile(String key) {
        return s3Client.getObjectAsBytes(req -> req.bucket("alrawi-awards").key(key))
                .asByteArray();
    }

    public String downloadFileAsBase64(String imageKey) {
        byte[] imageBytes = s3Client.getObjectAsBytes(req -> req
                        .bucket("alrawi-awards")
                        .key(imageKey))
                .asByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);

    }


    public static String getExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }


    private File compressImagesToArchiveFile(List<MultipartFile> images) throws IOException {

        File tempZip = File.createTempFile("images-", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempZip)))) {

            byte[] buffer = new byte[16 * 1024];

            for (MultipartFile image : images) {

                String fileName = Objects.requireNonNullElse(image.getOriginalFilename(), UUID.randomUUID() + ".bin");
                ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);

                try (InputStream is = new BufferedInputStream(image.getInputStream())) {
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }

                zos.closeEntry();
            }
        }

        return tempZip;
    }



    public void uploadArchiveToS3(String key,List<MultipartFile> images) {

     try {

         File archiveStream=compressImagesToArchiveFile(images);

         PutObjectRequest request = PutObjectRequest.builder()
                 .bucket(bucketName)
                 .key(key)
                 .contentType("application/zip")
                 .contentLength(archiveStream.length())
                 .build();

         s3Client.putObject(request, RequestBody.fromFile(archiveStream));
         archiveStream.deleteOnExit();

         if (archiveStream.delete())
            log.info("Temporary ZIP file deleted successfully.");
          else
            log.info("Warning: Could not delete temporary ZIP file: {}" , archiveStream.getAbsolutePath());

         log.info("ZIP uploaded successfully to S3: {}" , key);
     }catch (Exception e){
         log.info("an error has been occurred while upload archive  ",e);
     }

    }

}