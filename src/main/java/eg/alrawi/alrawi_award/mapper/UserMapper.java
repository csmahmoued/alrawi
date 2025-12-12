package eg.alrawi.alrawi_award.mapper;


import eg.alrawi.alrawi_award.dto.RegisterDto;
import eg.alrawi.alrawi_award.dto.UserResponseDto;
import eg.alrawi.alrawi_award.entity.AlrawiUser;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    AlrawiUser mapUser(RegisterDto registerDto);
    UserResponseDto mapUserDto(AlrawiUser alrawiUser);
}
