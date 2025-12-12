package eg.alrawi.alrawi_award.service;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import eg.alrawi.alrawi_award.dto.ProjectContentDto;
import eg.alrawi.alrawi_award.dto.UploadProjectResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiCategory;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiProjectContent;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectContentService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PresignedUrlService presignedUrlService;
  //  private final S3TransferManager transferManager;
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

         AlrawiProject alrawiProject = new AlrawiProject();
         alrawiProject.setAlrawiUser(alrawiUser);
         alrawiProject.setAlrawiCategory(alrawiCategory);
         alrawiProject.setProjectTitle(projectContentDto.getProjectTitle());
         alrawiProject.setProjectDescription(projectContentDto.getProjectDescription());
         alrawiProject.setAlrawiProjectContent(getProjectContent(projectContentDto, alrawiProject));
         projectRepository.save(alrawiProject);
         String uploadUrl = uploadProjectContents(alrawiProject);
         uploadProjectResponseDto.setUploadUrl(uploadUrl);
         uploadProjectResponseDto.setProjectName(alrawiProject.getProjectTitle());


     }catch (BusinessException businessException){
         log.error("upload project failed (businessException) ",businessException);
         return ApiResponseDto.businessException(List.of(businessException.getMessage()));
     }
     catch (Exception e){
            log.error("upload project failed ",e);
            return ApiResponseDto.error(List.of("An error has been occurred "),"FAIL");
     }

     return ApiResponseDto.success(uploadProjectResponseDto,"Success");
    }





    private String uploadProjectContents(AlrawiProject alrawiProject) {

        String uploadProjectUrl="";
        if (alrawiProject.getAlrawiCategory().getCategoryName().equalsIgnoreCase("Photography")) {
            log.info("Uploading Photography Project");
        }else{
            String contentKey=  alrawiProject.getAlrawiProjectContent().getFirst().getContentKey();
            log.info("contentKey {} ",contentKey);
            uploadProjectUrl = presignedUrlService.generateUploadLink(contentKey);
            log.info("upload vido url : {} ",uploadProjectUrl);
        }

        return uploadProjectUrl;

    }

    private List<AlrawiProjectContent> getProjectContent(ProjectContentDto projectContentDto, AlrawiProject alrawiProject) {

        List<AlrawiProjectContent>  projectContents=new ArrayList<>();
        AlrawiProjectContent  alrawiProjectContent=new AlrawiProjectContent();
        String userId=(alrawiProject.getAlrawiUser().getNationalId()!=null?alrawiProject.getAlrawiUser().getNationalId():alrawiProject.getAlrawiUser().getPassportNumber());
        alrawiProjectContent.setContentKey(userId+"_"+projectContentDto.getCategoryId()+"_"+projectContentDto.getFile().getOriginalFilename());
        alrawiProjectContent.setAlrawiProject(alrawiProject);

        projectContents.add(alrawiProjectContent);
        return   projectContents;

    }

    private void testcases(){
        //user.setAlrawiUserImages(getUserImages(registerRequest));
        //URL url=  presignedUrlService.generateUploadUrl(123L,"test","txt");
        //  List<String> names=new ArrayList<>(List.of("test1s23"));
    //    List<String> urls=presignedUrlService.generateUploadLinks(names);
        //   log.info("url : {} ",urls);
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


*/
}
