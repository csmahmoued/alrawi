package eg.alrawi.alrawi_award.service;
import eg.alrawi.alrawi_award.dto.*;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.entity.UserImage;
import eg.alrawi.alrawi_award.error.BusinessException;
import eg.alrawi.alrawi_award.mapper.UserMapper;
import eg.alrawi.alrawi_award.model.Constants;
import eg.alrawi.alrawi_award.model.ImageType;
import eg.alrawi.alrawi_award.model.Role;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.utils.DecodedToken;
import eg.alrawi.alrawi_award.utils.LocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

import java.util.List;

import static eg.alrawi.alrawi_award.utils.NationalUtils.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private  final UserRepository userRepository;
    private final FileService fileService;
    private final UserMapper userMapper;
    private final HttpServletRequest request;
    private final LocalUtils localUtils;


    public ApiResponseDto<?> registerUser(RegisterDto registerRequest) {

        UserResponseDto userResponseDto=null;
     try {
         log.info("Start register user {} ",registerRequest.getEmail());

         AlrawiUser user = userMapper.mapUser(registerRequest);

         user.setUsername(registerRequest.getEmail());
         user.setRole(Role.ROLE_USER);

         if (registerRequest.getNationalId() != null) {
             boolean isNationalIdExist = checkIfNationalIsExist(registerRequest.getNationalId());
             if (isNationalIdExist)
                    throw new BusinessException(localUtils.getMessage("NATIONAL_ID_ERROR_MSG"));

             user.setGender(extractGender(registerRequest.getNationalId()));
             user.setDateOfBirth(extractBirthDateFormatted(registerRequest.getNationalId()));

         }else if (registerRequest.getPassportNumber()!=null) {
             boolean isPassPortExist=checkIfPassportIsExist(registerRequest.getPassportNumber());
             if (isPassPortExist)
                throw new BusinessException(localUtils.getMessage("PASSPORT_NUMBER_ERROR_MSG"));

             user.setPassportNumber(registerRequest.getPassportNumber());
         }
         boolean isMailExists=checkIfEmailIsExist(registerRequest.getEmail());
         if (isMailExists)
             throw new BusinessException(localUtils.getMessage("MAIL_EXISTS_ERROR_MSG"));

         if(registerRequest.getDateOfBirth()!=null)
             user.setDateOfBirth(registerRequest.getDateOfBirth());

         user.setAlrawiUserImages(getUserImages(registerRequest, user));

         userRepository.save(user);
         uploadUserPersonalImage(user.getAlrawiUserImages(),registerRequest);

         userResponseDto=userMapper.mapUserDto(user);

         log.info("End register user {} ",registerRequest.getEmail());


     }catch (BusinessException businessException){
         log.error("upload project failed (businessException) ",businessException);
         return ApiResponseDto.businessException(List.of(businessException.getMessage()));
     }
     catch (Exception e){
         log.error("Error while registering user {}",registerRequest.getEmail(),e);
     }

        return  ApiResponseDto.success(userResponseDto, Constants.SUCCESS);

    }



    public ApiResponseDto<?> getUserProfile() {
        try {
            String userEmail = DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));
            AlrawiUser user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null)
                throw new UsernameNotFoundException("Username Not Found");

            UserResponseDto userResponseDto =userMapper.mapUserDto(user);

            return  ApiResponseDto.success(userResponseDto,Constants.SUCCESS);

        }catch (BusinessException businessException){
            log.error("get user profile failed (businessException) ",businessException);
            return ApiResponseDto.businessException(List.of(businessException.getMessage()));
        }catch (Exception e){
            log.error("get user profile failed (e) ",e);
            return ApiResponseDto.error(List.of("An error occurred while processing the request"));
        }
    }


    public ApiResponseDto<?> updateUser(UpdateUserDto updateUserDto) {

    try {
        UserResponseDto userResponseDto=new UserResponseDto();
        String userEmail = DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));
        AlrawiUser user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null)
            throw new BusinessException("Username Not Found");

        if (updateUserDto.getCity() != null)
            user.setCity(updateUserDto.getCity());

        if (updateUserDto.getGovernment() != null)
            user.setGovernment(updateUserDto.getGovernment());

        if (updateUserDto.getFullName() != null)
            user.setFullName(updateUserDto.getFullName());

        if (updateUserDto.getMobileNumber() != null)
            user.setMobileNumber(updateUserDto.getMobileNumber());


        UserImage userImage=user.getAlrawiUserImages().stream()
                    .filter(type->type.getImageType().name().equalsIgnoreCase(ImageType.PERSONAL.name()))
                    .findFirst().orElse(null);

            if (userImage!=null) {
                uploadUserPersonalImage(user, updateUserDto.getProfilePicture());
            }
            else {
                userImage = new UserImage(user.getNationalId() + "_" + ImageType.PERSONAL, ImageType.FRONT_ID);
                userImage.setAlrawiUser(user);
                uploadUserPersonalImage(user, updateUserDto.getProfilePicture());
            }
            user=userRepository.save(user);
       UserImage userImages = user.getAlrawiUserImages().stream().filter(img->img.getImageType().equals(ImageType.PERSONAL)).findFirst().orElse(null);
       if (userImages != null) {
           String imageBytes= fileService.downloadFileAsBase64(userImages.getImageKey());
           userResponseDto.setPersonalImg(imageBytes);
       }

        userResponseDto =userMapper.mapUserDto(user)  ;

       return  ApiResponseDto.success(userResponseDto,Constants.SUCCESS);

    }catch (BusinessException businessException){
       log.error("update user failed (businessException) ",businessException);
        return ApiResponseDto.businessException(List.of(businessException.getMessage()));

    }catch (Exception e){
        log.error("Error while updating user {}",updateUserDto.getEmail(),e);
        return ApiResponseDto.businessException(List.of(e.getMessage()));

    }
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

    private String buildUserKey(RegisterDto registerRequest,ImageType  imageType) {
        return switch (imageType) {
            case FRONT_ID ->
                    registerRequest.getNationalId() + "/" + imageType.name() + "." + getExtension(registerRequest.getNationalImgFront());
            case BACK_ID ->
                    registerRequest.getNationalId() + "/" + imageType.name() + "." + getExtension(registerRequest.getNationalImgBack());
            case PASSPORT ->
                    registerRequest.getPassportNumber() + "/" + imageType.name() + "." + getExtension(registerRequest.getPassportImg());
            default -> "";
        };
    }

    public  String getExtension(MultipartFile file) {
        return fileService.getExtension(file);
    }

    private void uploadUserPersonalImage(List<UserImage> userImageList,RegisterDto registerRequest) {

        if (userImageList==null || userImageList.isEmpty())
            return;

        userImageList.forEach(userImage -> {
            switch (userImage.getImageType()) {
                case BACK_ID ->{
                    log.info("upload national image back ");
                    fileService.uploadFile(registerRequest.getNationalImgBack(),userImage.getImageKey());
                    log.info("national image back uploaded successfully");
                }
                case FRONT_ID -> {
                    log.info("upload national image front ");
                    fileService.uploadFile(registerRequest.getNationalImgFront(),userImage.getImageKey());
                    log.info("national image front uploaded successfully");
                }

                case PASSPORT ->{
                    log.info("upload  image passport ");
                    fileService.uploadFile(registerRequest.getPassportImg(),userImage.getImageKey());
                    log.info("image passport uploaded successfully");
                }
            }

        });

    }


    private void uploadUserPersonalImage(AlrawiUser user, MultipartFile file) {
        log.info("start upload user personal image ");
        if (file != null )
             fileService.uploadFile(buildUserPrefix(user),file,user.getNationalId()+"_"+ImageType.FRONT_ID);
    }

    private String buildUserPrefix(AlrawiUser user) {
        return user.getFullName()+"_"+user.getMobileNumber();
    }
    private List<UserImage> getUserImages(RegisterDto registerRequest, AlrawiUser user) {

        List<UserImage>  userImages = new ArrayList<>();

        if (registerRequest.getNationalId()!=null) {
            UserImage userImageNationalFront = new UserImage(buildUserKey(registerRequest,ImageType.FRONT_ID), ImageType.FRONT_ID);
            UserImage userImageNationalBack = new UserImage(buildUserKey(registerRequest,ImageType.BACK_ID),ImageType.BACK_ID);
            userImageNationalFront.setAlrawiUser(user);
            userImageNationalBack.setAlrawiUser(user);
            userImages.add(userImageNationalBack);
            userImages.add(userImageNationalFront);
        }
        if (registerRequest.getPassportImg() != null) {
            UserImage userImagePassPort = new UserImage(buildUserKey(registerRequest,ImageType.PASSPORT), ImageType.PASSPORT);
            userImagePassPort.setAlrawiUser(user);
            userImages.add(userImagePassPort);
        }

        return userImages;


    }
}
