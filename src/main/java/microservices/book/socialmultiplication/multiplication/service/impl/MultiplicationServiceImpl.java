package microservices.book.socialmultiplication.multiplication.service.impl;

import microservices.book.socialmultiplication.multiplication.domain.Multiplication;
import microservices.book.socialmultiplication.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.multiplication.domain.User;
import microservices.book.socialmultiplication.multiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.multiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.multiplication.repository.UserRepository;
import microservices.book.socialmultiplication.multiplication.service.MultiplicationService;
import microservices.book.socialmultiplication.multiplication.service.RandomGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

    private RandomGeneratorService randomGeneratorService;
    private MultiplicationResultAttemptRepository attemptRepository;
    private UserRepository userRepository;
    private EventDispatcher eventDispatcher;

    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository attemptRepository,
                                     UserRepository userRepository,
                                     EventDispatcher eventDispatcher) {
        this.randomGeneratorService = randomGeneratorService;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();

        return new Multiplication(factorA, factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {

        Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());

        boolean correct = (resultAttempt.getMultiplication().getFactorA() *
                resultAttempt.getMultiplication().getFactorB()) == resultAttempt.getResultAttempt();

        MultiplicationResultAttempt copyAttempt = new MultiplicationResultAttempt(
                user.orElse(resultAttempt.getUser()),
                resultAttempt.getMultiplication(),
                resultAttempt.getResultAttempt(),
                correct
        );

        attemptRepository.save(copyAttempt);
        eventDispatcher.send(new MultiplicationSolvedEvent(copyAttempt.getId(),
                copyAttempt.getUser().getId(),
                copyAttempt.isCorrect())
        );

        return correct;
    }

    @Override
    public List<MultiplicationResultAttempt> retrieveStats(String alias) {
        return attemptRepository.findTop5ByUserAliasOrderByIdDesc(alias);
    }

    @Override
    public MultiplicationResultAttempt getResultById(final Long resultId) {
        Optional<MultiplicationResultAttempt> multiplicationResultAttempt = attemptRepository.findById(resultId);

        return multiplicationResultAttempt.orElse(null);
    }
}
