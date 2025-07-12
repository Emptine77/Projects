package edu.dnu.movieplex.recommendationsystem.api.controller;
import edu.dnu.movieplex.recommendationsystem.domain.dto.UserRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.UserResponse;
import edu.dnu.movieplex.recommendationsystem.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<UserResponse> getAllUsers(){
    return userService.getAll();
  }
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse addUser(@RequestBody UserRequest userRequest){
    return userService.addUser(userRequest);
  }
  @PutMapping("/{userId}/{movieId}/{rating}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse addRate(@PathVariable String userId, @PathVariable String movieId, @PathVariable Short rating){
    return userService.addRate(userId,movieId, rating);
  }
  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserByUserId(@PathVariable String userId){
    return userService.getUserByUserId(userId);
  }
}
