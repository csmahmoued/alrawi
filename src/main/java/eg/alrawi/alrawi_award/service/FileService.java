package eg.alrawi.alrawi_award.service;


import eg.alrawi.alrawi_award.utils.ProgressInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class FileService {

    private final Logger log= LoggerFactory.getLogger(FileService.class);
    private final static String bucketName="alrawi-awards";
    private final static String bucketPrefix="uploads/";
    private final S3Client s3Client;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file,String key)  {

    try {

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(bucketPrefix+key+"."+getExtension(file))
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        }catch (Exception e){
            log.info("an error has been occurred while upload image  ",e);
        }
    }

    public byte[] downloadFile(String key){
        ResponseBytes<GetObjectResponse> objectAclResponseResponseBytes=s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket("ets-media")
                .key(key)
                .build());
        return objectAclResponseResponseBytes.asByteArray();
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
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip))) {
            for (MultipartFile image : images) {
                ZipEntry entry = new ZipEntry(Objects.requireNonNull(image.getOriginalFilename()));
                zos.putNextEntry(entry);

                try (InputStream is = new ProgressInputStream(image.getInputStream(), image.getOriginalFilename())) {
                    byte[] buffer = new byte[16 * 1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
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
                 .key(bucketPrefix+key)
                 .contentType("application/zip")
                 .contentLength(archiveStream.length())
                 .build();

         s3Client.putObject(request, RequestBody.fromFile(archiveStream));

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