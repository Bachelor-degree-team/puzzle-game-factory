package com.puzzlemaker.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true", "server.servlet.context-path=/"})
public class GameTests {
    @LocalServerPort
    int port;
    String newUserLogin = "newUser#1";
    String newUserPassword = "testpassword";
    String newUserEmail = "test@test.pl";
    static String createdGameId;
    static String activeGameId;
    static String correctGuess;
    static String incorrectGuess;
    static String sessionId;
    static String createdUserId;
    static String rating = "3";

    Path correctCSV = Paths.get(System.getProperty("user.dir"), "src", "test", "java", "com", "puzzlemaker",
            "resources", "positive.csv");


    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
    }


    @Test
    @Order(1)
    public void addUserTest()throws Exception{
        Response responseBefore = given().get("/user/getAll").thenReturn();
        Assertions.assertEquals(200, responseBefore.statusCode());
        JSONObject body = new JSONObject();
        body.put("login", newUserLogin);
        body.put("password", newUserPassword);
        body.put("email", newUserEmail);
        Assertions.assertEquals("true", given().contentType(ContentType.JSON).
                body(body.toString()).post("/register").thenReturn().asString());
        Response responseAfter = given().get("/user/getAll").thenReturn();
        Assertions.assertEquals(200, responseAfter.statusCode());
        Assertions.assertEquals((new JSONArray(responseBefore.asString())).length()+1, (new JSONArray(responseAfter.asString())).length());
        JSONArray arrayAfter = new JSONArray(responseAfter.asString());
        for (int i = 0; i < arrayAfter.length(); i++) {
            JSONObject json = (JSONObject) arrayAfter.get(i);
            String login = (String) json.get("login");
            if(login.compareTo(newUserLogin) == 0){
                createdUserId = (String) json.get("id");
            }
        }
    }
    @Test
    @Order(2)
    public void addGameTest() throws Exception {
        //login user
        Response before = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, before.statusCode());
        JSONObject bodyLogin = new JSONObject();
        bodyLogin.put("login", newUserLogin);
        bodyLogin.put("password", newUserPassword);
        JSONArray session = new JSONArray(given().contentType(ContentType.JSON).body(bodyLogin.toString())
                .when().post("/user/login").thenReturn().asString());
        sessionId = (String) session.get(1);
        Map<String, String> params = new HashMap<>();
        params.put("separator", ",");
        params.put("isPublic", "true");
        params.put("title", "add file test title");
        params.put("desc", "add file test description to assure request correctness");
        params.put("session",sessionId);
        Response response = given().contentType(ContentType.MULTIPART)
                .auth().basic(newUserLogin, newUserPassword)
                .params(params)
                .multiPart("csv", new File(correctCSV.toString()))
                .post("/game/create").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        createdGameId = response.asString();
        System.out.println("Created game id " + createdGameId);
        Response after = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, after.statusCode());
        int beforeLength = (new JSONArray(before.asString())).length();
        int afterLength = (new JSONArray(after.asString())).length();
        Assertions.assertEquals(beforeLength+1, afterLength);
    }
    @Test
    @Order(3)
    public void startGameTest(){
        System.out.println(createdGameId);
        Response response= given().auth().basic(newUserLogin, newUserPassword).get("/game/"+createdGameId + "/play").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        activeGameId = response.asString();
    }
    @Test
    @Order(4)
    public void getActiveGameTest()throws Exception{
        Response response = given().get("/game/active/get/"+activeGameId).thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        JSONObject responseJSON = new JSONObject(response.asString());
        correctGuess = (String)(responseJSON.get("correctGuess"));
        JSONArray guesses = (JSONArray) responseJSON.get("guesses");
        for (int i = 0; i < guesses.length(); i++) {
            String guess = (String) guesses.get(i);
            if(guess.compareTo(correctGuess)!= 0){
                incorrectGuess = guess;
                break;
            }
        }
    }
    @Test
    @Order(5)
    public void incorrectGuessTest()throws Exception{
        Response response = given().get("/game/"+activeGameId+"/guess/"+incorrectGuess).thenReturn();
        System.out.println(response.asString());
        Assertions.assertEquals(200,response.statusCode());
        JSONObject responseJSON= new JSONObject(response.asString());
        JSONArray responseArray = (JSONArray) responseJSON.get("game_won");
        Assertions.assertEquals("false",responseArray.get(0));
    }
    @Test
    @Order(6)
    public void correctGuessTest()throws Exception{
        Response response = given().get("/game/"+ activeGameId+"/guess/"+correctGuess).thenReturn();
        System.out.println(response.asString());
        Assertions.assertEquals(200,response.statusCode());
        JSONObject responseJSON= new JSONObject(response.asString());
        JSONArray responseArray = (JSONArray) responseJSON.get("game_won");
        Assertions.assertEquals("true",responseArray.get(0));
    }
    @Test
    @Order(7)
    public void addScoreTest(){
        Response response = given().get("/user/"+sessionId+"/scores/"+createdGameId+"/add/"+2);
        Assertions.assertEquals(200, response.statusCode());
        System.out.println("Add score response " +response.asString());
    }
    @Test
    @Order(8)
    public void getScoresTest(){
        Response response = given().get("/user/"+sessionId+"/scores/get");
        Assertions.assertEquals(200, response.statusCode());
        System.out.println(response.asString());
    }

    @Test
    @Order(9)
    public void addRatingTest(){
        Response response = given().auth().basic(newUserLogin, newUserPassword).
                get("/game/"+createdGameId+"/ratings/"+sessionId+"/rate/"+rating);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(createdGameId, response.asString());
    }
    @Test
    @Order(10)
    public void getRatingsTest(){
        Response response = given().auth().basic(newUserLogin, newUserPassword).
                get("/game/"+createdGameId+"/ratings/get").thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("3.0", response.asString());
    }
    @Test
    @Order(11)
    public void makeGameInvisible()throws Exception{
        Response before = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, before.statusCode());
        Response response = given().get("/game/visibility/"+createdGameId);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("true", response.asString());
        Response response1 = given().auth().basic(newUserLogin,newUserPassword)
                .get("/game/get/"+createdGameId).thenReturn();
        Assertions.assertEquals(200, response1.statusCode());
        Response after = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, after.statusCode());
        Assertions.assertEquals((new JSONArray(before.asString()).length()), (new JSONArray(after.asString()).length()+1));
    }
    @Test
    @Order(12)
    public void makeGameVisible()throws Exception{
        Response before = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, before.statusCode());
        Response response = given().get("/game/visibility/"+createdGameId);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("true", response.asString());
        Response response1 = given().auth().basic(newUserLogin,newUserPassword)
                .get("/game/get/"+createdGameId).thenReturn();
        Assertions.assertEquals(200, response1.statusCode());
        Response after = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, after.statusCode());
        Assertions.assertEquals((new JSONArray(before.asString()).length())+1, (new JSONArray(after.asString()).length()));
    }
    @Test
    @Order(13)
    public void removeGameTest(){
        Response before = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, before.statusCode());
        Response response = given().auth().basic(newUserLogin, newUserPassword)
                .get("/game/remove/"+ createdGameId).thenReturn();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("true", response.asString());
        Response after = given().get("/game/public/getAll").thenReturn();
        Assertions.assertEquals(200, after.statusCode());


    }
    @Test
    @Order(14)
    public void removeUserTest() throws Exception {
        Response responseBefore = given().get("/user/getAll").thenReturn();
        Assertions.assertEquals(200, responseBefore.statusCode());
        Response responseRemove = given().auth().basic(newUserLogin, newUserPassword)
                .get("/user/remove/" + createdUserId).thenReturn();
        Assertions.assertEquals(200, responseRemove.statusCode());
        Response responseAfter = given().get("/user/getAll").thenReturn();
        Assertions.assertEquals(200, responseAfter.statusCode());
        Assertions.assertEquals((new JSONArray(responseBefore.asString())).length(), (new JSONArray(responseAfter.asString())).length()+1);
    }
}
