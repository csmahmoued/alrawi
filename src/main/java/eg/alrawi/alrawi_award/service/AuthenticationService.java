package eg.alrawi.alrawi_award.service;


import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.UserResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.entity.UserImage;
import eg.alrawi.alrawi_award.error.BusinessException;
import eg.alrawi.alrawi_award.mapper.UserMapper;
import eg.alrawi.alrawi_award.model.ImageType;
import eg.alrawi.alrawi_award.model.Role;
import eg.alrawi.alrawi_award.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
}
