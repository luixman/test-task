package com.testtask.controller;

import com.testtask.Entity.Quote;
import com.testtask.Entity.User;
import com.testtask.Service.OperationService;
import com.testtask.Service.QuoteService;
import com.testtask.model.QuoteCreateModel;
import com.testtask.model.QuoteModel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

@RestController()
@Slf4j
@RequestMapping(value = "/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;


    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;

    }

/*    @PostConstruct
    public void init() {
        User user = new User(1L, "user", "user", "user@mail.ru", Instant.now(), Role.USER, Status.ACTIVE);
        User user2 = new User(2L, "admin", "admin", "admin@mail.ru", Instant.now(), Role.ADMIN, Status.ACTIVE);

        Quote quote1 = Quote.builder()
                .id(1L)
                .content("first")
                .date(Instant.now())
                .score(0)
                .user(user)
                .build();

        Quote quote2 = Quote.builder()
                .id(2L)
                .content("test test test")
                .date(Instant.now())
                .score(0)
                .user(user2)
                .build();
        Quote quote3 = Quote.builder()
                .id(3L)
                .content("3")
                .date(Instant.now())
                .score(0)
                .user(user)
                .build();

        Quote quote4 = Quote.builder()
                .id(4L)
                .content("quote4")
                .date(Instant.now())
                .score(0)
                .user(user2)
                .build();


        quoteService.save(quote1);
        quoteService.save(quote2);
        quoteService.save(quote3);
        quoteService.save(quote4);

    }*/


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //@PreAuthorize("hasAuthority('all:read')")
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
    public ResponseEntity create(@RequestBody QuoteCreateModel quoteModel) {
/*        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info(authentication.getName());*/

        try {
            Quote quote = Quote.builder()
                    .content(quoteModel.getContent())
                    .date(Instant.now())
                    .score(0)
                    .user(null)// TODO: 12.02.2023 СПРИНГ СЕКУРИТИ
                    .build();

            QuoteModel createdUser = quoteService.save(quote);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity updateQuote(@PathVariable Long id, @RequestBody Quote quote) {
        try {
            quoteService.update(id, quote);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @DeleteMapping("/{id}")
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

    @GetMapping("/{id}/operations")
    public ResponseEntity getAllOperations(@PathVariable Long id) {
        return ResponseEntity.ok().body(id);
    }
}
