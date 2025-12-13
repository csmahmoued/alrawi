package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.FileDto;
import eg.alrawi.alrawi_award.dto.ProjectContentDto;
import eg.alrawi.alrawi_award.dto.UploadProjectResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import eg.alrawi.alrawi_award.error.BusinessException;
import eg.alrawi.alrawi_award.repository.CategoryRepository;
import eg.alrawi.alrawi_award.repository.ProjectRepository;
import eg.alrawi.alrawi_award.repository.UserRepository;
import eg.alrawi.alrawi_award.utils.DecodedToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
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

    public ApiResponseDto<?> uploadProject(ProjectContentDto projectContentDto){

        UploadProjectResponseDto  uploadProjectResponseDto = new UploadProjectResponseDto();
     try {

         String userEmail= DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));

         AlrawiUser alrawiUser = userRepository.findByEmail(userEmail).orElse(null);

         if(alrawiUser == null)
             throw new BusinessException("User not found");

         AlrawiCategory alrawiCategory = categoryRepository.findById(projectContentDto.getCategoryId()).orElse(null);

         if(alrawiCategory == null)
             throw new BusinessException("invalid category ");

         validateUserProject(alrawiUser,projectContentDto);


         AlrawiProject alrawiProject = getAlrawiProject(projectContentDto, alrawiUser, alrawiCategory);

         projectRepository.save(alrawiProject);

         uploadProjectContents(alrawiProject,projectContentDto);

         String uploadUrl = uploadProjectContents(alrawiProject,projectContentDto);
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

         return ApiResponseDto.success(uploadProjectResponseDto,"SUCCESS");
    }

    private void validateUserProject(AlrawiUser alrawiUser, ProjectContentDto projectContentDto) {

        if (alrawiUser.getProjects().size() >3)
            throw new BusinessException("Project size must be greater than 3");

        for (AlrawiProject alrawiProject : alrawiUser.getProjects()) {
            if (Objects.equals(projectContentDto.getCategoryId(), alrawiProject.getAlrawiCategory().getCategoryId())) {
                throw new BusinessException("Project category already exists ");
            }
        }

    }

    private static AlrawiProject getAlrawiProject(ProjectContentDto projectContentDto, AlrawiUser alrawiUser, AlrawiCategory alrawiCategory) {
        AlrawiProject alrawiProject = new AlrawiProject();
        alrawiProject.setAlrawiUser(alrawiUser);
        alrawiProject.setAlrawiCategory(alrawiCategory);
        alrawiProject.setProjectTitle(projectContentDto.getProjectTitle());
        alrawiProject.setProjectDescription(projectContentDto.getProjectDescription());
        alrawiProject.setProjectKey(alrawiUser.getNationalId()+"_"+ alrawiCategory.getCategoryId()+"_"+ alrawiCategory.getCategoryName());
        alrawiProject.setProjectStatus("PENDING");
        return alrawiProject;
    }


    private String uploadProjectContents(AlrawiProject alrawiProject,ProjectContentDto projectContentDto)  {

        String uploadProjectUrl="";
        if (alrawiProject.getAlrawiCategory().getCategoryName().equalsIgnoreCase("Photography")) {
            log.info("Uploading Photography Project Number of Photos {} ",projectContentDto.getUploads().size());
            List<MultipartFile> projectImages=new  ArrayList<>();
            for (FileDto fileDto : projectContentDto.getUploads()) {
                projectImages.add(fileDto.getFile());
            }
              fileService.uploadArchiveToS3(alrawiProject.getProjectKey(),projectImages);
        }else if (alrawiProject.getAlrawiCategory().getCategoryName().equalsIgnoreCase("Scriptwriting")) {
             log.info("upload file {} ",projectContentDto.getFile().getContentType());
             fileService.uploadFile(projectContentDto.getFile(),alrawiProject.getProjectKey());
        }
        else{
            uploadProjectUrl = presignedUrlService.generateVideoUploadLink(alrawiProject.getProjectKey());
            log.info("upload video url : {} ",uploadProjectUrl);
        }

        return uploadProjectUrl;

    }

}
