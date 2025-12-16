package eg.alrawi.alrawi_award.service;


import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.UpdateUserDto;
import eg.alrawi.alrawi_award.dto.UserResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.entity.UserImage;
import eg.alrawi.alrawi_award.error.BusinessException;
import eg.alrawi.alrawi_award.mapper.UserMapper;
import eg.alrawi.alrawi_award.model.ImageType;
import eg.alrawi.alrawi_award.model.Role;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.utils.DecodedToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import static eg.alrawi.alrawi_award.utils.NationalUtils.extractBirthDateFormatted;
import static eg.alrawi.alrawi_award.utils.NationalUtils.extractGender;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private  final UserRepository userRepository;
    private final FileService fileService;
    private final UserMapper userMapper;
    private final HttpServletRequest request;


    public ApiResponseDto<?> registerUser(RegisterDto registerRequest) {

        UserResponseDto userResponseDto=null;
     try {
         AlrawiUser user = userMapper.mapUser(registerRequest);
         user.setUsername(registerRequest.getEmail());
         user.setRole(Role.ROLE_USER);

         if (registerRequest.getNationalId() != null) {
             boolean isNationalIdExist = checkIfNationalIsExist(registerRequest.getNationalId());
             if (isNationalIdExist)
                    throw new BusinessException("National ID already exist");
         }else if (registerRequest.getPassportNumber()!=null) {
             boolean isPassPortExist=checkIfPassportIsExist(registerRequest.getPassportNumber());
             if (isPassPortExist)
                throw new BusinessException("Passport number is already exist");
         }
         boolean isMailExists=checkIfEmailIsExist(registerRequest.getEmail());
         if (isMailExists)
             throw new BusinessException("Email is already exist");

         if(registerRequest.getNationalId() !=null)
           user.setGender(extractGender(registerRequest.getNationalId()));

         user.setAlrawiUserImages(getUserImages(registerRequest, user));
         user.setUsername(registerRequest.getEmail());
         user.setDateOfBirth(extractBirthDateFormatted(registerRequest.getNationalId()));

             userRepository.save(user);
             uploadUserImages(registerRequest);
              userResponseDto=userMapper.mapUserDto(user);


     }catch (BusinessException businessException){
         log.error("upload project failed (businessException) ",businessException);
         return ApiResponseDto.businessException(List.of(businessException.getMessage()));
     }
     catch (Exception e){
         log.error("Error while registering user {}",registerRequest.getEmail(),e);
     }

        return  ApiResponseDto.success(userResponseDto,"SUCCESS");

    }



    public ApiResponseDto<?> getUserProfile() {
        try {
            String userEmail = DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));
            AlrawiUser user = userRepository.findByEmail(userEmail).orElse(null);
            if (user == null)
                throw new UsernameNotFoundException("Username Not Found");

            UserResponseDto userResponseDto =userMapper.mapUserDto(user);


            return  ApiResponseDto.success(userResponseDto,"SUCCESS");

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


        if (updateUserDto.getEmail() != null)
            user.setEmail(updateUserDto.getEmail());

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
        UserResponseDto userResponseDto =userMapper.mapUserDto(user)  ;
        return  ApiResponseDto.success(userResponseDto,"SUCCESS");
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


    private void uploadUserImages(RegisterDto registerRequest)  {
        if (registerRequest.getNationalId()!=null) {
            log.info("upload National Id : {}", registerRequest.getNationalId());
            fileService.uploadFile(registerRequest.getFullName()+"_"+registerRequest.getMobileNumber(),registerRequest.getNationalImgFront(), registerRequest.getNationalId()+"_"+ImageType.FRONT_ID);
            fileService.uploadFile(registerRequest.getFullName()+"_"+registerRequest.getMobileNumber(),registerRequest.getNationalImgBack(), registerRequest.getNationalId()+"_"+ImageType.BACK_ID);
        }else {
            log.info("upload passport Id : {}", registerRequest.getPassportNumber());
            fileService.uploadFile(registerRequest.getFullName()+"_"+registerRequest.getMobileNumber(),registerRequest.getNationalImgBack(), registerRequest.getPassportNumber()+"_"+ImageType.PASSPORT);
        }
    }

    private void uploadUserPersonalImage(AlrawiUser user, MultipartFile file) {
        log.info("start upload user personal image ");
        fileService.uploadFile(buildUserPrefix(user),file,user.getNationalId()+"_"+ImageType.FRONT_ID);
    }

    private String buildUserPrefix(AlrawiUser user) {
        return user.getFullName()+"_"+user.getMobileNumber();
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
}
