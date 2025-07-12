package edu.dnu.movieplex.user.domain.mapper;

import edu.dnu.movieplex.user.domain.dto.UserDetailsResponse;
import edu.dnu.movieplex.user.domain.dto.UserRegistrationRequest;
import edu.dnu.movieplex.user.domain.dto.UserResponse;
import edu.dnu.movieplex.user.persistance.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for mapping between User entities and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "status", constant = "INACTIVE")
    @Mapping(target = "firstname", source = "firstName")
    @Mapping(target = "lastname", source = "lastName")
    User toUser(UserRegistrationRequest userRegistrationRequest);

    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    UserResponse toResponse(User user);

    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    UserDetailsResponse toUserDetailsResponse(User user);
}
