package com.testtask.repo;

import com.testtask.Entity.QuoteRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRatingRepo extends JpaRepository<QuoteRating, Long> {

    void deleteByQuoteIdAndUserId(Long quoteId, Long userId);
    Optional<QuoteRating> findFirstByQuoteIdAndUserId(Long quoteId, Long userId);
    List<QuoteRating> findFirst5ByUserIdOrderByDateDesc(Long userId);
}
