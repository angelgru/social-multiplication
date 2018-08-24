package microservices.book.socialmultiplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.book.socialmultiplication.multiplication.controller.MultiplicationResultAttemptController;
import microservices.book.socialmultiplication.multiplication.domain.Multiplication;
import microservices.book.socialmultiplication.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.multiplication.domain.User;
import microservices.book.socialmultiplication.multiplication.service.MultiplicationService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTest {

    @MockBean
    private MultiplicationService multiplicationService;

    private JacksonTester<MultiplicationResultAttempt> json;
    private JacksonTester<List<MultiplicationResultAttempt>> jsonResultAttemptList;

    @Autowired
    MockMvc mvc;

    @Before
    public void setup(){
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void postResultReturnCorrect() throws Exception {
//        Given
        User user = new User("ben");
        Multiplication multiplication = new Multiplication(50, 60);
        MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3000, false);

        given(multiplicationService.checkAttempt(any(MultiplicationResultAttempt.class))).willReturn(true);

//        When
        MockHttpServletResponse response = mvc.perform(post("/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.write(resultAttempt).getJson()))
                .andReturn().getResponse();

//        Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(json.write(new MultiplicationResultAttempt(user, multiplication, resultAttempt.getResultAttempt(), true)).getJson());

    }

    @Test
    public void getStatisticsTest() throws Exception {
//        Given
        User user = new User("john_doe");
        Multiplication multiplication = new Multiplication(50, 70);
        MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 3500, true);

        List<MultiplicationResultAttempt> recentAttempts = Lists.newArrayList(resultAttempt);
        given(multiplicationService.retrieveStats("john_doe")).willReturn(recentAttempts);

//        When
        MockHttpServletResponse response = mvc.perform(get("/results").param("alias", "john_doe"))
                .andReturn().getResponse();

//        Then
        assertThat(response.getContentAsString()).isEqualTo(jsonResultAttemptList.write(recentAttempts).getJson());
    }

    @Test
    public void getResultByIdTest() throws Exception {
//        Given
        Multiplication multiplication = new Multiplication(11, 11);
        User user = new User("john_doe");
        MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(user, multiplication, 110, true);

        given(multiplicationService.getResultById(1L)).willReturn(multiplicationResultAttempt);

//        When
        MockHttpServletResponse response = mvc.perform(get("/results/1")).andReturn().getResponse();

//        Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(json.write(multiplicationResultAttempt).getJson());
    }
}
