package eg.alrawi.alrawi_award.service;


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

import java.io.IOException;


@Service
public class FileService {

    private final Logger log= LoggerFactory.getLogger(FileService.class);

    private final S3Client s3Client;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file,String key) throws IOException {

    try {
        s3Client.putObject(PutObjectRequest.builder()

                        .bucket("ets-media")
                        .key("Al_RAWI/"+key+getExtension(file))
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

    }catch (Exception e){
        log.info("error ",e);
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


}
