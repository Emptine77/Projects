package edu.dnu.movieplex.movie.persistance.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieBriefResponse;
import edu.dnu.movieplex.movie.domain.dto.movie.MovieFilter;
import edu.dnu.movieplex.movie.persistance.model.entity.QMovie;
import edu.dnu.movieplex.movie.persistance.model.entity.QMovieSchedule;
import edu.dnu.movieplex.movie.persistance.model.entity.Movie;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * Implementation of the custom movie repository with Querydsl support.
 */
public class MovieRepositoryImpl extends QuerydslRepositorySupport
        implements MovieRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  private final QMovie queryMovie = QMovie.movie;
  private final QMovieSchedule queryMovieSchedule = QMovieSchedule.movieSchedule;

  public MovieRepositoryImpl(EntityManager entityManager) {
    super(Movie.class);
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<MovieBriefResponse> findAllByFilter(Pageable pageable, MovieFilter filter) {
    QueryResults<MovieBriefResponse> result = queryFactory
        .select(Projections.constructor(MovieBriefResponse.class,
            queryMovie.id,
            queryMovie.title,
            queryMovie.rating,
            queryMovie.duration,
            queryMovie.ageLimit,
            queryMovie.director,
            queryMovie.rentalPeriodStart,
            queryMovie.rentalPeriodEnd,
            queryMovie.posterUrl))
        .from(QMovie.movie)
        .leftJoin(QMovieSchedule.movieSchedule).on(queryMovie.id.eq(queryMovieSchedule.movie.id))
        .where(applyFilter(filter))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetchResults();
    long totalCount = result.getOffset();
    List<MovieBriefResponse> content = result.getResults();
    return new PageImpl<>(content, pageable, totalCount);
  }

  private BooleanExpression applyFilter(MovieFilter filter) {
    BooleanExpression predicate = queryMovie.isNotNull();

    if (Objects.nonNull(filter.date())) {
      predicate = predicate
        .and(queryMovieSchedule.startTime.year().eq(filter.date().getYear()))
        .and(queryMovieSchedule.startTime.month().eq(filter.date().getMonthValue()))
        .and(queryMovieSchedule.startTime.dayOfMonth().eq(filter.date().getDayOfMonth()));
    }

    return predicate;
  }
}
