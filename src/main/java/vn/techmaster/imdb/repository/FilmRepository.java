package vn.techmaster.imdb.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import vn.techmaster.imdb.model.Film;

@Repository
public class FilmRepository implements IFilmRepo {

  private List<Film> films;

  public FilmRepository(@Value("${datafile}") String datafile) {
    try {
      File file = ResourceUtils.getFile("classpath:static/" + datafile);
      ObjectMapper mapper = new ObjectMapper(); // Dùng để ánh xạ cột trong CSV với từng trường trong POJO
      films = Arrays.asList(mapper.readValue(file, Film[].class));
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public List<Film> getAll() {
    return films;
  }

  @Override
  public Map<String, List<Film>> getFilmByCountry() {
    // TODO Auto-generated method stub
    return films.stream().collect(Collectors.groupingBy(Film::getCountry));
  }

  @Override
  public Entry<String, Integer> getcountryMakeMostFilms() {
    // TODO Auto-generated method stub
    var countryCount = films.stream().collect(
            Collectors.groupingBy(film -> film.getCountry(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

    return countryCount.entrySet().stream()
            .max((c1, c2) -> c1.getValue().compareTo(c2.getValue())).get();
  }

  @Override
  public Entry<Integer, Integer> yearMakeMostFilms() {
    // TODO Auto-generated method stub
    var yearCount = films.stream().collect(
            Collectors.groupingBy(film -> film.getYear(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

    return yearCount.entrySet().stream()
            .max((c1, c2) -> c1.getValue().compareTo(c2.getValue())).get();
  }

  @Override
  public List<String> getAllGeneres() {
    return films.stream().map(Film::getGeneres).flatMap(Collection::stream).distinct().toList();
  }

  @Override
  public List<Film> getFilmsMadeByCountryFromYearToYear(String country, int fromYear,
                                                        int toYear) {
    // TODO Auto-generated method stub
    return films.stream().filter(film -> {
      return film.getCountry().equals(country) && fromYear <= film.getYear()
              && toYear >= film.getYear();
    }).collect(Collectors.toList());
  }

  @Override
  public Map<String, List<Film>> categorizeFilmByGenere() {
    // TODO Auto-generated method stub
    Map<String, List<Film>> listGeneres = getAllGeneres().stream().collect(Collectors.toMap(s -> s, s -> new ArrayList()));
    films.forEach(film -> {
      film.getGeneres().forEach(s -> {
        listGeneres.get(s).add(film);
      });
    });

    return listGeneres;
  }

  @Override
  public List<Film> top5HighMarginFilms() {
    // TODO Auto-generated method stub
    return films.stream().sorted(
                    (film1, film2) -> (film2.getRevenue() - film2.getCost()) - (film1.getRevenue()
                            - film1.getCost()))
            .limit(5).collect(Collectors.toList());
  }

  @Override
  public List<Film> top5HighMarginFilmsIn1990to2000() {
    // TODO Auto-generated method stub
    return films.stream().filter(film -> {
              return film.getYear() >= 1990 && film.getYear() <= 2000;
            }).sorted(
                    (film1, film2) -> (film2.getRevenue() - film2.getCost()) - (film1.getRevenue()
                            - film1.getCost()))
            .limit(5).collect(Collectors.toList());
  }

  @Override
  public double ratioBetweenGenere(String genreX, String genreY) {
    var allGenere = films.stream().map(Film::getGeneres).flatMap(Collection::stream).toList();

    return allGenere.stream().collect(
            Collectors.teeing(
                    Collectors.filtering(x -> x.equals(genreX), Collectors.counting()),
                    Collectors.filtering(y -> y.equals(genreY), Collectors.counting()),
                    (x, y) -> {
                      return (double) x / y;
                    }));
  }

  @Override
  public List<Film> top5FilmsHighRatingButLowMargin() {
    // TODO Auto-generated method stub
    return films.stream()
            .sorted(Comparator.comparing(Film::getRating).reversed()
                    .thenComparing(Comparator.comparing(film -> {
                      return film.getRevenue() - film.getCost();
                    }))).limit(5).toList();
  }

}