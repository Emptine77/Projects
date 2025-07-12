package edu.dnu.movieplex.recommendationsystem.domain.mapper;


import edu.dnu.movieplex.recommendationsystem.domain.dto.UserRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.UserResponse;
import edu.dnu.movieplex.recommendationsystem.persistance.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse entityToDto(User user);
  User DtoToEntity(UserRequest userRequest);
}
