package com.puzzlemaker.integration;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true", "server.servlet.context-path=/"})
public class ControllerTests {

    @LocalServerPort
    int port;
    String adminLogin = "admin";
    String adminPassword = "admin";

    @BeforeEach
    public void setUp() {
        RestAssured.port = this.port;
    }

    @Test
    public void getAllUsersTest() throws Exception {
        Response response = given().get("/user/getAll");
        JSONArray array = new JSONArray(response.asString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("admin", array.getJSONObject(0).get("login"));
        Assertions.assertEquals("test", array.getJSONObject(1).get("login"));
    }

    @Test
    public void loginAdminTest() throws Exception {
        JSONObject body = new JSONObject();
        body.put("login", adminLogin);
        body.put("password", adminPassword);
        //build request
        RequestSpecification request = given().contentType(ContentType.JSON);
        request.body(body.toString());
        //make a request
        Response response = request.post("/user/login");
        //asserts
        Assertions.assertEquals(200, response.statusCode());
        JSONArray responseJSON = new JSONArray(response.asString());
        Assertions.assertEquals(2, responseJSON.length());
        Assertions.assertEquals("true", (String) responseJSON.get(0));
    }

    @Test
    public void isAdminSession() throws Exception {
        //get admin session
        JSONObject body = new JSONObject();
        body.put("login", adminLogin);
        body.put("password", adminPassword);
        //build request
        RequestSpecification requestSpecification1 = given().contentType(ContentType.JSON);
        requestSpecification1.body(body.toString());
        //make a request
        Response response1 = requestSpecification1.post("/user/login");
        JSONArray responseObjects = new JSONArray(response1.asString());
        String sessionId = (String) responseObjects.get(1);
        //build request
        RequestSpecification requestSpecification = given();
        //request
        Response response2 = requestSpecification.get("/user/isAdmin/{session}", sessionId).thenReturn();
        //asserts
        Assertions.assertEquals(200, response2.statusCode());
        Assertions.assertEquals("true", response2.asString());
    }

    @Test
    public void adminGamesTest() throws Exception {
        JSONObject body = new JSONObject();
        body.put("login", adminLogin);
        body.put("password", adminPassword);
        //build request
        RequestSpecification requestSpecification1 = given().contentType(ContentType.JSON);
        requestSpecification1.body(body.toString());
        //make a request
        Response response2 = given().auth().basic(adminLogin, adminPassword).get("/user/admin/games").andReturn();
        JSONArray responseJSON = new JSONArray(response2.asString());
        Assertions.assertEquals(1, responseJSON.length());
    }

    @Test
    public void blockAndUnblockTestUserFromAdmin() throws Exception {
        //get user id to block
        JSONArray array = new JSONArray(given().get("/user/getAll").andReturn().asString());
        String userID = (String) array.getJSONObject(1).get("id");
        String username = (String) array.getJSONObject(1).get("login");
        Assertions.assertEquals("test", username);
        //block user
        Assertions.assertEquals(userID, given().get("/user/" + userID + "/block/" + true).andReturn().asString());
        //check if user is blocked
        JSONArray array2 = new JSONArray(given().get("/user/getAll").asString());
        Boolean status = (Boolean) array2.getJSONObject(1).get("isBlocked");
        Assertions.assertTrue(status);
        //unblock user
        Assertions.assertEquals(userID, given().get("/user/" + userID + "/block/" + false).andReturn().asString());
        //check if user is unblocked
        JSONArray array3 = new JSONArray(given().get("/user/getAll").asString());
        Boolean status2 = (Boolean) array3.getJSONObject(1).get("isBlocked");
        Assertions.assertFalse(status2);
    }

    @Test
    public void getUserById() throws Exception {
        //get users
        JSONArray array = new JSONArray(given().get("/user/getAll").andReturn().asString());
        String userID = (String) array.getJSONObject(1).get("id");
        String username = (String) array.getJSONObject(1).get("login");
        Assertions.assertEquals("test", username);
        JSONObject responseJson = new JSONObject(given().get("/user/get/" + userID).thenReturn().asString());
        Assertions.assertEquals("test", (String) responseJson.get("login"));
    }





    @Test
    public void getAllPublicGames() throws Exception {
        Response response = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        JSONArray array = new JSONArray(response.asString());
        System.out.println(array);
        Assertions.assertTrue(array.length() >= 0);

    }

    @Test
    public void getExampleGame() throws Exception {
        Response response = given().get("/game/get/example").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(24, response.asString().toCharArray().length);
    }

    @Test
    public void getAdminGames() throws Exception {
        Response response = given().get("/game/admin/getAll").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        JSONArray array = new JSONArray(response.asString());
        System.out.println(array);
        Assertions.assertTrue(array.length() >= 2);
        JSONObject json1 = (JSONObject) array.get(0);
        JSONObject json2 = (JSONObject) array.get(1);
        List<String> titles = List.of((String) json1.get("title"), (String) json2.get("title"));
        Assertions.assertTrue(titles.contains("A test game"));
        Assertions.assertTrue(titles.contains("Example Game"));
    }



}
