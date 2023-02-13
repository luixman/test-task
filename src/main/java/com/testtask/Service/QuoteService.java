package com.testtask.Service;

import com.testtask.Entity.Quote;
import com.testtask.Entity.User;
import com.testtask.exception.NoPermissionException;
import com.testtask.model.QuoteCreateModel;
import com.testtask.model.QuoteModel;
import com.testtask.repo.QuoteRepo;
import com.testtask.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    private final QuoteRepo quoteRepo;
    private final UserService userService;

    public QuoteService(QuoteRepo quoteRepo, UserService userService) {
        this.quoteRepo = quoteRepo;
        this.userService = userService;
    }

    public List<QuoteModel> getAll() {
        return quoteRepo.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    public QuoteModel save(Quote quote) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUserByUsername(authentication.getName());
        quote.setUser(user);
        return toModel(quoteRepo.save(quote));
    }

    public QuoteModel findById(Long id) {
        return toModel(quoteRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found")));

    }

    public QuoteModel update(Long id, QuoteCreateModel quoteModel) {

        Quote existingQuote = quoteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        if (!checkPermissions(existingQuote))
            throw new NoPermissionException("no permissions");

        existingQuote.setContent(quoteModel.getContent());
        existingQuote.setDate(Instant.now());
        quoteRepo.save(existingQuote);
        return toModel(existingQuote);
    }

    public void deleteById(Long id) {
        Quote existingQuote = quoteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        if (!checkPermissions(existingQuote))
            throw new NoPermissionException("no permissions");

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

    protected Quote toEntity(QuoteModel quoteModel) {
        User user = userService.findUserByUsername(quoteModel.getCreator());
        return Quote.builder()
                .id(quoteModel.getId())
                .score(quoteModel.getScore())
                .content(quoteModel.getContent())
                .date(quoteModel.getDate())
                .user(user)
                .build();
    }

    private boolean checkPermissions(Quote quote) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User quoteOwner = quote.getUser();

        if (authentication.getName().equals(quoteOwner.getUsername()))
            return true;

        if (authentication.getAuthorities().equals("ROLE_ADMIN"))
            return true;

        return false;
    }
}
