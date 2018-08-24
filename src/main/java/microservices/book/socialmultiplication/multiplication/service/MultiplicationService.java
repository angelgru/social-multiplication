package microservices.book.socialmultiplication.multiplication.service;

import microservices.book.socialmultiplication.multiplication.domain.Multiplication;
import microservices.book.socialmultiplication.multiplication.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();

    boolean checkAttempt(MultiplicationResultAttempt resultAttempt);

    List<MultiplicationResultAttempt> retrieveStats(String alias);

    MultiplicationResultAttempt getResultById(final Long resultId);
}
