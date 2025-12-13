package eg.alrawi.alrawi_award.mapper;


import eg.alrawi.alrawi_award.dto.ProjectDto;
import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.UserResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiProject;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import org.mapstruct.*;

import java.util.List;


@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    AlrawiUser mapUser(RegisterDto registerDto);

    UserResponseDto mapUserDto(AlrawiUser alrawiUser);

    @Mapping(source = "alrawiCategory.categoryName", target = "categoryName")
    ProjectDto mapProject(AlrawiProject alrawiProject);

    List<ProjectDto> mapProject(List<AlrawiProject>  alrawiProjects);

}
