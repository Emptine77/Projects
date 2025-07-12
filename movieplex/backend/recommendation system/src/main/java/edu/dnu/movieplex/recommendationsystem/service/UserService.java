package edu.dnu.movieplex.recommendationsystem.service;

import edu.dnu.movieplex.recommendationsystem.domain.dto.UserRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.UserResponse;
import edu.dnu.movieplex.recommendationsystem.domain.mapper.UserMapper;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Movie;
import edu.dnu.movieplex.recommendationsystem.persistance.model.User;
import edu.dnu.movieplex.recommendationsystem.persistance.repository.MovieRepository;
import edu.dnu.movieplex.recommendationsystem.persistance.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final MovieRepository movieRepository;
  @ReadOnlyProperty
  public List<UserResponse> getAll(){
    List<User> users = userRepository.findAll();
    return users.stream().map(userMapper::entityToDto).toList();
  }
  @ReadOnlyProperty
  public UserResponse getUserByUserId(String userId){
    User user = userRepository.findUserByUserId(userId).orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    return userMapper.entityToDto(user);
  }
  public UserResponse addUser(UserRequest userRequest){
    // Проверяем, существует ли пользователь с таким userId
    if(userRepository.existsByName(userRequest.name())){
      throw new RuntimeException("Користувач з таким id вже існує");
    }
    User user = userRepository.save(userMapper.DtoToEntity(userRequest));
    return userMapper.entityToDto(user);
  }

  public UserResponse addRate(String userId, String movieTitle, Short rating) {
    Movie movie = movieRepository.findMovieByTitle(movieTitle)
        .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    User user = userRepository.findUserByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    user.rateMovie(movie, rating);
    userRepository.save(user);
    return userMapper.entityToDto(user);
  }
}
