package ru.praktikum.stellarburger.api.auth.register;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import ru.praktikum.stellarburger.api.TestBase;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class CreateUserTest extends TestBase {
    public String userAccessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Step("Отправка запроса на создание пользователя")
    public Response sendRequestCreateUser(String email, String password, String name) {
        CreateUser createUser = new CreateUser(email, password, name);
        return createUser.getCreateUserResponse(createUser);
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void createUserPositiveCheck() {
        Response createUserResponse = sendRequestCreateUser("olgaleto@yandex.ru",
                "qaws1234", "Apsenty");
        createUserResponse.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));
        CreateUserResponse response = createUserResponse.as(CreateUserResponse.class);
        userAccessToken = response.getAccessToken().replace("Bearer ","");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createUserCreateTwoEqualUserShouldReturnError() {
        //шаг 1 - создание первого пользователя
        Response createUserResponse = sendRequestCreateUser("olgaleto@yandex.ru",
                "qaws1234", "Apsenty");
        createUserResponse.then().statusCode(200);
        CreateUserResponse response = createUserResponse.as(CreateUserResponse.class);
        userAccessToken = response.getAccessToken().replace("Bearer ","");
        //шаг 2 - создание такого же пользователя
        Response createSecondUserResponse = sendRequestCreateUser("olgaleto@yandex.ru",
                "qaws1234", "Apsenty");
        createSecondUserResponse.then().statusCode(403)
                .and()
                .assertThat().body("message", equalTo("User already exists"));
    }


    @After
    public void tearDown() {
        /* LoginUser loginUser = new LoginUser("olgaleto@yandex.ru", "qaws1234");
        LoginUserResponse loginUserResponse = loginUser.getLoginUserResponse(loginUser)
                .body().as(LoginUserResponse.class);
        userAccessToken = loginUserResponse.getAccessToken().replace("Bearer ",""); */
        given()
                .auth().oauth2(userAccessToken)
                .delete("/api/auth/user");
    }
}