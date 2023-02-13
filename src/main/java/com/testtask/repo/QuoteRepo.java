package com.testtask.repo;

import com.testtask.Entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRepo extends JpaRepository<Quote, Long> {

    @Query(value = "SELECT * FROM quote group by RAND() LIMIT 1", nativeQuery = true)
    Quote getRandomQuote();

    @Query(value = "SELECT * from quote ORDER BY score DESC LIMIT :count", nativeQuery = true)
    List<Quote> findTopCountQuotes(@Param("count") Integer count);

    @Query(value = "SELECT * from quote ORDER BY score  LIMIT :count", nativeQuery = true)
    List<Quote> findFlopCountQuotes(@Param("count") Integer count);


    @Query(value = "SELECT * FROM quote ORDER BY id DESC LIMIT :count", nativeQuery = true)
    List<Quote> findLastCountQuotes(@Param("count") Integer count);

}
