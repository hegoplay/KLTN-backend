package iuh.fit.se.services.user_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.dto.RegisterRequestDto;
import iuh.fit.se.services.user_service.dto.UserInfoResponseDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import iuh.fit.se.services.user_service.dto.UserUpdateInfoRequestDto;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
	public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	public abstract User toUserEntity(RegisterRequestDto requestDto);
	public abstract UserInfoResponseDto toUserInfoResponseDto(User user);
	
	public abstract UserShortInfoResponseDto toShortUserInfoResponseDto(User user);
	
	@AfterMapping
    protected void afterToShortUserInfoResponseDto(User user, @MappingTarget UserShortInfoResponseDto.UserShortInfoResponseDtoBuilder dto)
    {
		String userUrl = "/api/users/" + user.getId();
		dto.userUrl(userUrl);
    }
	
	public abstract void mapToUser(UserUpdateInfoRequestDto dto, @MappingTarget User user);
}
