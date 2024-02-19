package com.puzzlemaker.integration;

import io.restassured.response.Response;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true", "server.servlet.context-path=/"})
public class TestController {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp(){
        RestAssured.port = this.port;
    }

    @Test
    public void pingResponseTest(){
        Response response = given().get("/ping").thenReturn();
        Assertions.assertEquals("Saul Goodman B)", response.asString());
    }
}
