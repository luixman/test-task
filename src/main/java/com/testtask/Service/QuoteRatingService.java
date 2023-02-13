package com.testtask.Service;

import com.testtask.Entity.Quote;
import com.testtask.Entity.QuoteRating;
import com.testtask.Entity.User;
import com.testtask.model.QuoteModel;
import com.testtask.model.VoteModel;
import com.testtask.repo.QuoteRatingRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteRatingService {
    private final QuoteRatingRepo quoteRatingRepo;
    private final UserService userService;
    private final QuoteService quoteService;


    public QuoteRatingService(QuoteRatingRepo quoteRatingRepo, UserService userService, QuoteService quoteService) {
        this.quoteRatingRepo = quoteRatingRepo;
        this.userService = userService;
        this.quoteService = quoteService;
    }

    @Transactional
    public void vote(Long quoteId, VoteModel voteModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(authentication.getName());
        Character vote = voteModel.getVote();
        Optional<QuoteRating> existingQuoteRating = quoteRatingRepo.findFirstByQuoteIdAndUserId(quoteId, user.getId());

        QuoteModel quoteModel = quoteService.findById(quoteId);

        if (vote.equals('+') || vote.equals('-')) {
            if (existingQuoteRating.isEmpty()) {
                QuoteRating quoteRating = QuoteRating.builder()
                        .operation(vote)
                        .date(Instant.now())
                        .userId(user.getId())
                        .quoteId(quoteId)
                        .build();
                quoteRatingRepo.save(quoteRating);
                if (vote.equals('+'))
                    quoteModel.setScore(quoteModel.getScore() + 1);
                else
                    quoteModel.setScore(quoteModel.getScore() - 1);
            } else {
                QuoteRating quoteRating = existingQuoteRating.get();
                if (quoteRating.getOperation().equals(vote)) {
                    return;
                } else {
                    switch (vote) {
                        case '+':
                            quoteModel.setScore(quoteModel.getScore() + 2);
                            quoteRating.setOperation('+');
                            break;
                        case '-':
                            quoteModel.setScore(quoteModel.getScore() - 2);
                            quoteRating.setOperation('-');
                            break;
                    }
                    quoteRatingRepo.save(quoteRating);
                }
            }


        } else if (vote.equals('0')) {
            //находим существующий объект,
            //разбираемся отминусовать или прибавить лайк нужно+
            //удаляем запись
            //сохраняем quote

            if (existingQuoteRating.isPresent()) {
                Character operation = existingQuoteRating.get().getOperation();
                if (operation == '+') {
                    quoteModel.setScore(quoteModel.getScore() - 1);
                } else if (operation == '-') {
                    quoteModel.setScore((quoteModel.getScore()) + 1);
                }
                quoteRatingRepo.deleteByQuoteIdAndUserId(quoteId, user.getId());
            }
        }
        quoteService.save(quoteService.toEntity(quoteModel));


    }

    public List<QuoteRating> getLastVotes(Long userId) {

        return quoteRatingRepo.findFirst5ByUserIdOrderByDateDesc(userId);
    }
}
