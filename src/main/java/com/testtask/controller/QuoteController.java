package com.testtask.controller;

import com.testtask.Entity.Quote;
import com.testtask.Service.QuoteRatingService;
import com.testtask.Service.QuoteService;
import com.testtask.model.QuoteCreateModel;
import com.testtask.model.QuoteModel;
import com.testtask.model.VoteModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController()
@Slf4j
@RequestMapping(value = "/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;
    private final QuoteRatingService quoteRatingService;


    public QuoteController(QuoteService quoteService, QuoteRatingService quoteRatingService) {
        this.quoteService = quoteService;
        this.quoteRatingService = quoteRatingService;
    }

    @GetMapping
    public ResponseEntity getAll() {
        try {
            List<QuoteModel> model = quoteService.getAll();
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            log.warn(e.toString());
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity create(@RequestBody QuoteCreateModel quoteModel) {
        try {
            Quote quote = Quote.builder()
                    .content(quoteModel.getContent())
                    .date(Instant.now())
                    .score(0)
                    .build();

            QuoteModel createdUser = quoteService.save(quote);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getQuoteById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quoteService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity updateQuote(@PathVariable Long id, @RequestBody QuoteCreateModel quoteModel) {
        try {
            QuoteModel existingQuote = quoteService.update(id, quoteModel);
            return ResponseEntity.ok(existingQuote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity deleteQuote(@PathVariable Long id) {
        try {
            quoteService.deleteById(id);
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @GetMapping("/random")
    public ResponseEntity getRandomQuote() {
        try {
            return ResponseEntity.ok(quoteService.getRandomQuote());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/top")
    public ResponseEntity getTopQuote(@RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        try {
            return ResponseEntity.ok(quoteService.getTopQuote(count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/flop")
    public ResponseEntity getFlopQuote(@RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        try {
            return ResponseEntity.ok(quoteService.getFlopQuote(count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/last")
    public ResponseEntity getLastQuote(@RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        try {
            return ResponseEntity.ok(quoteService.getLastQuote(count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/votes")
    public ResponseEntity getAllOperations(@PathVariable Long id) {
        try {
            QuoteModel quoteModel = quoteService.findById(id);
            return ResponseEntity.ok().body(quoteModel.getScore());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/votes")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity vote(@PathVariable Long id, @RequestBody VoteModel voteModel) {
        try {
            quoteRatingService.vote(id, voteModel);
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
