package edu.dnu.movieplex.recommendationsystem.api.controller;

import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreResponse;
import edu.dnu.movieplex.recommendationsystem.service.GenreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/genre")
@RequiredArgsConstructor
public class GenreController {
  private final GenreService genreService;
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public GenreResponse addGenre(@RequestBody GenreRequest genreRequest){
    return genreService.addGenre(genreRequest);
  }
  @PutMapping("/{genreTitle}")
  @ResponseStatus(HttpStatus.OK)
  public GenreResponse updateGenre(@RequestBody GenreRequest genreRequest, @PathVariable String genreTitle){
    return genreService.updateGenre(genreRequest, genreTitle);
  }
  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<GenreResponse> getAllGenres(){
    return genreService.getAllGenres();
  }
  @DeleteMapping("/{genreTitle}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteGenre(@PathVariable String genreTitle){
     genreService.deleteGenre(genreTitle);
  }
}
