package edu.dnu.movieplex.recommendationsystem.service;

import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreRequest;
import edu.dnu.movieplex.recommendationsystem.domain.dto.GenreResponse;
import edu.dnu.movieplex.recommendationsystem.domain.mapper.GenreMapper;
import edu.dnu.movieplex.recommendationsystem.persistance.model.Genre;
import edu.dnu.movieplex.recommendationsystem.persistance.repository.GenreRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class GenreService {
  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;
  public GenreResponse addGenre(GenreRequest genreRequest) {
    if(genreRepository.existsByTitle(genreRequest.title())){
      throw new RuntimeException("Жанр з такою назвою вже існує");
    }
    Genre genre = genreRepository.save(genreMapper.DtoToEntity(genreRequest));
    return genreMapper.entityToDto(genre);
  }
  public GenreResponse updateGenre(GenreRequest genreRequest, String genreTitle) {
    Genre genre = genreRepository.findGenreByTitle(genreTitle)
        .orElseThrow(()-> new RuntimeException("Жанр з назвою " + genreTitle + " не знайден"));
    genreMapper.updateGenre(genre, genreRequest);
    genreRepository.save(genre);
    return genreMapper.entityToDto(genre);
  }
  @ReadOnlyProperty
  public List<GenreResponse> getAllGenres() {
    List<Genre> genres = genreRepository.findAll();
    return genres.stream()
        .map(genreMapper::entityToDto)
        .toList();
  }

  public void deleteGenre(String genreTitle) {
    genreRepository.deleteAllByTitle(genreTitle);
  }
}
