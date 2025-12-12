package eg.alrawi.alrawi_award.service;


import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.UserResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.entity.UserImage;
import eg.alrawi.alrawi_award.mapper.UserMapper;
import eg.alrawi.alrawi_award.model.ImageType;
import eg.alrawi.alrawi_award.model.Role;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static eg.alrawi.alrawi_award.utils.NationalUtils.extractBirthDateFormatted;
import static eg.alrawi.alrawi_award.utils.NationalUtils.extractGender;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final S3AsyncClient s3AsyncClient;
    private final PasswordEncoder passwordEncoder;
    private  final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PresignedUrlService presignedUrlService;
    private final FileService fileService;


    private final UserMapper userMapper;
  //  private final  S3TransferManager transferManager;


    public ApiResponseDto<?> registerUser(RegisterDto registerRequest) {

        List<String> errors = new ArrayList<>();
     try {
         AlrawiUser user = userMapper.mapUser(registerRequest);

         if (registerRequest.getNationalId() != null) {
             boolean isNationalIdExist = checkIfNationalIsExist(registerRequest.getEmail());
             if (isNationalIdExist)
                     errors.add("National ID is already exist");
         }else if (registerRequest.getPassportNumber()!=null) {
             boolean isPassPortExist=checkIfPassportIsExist(registerRequest.getPassportNumber());
             if (isPassPortExist)
                 errors.add("Passport number is already exist");
         }
         boolean isMailExists=checkIfEmailIsExist(registerRequest.getEmail());
         if (isMailExists)
                errors.add("Email is already exist");

         user.setRole(Role.ROLE_USER);
         if(registerRequest.getNationalId() !=null)
           user.setGender(extractGender(registerRequest.getNationalId()));
         user.setAlrawiUserImages(getUserImages(registerRequest, user));
         user.setUsername(registerRequest.getEmail());
         user.setDateOfBirth(extractBirthDateFormatted(registerRequest.getNationalId()));
         if (errors.isEmpty()) {
             userRepository.save(user);
             uploadUserImages(registerRequest);
             UserResponseDto userResponseDto=userMapper.mapUserDto(user);
             return  ApiResponseDto.success(userResponseDto,"SUCCESS");
         }


     }catch (Exception e){
         log.error("Error while registering user {}",registerRequest.getEmail(),e);
     }

        return ApiResponseDto.error(errors,"FAIL");

    }

    public boolean checkIfEmailIsExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkIfNationalIsExist(String nationalId) {
        return userRepository.findByNationalId(nationalId).isPresent();
    }

    public boolean checkIfPassportIsExist(String nationalId) {
        return userRepository.findByPassportNumber(nationalId).isPresent();
    }


    private void uploadUserImages(RegisterDto registerRequest) throws IOException {
        if (registerRequest.getNationalId()!=null) {
            log.info("upload National Id : {}", registerRequest.getNationalId());
            fileService.uploadFile(registerRequest.getNationalImgFront(), registerRequest.getNationalId()+"_"+ImageType.FRONT_ID);
            fileService.uploadFile(registerRequest.getNationalImgBack(), registerRequest.getNationalId()+"_"+ImageType.BACK_ID);
        }else {
            log.info("upload passport Id : {}", registerRequest.getPassportNumber());
            fileService.uploadFile(registerRequest.getNationalImgBack(), registerRequest.getPassportNumber()+"_"+ImageType.PASSPORT);
        }
    }




    private List<UserImage> getUserImages(RegisterDto registerRequest, AlrawiUser user) {

        List<UserImage>  userImages = new ArrayList<>();
        if (registerRequest.getNationalImgFront() != null) {
            UserImage userImageNationalFront = new UserImage(registerRequest.getNationalId()+"_"+ ImageType.FRONT_ID, ImageType.FRONT_ID);
            userImageNationalFront.setAlrawiUser(user);
            userImages.add(userImageNationalFront);
        }

        if (registerRequest.getNationalImgBack() != null) {
            UserImage userImageNationalBack = new UserImage(registerRequest.getNationalId()+"_"+ImageType.BACK_ID, ImageType.BACK_ID);
            userImageNationalBack.setAlrawiUser(user);
            userImages.add(userImageNationalBack);
        }
        if (registerRequest.getPassportImg() != null) {
            UserImage userImagePassPort = new UserImage(registerRequest.getPassportNumber() + "_" + ImageType.PASSPORT, ImageType.PASSPORT);
            userImagePassPort.setAlrawiUser(user);
            userImages.add(userImagePassPort);
        }

        return userImages;


    }

    public static String getUsername(String email) {
        return email.substring(0, email.indexOf('@'));
    }




    /*

    public void uploadNationalImg(RegisterDto registerRequest){





        // Use it with TransferManager
     //   S3TransferManager transferManager = S3TransferManager.builder()
        //        .s3Client(s3AsyncClient)
           //     .build();

        // Files to upload
        List<Path> files = List.of(
                Path.of("C:/uploads/test.png")
        );

        // Upload concurrently
        List<CompletableFuture<CompletedFileUpload>> uploads = files.stream()
                .map(file -> {
                    UploadFileRequest uploadRequest = UploadFileRequest.builder()
                            .putObjectRequest(PutObjectRequest.builder()
                                    .bucket("ets-media")
                                    .key("Al_RAWI/" + file.getFileName().toString())
                                    .build())
                            .source(file)
                            .addTransferListener(LoggingTransferListener.create())
                            .build();

                    FileUpload upload = transferManager.uploadFile(uploadRequest);
                    return upload.completionFuture();
                })
                .toList();

        // Wait for all uploads
        uploads.forEach(CompletableFuture::join);

        transferManager.close();
        System.out.println(" All files uploaded successfully!");

    }



    public LoginResponse signin(SigningRequest signinRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(),signinRequest.getPassword()));
       AlrawiUser alrawiUser= userRepository.findByUsername(signinRequest.getUsername()).orElseThrow(()->new IllegalArgumentException("Invalid Username or Password"));
       String token = jwtService.generateToken(alrawiUser);
       String refreshToken = jwtService.generateRefreshToken(new HashMap<>(),alrawiUser);

        LoginResponse loginResponse=new LoginResponse();
        loginResponse.setToke(token);


        return loginResponse;


    }

*/



}
