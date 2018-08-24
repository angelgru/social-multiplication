package microservices.book.socialmultiplication.service;

import microservices.book.socialmultiplication.multiplication.domain.Multiplication;
import microservices.book.socialmultiplication.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.multiplication.domain.User;
import microservices.book.socialmultiplication.multiplication.event.EventDispatcher;
import microservices.book.socialmultiplication.multiplication.event.MultiplicationSolvedEvent;
import microservices.book.socialmultiplication.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.socialmultiplication.multiplication.repository.UserRepository;
import microservices.book.socialmultiplication.multiplication.service.MultiplicationService;
import microservices.book.socialmultiplication.multiplication.service.impl.MultiplicationServiceImpl;
import microservices.book.socialmultiplication.multiplication.service.RandomGeneratorService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class MultiplicationServiceTest {

    private MultiplicationService multiplicationService;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository attemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventDispatcher eventDispatcher;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        multiplicationService = new MultiplicationServiceImpl(randomGeneratorService, attemptRepository, userRepository, eventDispatcher);
    }

    @Test
    public void createRandomMultiplicationTest() {
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 60);

        Multiplication multiplication = multiplicationService.createRandomMultiplication();

        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(60);
    }


    @Test
    public void checkCorrectAttemptTest() {
//        Given
        User user = new User("john_doe");
        Multiplication multiplication = new Multiplication(50, 60);
        MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, true);

        MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(
                multiplicationResultAttempt.getUser(),
                multiplicationResultAttempt.getMultiplication(),
                multiplicationResultAttempt.getResultAttempt(),
                true);

        MultiplicationSolvedEvent event = new MultiplicationSolvedEvent(verifiedAttempt.getId(),
                verifiedAttempt.getUser().getId(), true);

        given(userRepository.findByAlias("john_doe")).
                willReturn(Optional.empty());

//        When
        boolean result = multiplicationService.checkAttempt(multiplicationResultAttempt);

//        Assert
        assertThat(result).isTrue();
        verify(attemptRepository).save(verifiedAttempt);
        verify(eventDispatcher).send(eq(event));
    }

    @Test
    public void checkIncorrectAttemptTest() {
//        Given
        User user = new User("john_doe");
        Multiplication multiplication = new Multiplication(50, 60);
        MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(user, multiplication, 3010, false);

        given(userRepository.findByAlias("john_doe")).willReturn(Optional.empty());

//        When
        boolean result = multiplicationService.checkAttempt(multiplicationResultAttempt);

//        Assert
        assertThat(result).isFalse();
        verify(attemptRepository).save(multiplicationResultAttempt);
    }

    @Test
    public void retrieveStatsTest() {
//        Given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("john_doe");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user,
                multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user,
                multiplication, 3060, false);
        List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);

        given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("john_doe")).willReturn(latestAttempts);

//        When
        List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationService.retrieveStats("john_doe");

//        Assert
        assertThat(latestAttemptsResult).isEqualTo(latestAttemptsResult);

    }

    @Test
    public void getResultByIdTest(){
//        Given
        Long attemptId = 11L;
        Multiplication multiplication = new Multiplication(11, 20);
        User user = new User("john_doe");
        MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(
                user,
                multiplication,
                220,
                true
        );

        given(attemptRepository.findById(attemptId)).willReturn(Optional.of(multiplicationResultAttempt));

//        When
        MultiplicationResultAttempt returnedResult = multiplicationService.getResultById(attemptId);

//        Assert
        assertThat(returnedResult).isEqualTo(multiplicationResultAttempt);
    }
}
