package ru.praktikum.stellarburger.api.auth.register;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.stellarburger.api.TestBase;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParameterizedTest extends TestBase {
    private final String email;
    private final String password;
    private final String name;

    public CreateUserParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "Регистрация пользователя без обязательного поля. " +
            "Тестовые данные: {0} {1} {2}")
    public static Object[][] setData() {
        return new Object[][] {
                {null, "qaws1234", "apsenty"},
                {"olgaleto@yandex.ru", null, "apsenty"},
                {"olgaleto@yandex.ru", "qaws1234", null},
        };
    }
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Регистрация пользователя без обязательного поля")
    @Step("Отправка запроса создания пользователя")
    public void createUserCreateWithoutRequiredFieldShouldReturnError() {
        CreateUser createUser = new CreateUser(email, password, name);
        Response createUserResponse = createUser.getCreateUserResponse(createUser);
        createUserResponse.then().statusCode(403)
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {}
}
