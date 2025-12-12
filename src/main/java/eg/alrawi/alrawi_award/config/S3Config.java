package eg.alrawi.alrawi_award.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .credentialsProvider(awsCredentialsProvider())
                .region(Region.of(AWSConstants.AWS_REGION))
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.crtBuilder()
                .region(Region.US_EAST_2)
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(AWSConstants.AWS_REGION))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }
    private AwsCredentialsProvider awsCredentialsProvider() {
        AwsBasicCredentials awsCreeds = AwsBasicCredentials.create(AWSConstants.AWS_ACCESS_KEY_ID, AWSConstants.AWS_SECRET_ACCESS_KEY);
        return () -> awsCreeds;
    }



}
