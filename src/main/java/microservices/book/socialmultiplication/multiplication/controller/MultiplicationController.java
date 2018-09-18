package microservices.book.socialmultiplication.multiplication.controller;

import lombok.extern.slf4j.Slf4j;
import microservices.book.socialmultiplication.multiplication.domain.Multiplication;
import microservices.book.socialmultiplication.multiplication.service.MultiplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/multiplications")
public class MultiplicationController {

    private int serverPort;
    private MultiplicationService multiplicationService;

    @Autowired
    public MultiplicationController(MultiplicationService multiplicationService, @Value("${server.port}") int serverPort) {
        this.multiplicationService = multiplicationService;
        this.serverPort = serverPort;
    }

    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public Multiplication getMultiplication() {
        log.info("Generating random multiplication from server @{}", serverPort);
        return multiplicationService.createRandomMultiplication();
    }
}
