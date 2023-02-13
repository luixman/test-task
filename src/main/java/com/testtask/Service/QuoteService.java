package com.testtask.Service;

import com.testtask.Entity.Quote;
import com.testtask.model.QuoteModel;
import com.testtask.repo.QuoteRepo;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    private final QuoteRepo quoteRepo;

    public QuoteService(QuoteRepo quoteRepo) {
        this.quoteRepo = quoteRepo;
    }

    public List<QuoteModel> getAll() {
        return quoteRepo.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    public QuoteModel save(Quote quote) {

        return toModel(quoteRepo.save(quote));
    }

    public QuoteModel findById(Long id) {
        return toModel(quoteRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found")));

    }

    public void update(Long id, Quote quote) {
        Quote existingQuote = quoteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        existingQuote.setContent(quote.getContent());
        quote.setScore(quote.getScore());
        quote.setDate(quote.getDate());
        quoteRepo.save(existingQuote);
    }

    public void deleteById(Long id) {
        quoteRepo.deleteById(id);
    }

    public QuoteModel getRandomQuote() {
        return toModel(quoteRepo.getRandomQuote());

    }

    public List<QuoteModel> getTopQuote(Integer count) {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count must be more than 0 and less than 100");
        return quoteRepo.findTopCountQuotes(count).stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<QuoteModel> getFlopQuote(Integer count) {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count must be more than 0 and less than 100");
        return quoteRepo.findFlopCountQuotes(count).stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<QuoteModel> getLastQuote(Integer count) {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count must be more than 0 and less than 100");
        return quoteRepo.findLastCountQuotes(count).stream().map(this::toModel).collect(Collectors.toList());
    }

    public QuoteModel toModel(Quote quote) {
        return QuoteModel.builder()
                .id(quote.getId())
                .score(quote.getScore())
                .content(quote.getContent())
                .date(quote.getDate())
                .creator(quote.getUser().getUsername())
                .build();
    }
}
