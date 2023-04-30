package ru.praktikum.stellarburger.api;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import ru.praktikum.stellarburger.api.auth.register.CreateUser;
import ru.praktikum.stellarburger.api.auth.register.CreateUserResponse;
import static io.restassured.RestAssured.given;

public class TestBase {
    private final String email = "olgaleto@yandex.ru";
    private final String password = "qaws1234";
    private final String name = "Apsenty";
    protected String userAccessToken;
    private final String URI_DELETE_USER = "/api/auth/user";

    @Before
    public void setUp() { // предусловие - создание юзера и присвоение переменной значения accessToken
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        CreateUser createUser = new CreateUser(email, password, name); //создание пользователя
        CreateUserResponse createUserResponse = createUser.getCreateUserResponse(createUser)
                .body()
                .as(CreateUserResponse.class);
        userAccessToken = createUserResponse.getAccessToken().replace("Bearer ",""); //присвоение переменной значения accessToken из ответа
    }

    @After
    public void tearDown() { //постусловие - удаление пользователя
        given()
                .auth().oauth2(userAccessToken)
                .delete(URI_DELETE_USER);
    }
}
