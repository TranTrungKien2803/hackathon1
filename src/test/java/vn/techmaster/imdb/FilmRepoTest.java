package vn.techmaster.imdb;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;
import vn.techmaster.imdb.model.Film;
import vn.techmaster.imdb.repository.FilmRepository;

@SpringBootTest
class FilmRepoTest {
	@Autowired
	FilmRepository filmRepository;

	@Test
	void getAll() throws IOException {
		var result = filmRepository.getAll();
		File file = ResourceUtils.getFile("classpath:static/filmsmall.json" );
		ObjectMapper mapper = new ObjectMapper();

		System.out.println(file);
		assertEquals(Arrays.asList(mapper.readValue(file, Film[].class)).size(), result.size());
	}

	@Test
	void getFilmByCountry() {
		var country = filmRepository.getAll().stream().map(Film::getCountry).toList();

		var result = filmRepository.getFilmByCountry();

		assertTrue(result.keySet().containsAll(country));

		country.forEach(s -> {
			result.get(s).forEach(film -> {
				assertEquals(s, film.getCountry());
			});
		});
	}

	@Test
	void getcountryMakeMostFilms() {
		System.out.println(filmRepository.getcountryMakeMostFilms());
		var result = filmRepository.getcountryMakeMostFilms();
		assertEquals("China", result.getKey());
		assertEquals(9, result.getValue());
	}

	@Test
	void yearMakeMostFilms() {
		System.out.println(filmRepository.yearMakeMostFilms());
		var result = filmRepository.yearMakeMostFilms();
		assertEquals(1985, result.getKey());
		assertEquals(4, result.getValue());
	}

	@Test
	void getAllGeneres() {
		Set<String> set = new HashSet<>();
		filmRepository.getAll().forEach(film -> {
			set.add(film.getCountry());
		});

		set.containsAll(filmRepository.getAllGeneres());
	}

	@Test
	void getFilmsMadeByCountryFromYearToYear() {
		var result = filmRepository.getFilmsMadeByCountryFromYearToYear("China", 1900, 2000);

		result.forEach(film -> {
			assertTrue(film.getYear() > 1900);
			assertTrue(film.getYear() < 2000);
		});
	}

	@Test
	void categorizeFilmByGenere() {
		var result = filmRepository.categorizeFilmByGenere();
		assertTrue(result.keySet().containsAll(filmRepository.getAllGeneres()));

		result.keySet().forEach(s -> {
			var check = result.get(s).stream().map(Film::getGeneres)
					.flatMap(Collection::stream).toList();
			assertTrue(check.indexOf(s) > -1);
		});
	}

	@Test
	void top5HighMarginFilms() {
		var result = filmRepository.top5HighMarginFilms();

		assertEquals(11, result.get(0).getId());
		assertEquals(15, result.get(1).getId());
		assertEquals(16, result.get(2).getId());
		assertEquals(8, result.get(3).getId());
		assertEquals(19, result.get(4).getId());
	}

	@Test
	void top5HighMarginFilmsIn1990to2000() {
		var result = filmRepository.top5HighMarginFilmsIn1990to2000();

		result.forEach(film -> {
			assertTrue(film.getYear() > 1900);
			assertTrue(film.getYear() < 2000);
		});
	}

	@Test
	void ratioBetweenGenere() {
		var generes = filmRepository.getAll().stream()
				.map(Film::getGeneres).flatMap(Collection::stream).toList();
		var horror = 0;
		var drama = 0;
		for (int i = 0; i < generes.size(); i++){
			if (generes.get(i).equals("horror")){
				horror += 1;
			} else if (generes.get(i).equals("drama")){
				drama +=1;
			}
		}

		assertEquals(horror/drama, filmRepository.ratioBetweenGenere("horror", "drama"));
	}

	@Test
	void top5FilmsHighRatingButLowMargin() {
		var result = filmRepository.top5FilmsHighRatingButLowMargin();

		assertEquals(18, result.get(0).getId());
		assertEquals(7, result.get(1).getId());
		assertEquals(27, result.get(2).getId());
		assertEquals(19, result.get(3).getId());
		assertEquals(6, result.get(4).getId());
	}
}