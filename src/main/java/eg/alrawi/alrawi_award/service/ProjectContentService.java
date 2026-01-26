package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.ProjectContentDto;
import eg.alrawi.alrawi_award.dto.UploadProjectResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiProjectContent;
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

import java.io.IOException;
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
    private final LocalUtils localUtils;

    public ApiResponseDto<?> uploadProject(ProjectContentDto projectContentDto){

        UploadProjectResponseDto  uploadProjectResponseDto = new UploadProjectResponseDto();
     try {
         log.info("start uploadProject {} ",projectContentDto.getProjectTitle());
         String userEmail= DecodedToken.getEmailFromToken(request.getHeader("Authorization").substring(7));
         log.info("user email is {}",userEmail);

         AlrawiUser alrawiUser = userRepository.findByEmail(userEmail).orElse(null);

         if(alrawiUser == null)
             throw new BusinessException(localUtils.getMessage("USER_NOT_FOUND_MSG"));

         AlrawiCategory alrawiCategory = categoryRepository.findById(projectContentDto.getCategoryId()).orElse(null);

         if(alrawiCategory == null)
             throw new BusinessException(localUtils.getMessage("INVALID_CAT_MSG"));

         validateUserProject(alrawiUser,projectContentDto);

         AlrawiProject alrawiProject = getAlrawiProject(projectContentDto, alrawiUser, alrawiCategory);

         List<String> projectContentKeys=getProjectContentKey(projectContentDto, alrawiUser, alrawiCategory);
         log.info("projectContentKeys {} ",projectContentKeys);

         alrawiProject.setAlrawiProjectContent(getProjectContents(projectContentKeys,alrawiProject));

         projectRepository.save(alrawiProject);

         String uploadUrl = uploadProjectContents(alrawiProject,projectContentDto,projectContentKeys);

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

         log.info("End upload project {} ",projectContentDto.getProjectTitle());
         return ApiResponseDto.success(uploadProjectResponseDto, Constants.SUCCESS);
    }

    private List<AlrawiProjectContent> getProjectContents(List<String> projectContentKeys, AlrawiProject alrawiProject) {

        List<AlrawiProjectContent>  alrawiProjectContents = new ArrayList<>();
        projectContentKeys.forEach(projectContentKey -> {
            AlrawiProjectContent  alrawiProjectContent = new AlrawiProjectContent();
            alrawiProjectContent.setAlrawiProject(alrawiProject);
            alrawiProjectContent.setContentKey(projectContentKey);
            alrawiProjectContents.add(alrawiProjectContent);
        });

        return alrawiProjectContents;

    }

    private List<String> getProjectContentKey(ProjectContentDto projectContentDto, AlrawiUser alrawiUser, AlrawiCategory alrawiCategory) {

        List<String> projectContentKeys = new ArrayList<>();
        switch (alrawiCategory.getCategoryContentType()){

            case IMAGE :
                if (projectContentDto.getImgFile() !=null && !projectContentDto.getImgFile().isEmpty()){
                    String generatedKey=buildKey(alrawiUser,alrawiCategory);
                    for (int index=0;index<projectContentDto.getImgFile().size();index++)
                           projectContentKeys.add(generatedKey+"/"+index+"."+fileService.getExtension(projectContentDto.getImgFile().get(index)));
                }
             break;

            case VIDEO:
                projectContentKeys.add(buildKey(alrawiUser,alrawiCategory)+"/"+System.currentTimeMillis()+".mp4");
                break;

            case  PDF:
                projectContentKeys.add(buildKey(alrawiUser,alrawiCategory)+"/"+System.currentTimeMillis()+".pdf");
                break;

            case  MP3:
                projectContentKeys.add(buildKey(alrawiUser,alrawiCategory)+"/"+System.currentTimeMillis()+".mp3");
                break;

        }
        return  projectContentKeys;
    }

    private void validateUserProject(AlrawiUser alrawiUser, ProjectContentDto projectContentDto) {

        log.info("start validateUserProject {} ",projectContentDto.getProjectTitle());
        log.info("validateUserProject size {} ",alrawiUser.getProjects().size());
        if (alrawiUser.getProjects().size() > 2)
            throw new BusinessException(localUtils.getMessage("EXCEED_PROJECTS_MSG"));

        for (AlrawiProject alrawiProject : alrawiUser.getProjects()) {
            if (Objects.equals(projectContentDto.getCategoryId(), alrawiProject.getAlrawiCategory().getCategoryId())) {
                throw new BusinessException(localUtils.getMessage("CATEGORY_EXIST_ERROR_MSG"));
            }
        }

        log.info("end validateUserProject {} ",projectContentDto.getProjectTitle());

    }

    private static AlrawiProject getAlrawiProject(ProjectContentDto projectContentDto, AlrawiUser alrawiUser, AlrawiCategory alrawiCategory) {
        AlrawiProject alrawiProject = new AlrawiProject();
        alrawiProject.setAlrawiUser(alrawiUser);
        alrawiProject.setAlrawiCategory(alrawiCategory);
        alrawiProject.setProjectTitle(projectContentDto.getProjectTitle());
        alrawiProject.setProjectDescription(projectContentDto.getProjectDescription());
        alrawiProject.setProjectStatus(Constants.PENDING_STATUS);
        return alrawiProject;
    }

    private static String buildKey(AlrawiUser alrawiUser, AlrawiCategory alrawiCategory) {
        if(alrawiUser.getNationalId() !=null && !alrawiUser.getNationalId().isEmpty())
           return   alrawiUser.getNationalId()+"/projects/"+alrawiCategory.getCategoryName().replaceAll("\\s+","_");
        else
           return    alrawiUser.getPassportNumber()+"/projects/"+alrawiCategory.getCategoryName().replaceAll("\\s+","_");
    }


    private String uploadProjectContents(AlrawiProject alrawiProject,ProjectContentDto projectContentDto,List<String> projectContentKeys) throws IOException {

        String uploadProjectUrl="";

        switch (alrawiProject.getAlrawiCategory().getCategoryContentType()){
            case IMAGE:
                log.info("Uploading Photography");
                List<MultipartFile> projectImages=projectContentDto.getImgFile();
                List<String> files= fileService.uploadMultipleFilesAsync(projectImages,projectContentKeys);
                log.info("Uploading files files {} ",files);
                break;
            case PDF:
               log.info("upload file PDF {} ",projectContentDto.getScriptFile().getContentType());
               fileService.uploadFile(projectContentDto.getScriptFile(),projectContentKeys.getFirst());
            break;
            case MP3:
                uploadProjectUrl = presignedUrlService.generateVideoUploadLink(projectContentKeys.getFirst(),"audio/mpeg");
                log.info("upload video url : {} ",uploadProjectUrl);
                break;
            default:
                uploadProjectUrl = presignedUrlService.generateVideoUploadLink(projectContentKeys.getFirst(),"video/mp4");
                log.info("upload mp3 url : {} ",uploadProjectUrl);
                break;
        }

        return uploadProjectUrl;
    }


}
