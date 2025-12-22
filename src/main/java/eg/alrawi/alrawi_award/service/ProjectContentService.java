package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.ProjectContentDto;
import eg.alrawi.alrawi_award.dto.UploadProjectResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.error.BusinessException;
import eg.alrawi.alrawi_award.model.Constants;
import eg.alrawi.alrawi_award.repository.CategoryRepository;
import eg.alrawi.alrawi_award.repository.ProjectRepository;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.utils.DecodedToken;
import eg.alrawi.alrawi_award.utils.LocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectContentService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PresignedUrlService presignedUrlService;
    private final FileService fileService;
    private final HttpServletRequest request;
    private final LocalUtils localUtils;

    public ApiResponseDto<?> uploadProject(ProjectContentDto projectContentDto){

        UploadProjectResponseDto  uploadProjectResponseDto = new UploadProjectResponseDto();
     try {

         String userEmail= DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));

         AlrawiUser alrawiUser = userRepository.findByEmail(userEmail).orElse(null);

         if(alrawiUser == null)
             throw new BusinessException(localUtils.getMessage("USER_NOT_FOUND_MSG"));

         AlrawiCategory alrawiCategory = categoryRepository.findById(projectContentDto.getCategoryId()).orElse(null);

         if(alrawiCategory == null)
             throw new BusinessException(localUtils.getMessage("INVALID_CAT_MSG"));

         validateUserProject(alrawiUser,projectContentDto);

         AlrawiProject alrawiProject = getAlrawiProject(projectContentDto, alrawiUser, alrawiCategory);

         projectRepository.save(alrawiProject);

         String uploadUrl = uploadProjectContents(alrawiProject,projectContentDto);

         if (uploadUrl != null && !uploadUrl.isEmpty())
             uploadProjectResponseDto.setUploadUrl(uploadUrl);

         uploadProjectResponseDto.setProjectName(alrawiProject.getProjectTitle());


     }catch (BusinessException businessException){
         log.error("upload project failed (businessException) ",businessException);
         return ApiResponseDto.businessException(List.of(businessException.getMessage()));
     }
     catch (Exception e){
            log.error("upload project failed ",e);
            return ApiResponseDto.error(List.of("An error has been occurred "));
     }

         return ApiResponseDto.success(uploadProjectResponseDto, Constants.SUCCESS);
    }

    private void validateUserProject(AlrawiUser alrawiUser, ProjectContentDto projectContentDto) {

        if (alrawiUser.getProjects().size() > 3)
            throw new BusinessException(localUtils.getMessage("EXCEED_PROJECTS_MSG"));

        for (AlrawiProject alrawiProject : alrawiUser.getProjects()) {
            if (Objects.equals(projectContentDto.getCategoryId(), alrawiProject.getAlrawiCategory().getCategoryId())) {
                throw new BusinessException(localUtils.getMessage("CATEGORY_EXIST_ERROR_MSG"));
            }
        }

    }

    private static AlrawiProject getAlrawiProject(ProjectContentDto projectContentDto, AlrawiUser alrawiUser, AlrawiCategory alrawiCategory) {
        AlrawiProject alrawiProject = new AlrawiProject();
        alrawiProject.setAlrawiUser(alrawiUser);
        alrawiProject.setAlrawiCategory(alrawiCategory);
        alrawiProject.setProjectTitle(projectContentDto.getProjectTitle());
        alrawiProject.setProjectDescription(projectContentDto.getProjectDescription());
        alrawiProject.setProjectKey(buildKey(alrawiUser,alrawiCategory,projectContentDto));
        alrawiProject.setProjectStatus(Constants.PENDING_STATUS);
        return alrawiProject;
    }

    private static String buildKey(AlrawiUser alrawiUser, AlrawiCategory alrawiCategory,ProjectContentDto projectContentDto) {
        if(alrawiUser.getNationalId() !=null && !alrawiUser.getNationalId().isEmpty())
           return   alrawiUser.getNationalId()+"/projects/"+alrawiCategory.getCategoryName().replaceAll("\\s+","_")+"/"+projectContentDto.getProjectTitle().replaceAll("\\s+","_");
        else
           return    alrawiUser.getPassportNumber()+"/projects/"+alrawiCategory.getCategoryName().replaceAll("\\s+","_")+"/"+projectContentDto.getProjectTitle().replaceAll("\\s+","_");
    }

    private String uploadProjectContents(AlrawiProject alrawiProject,ProjectContentDto projectContentDto)  {

        String uploadProjectUrl="";

        switch (alrawiProject.getAlrawiCategory().getCategoryContentType()){
            case IMAGE:
                log.info("Uploading Photography");
                List<MultipartFile> projectImages=projectContentDto.getImgFile();
                if (projectImages.size()>10)
                    throw new BusinessException(localUtils.getMessage("UPLOAD_EXCEED_SIZE"));
                fileService.uploadArchiveToS3(alrawiProject.getProjectKey(),projectImages);
                break;
            case PDF:
               log.info("upload file {} ",projectContentDto.getScriptFile().getContentType());
               fileService.uploadFile(projectContentDto.getScriptFile(),alrawiProject.getProjectKey());
            break;
            case MP3:
                uploadProjectUrl = presignedUrlService.generateVideoUploadLink(alrawiProject.getProjectKey(),"audio/mpeg");
                log.info("upload video url : {} ",uploadProjectUrl);
                break;
            default:
                uploadProjectUrl = presignedUrlService.generateVideoUploadLink(alrawiProject.getProjectKey(),"video/mp4");
                log.info("upload mp3 url : {} ",uploadProjectUrl);
                break;
        }

        return uploadProjectUrl;
    }


}
