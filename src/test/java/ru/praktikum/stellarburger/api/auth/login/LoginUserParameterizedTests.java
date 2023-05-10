package ru.praktikum.stellarburger.api.auth.login;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.stellarburger.api.TestBase;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class LoginUserParameterizedTests extends TestBase {
    private final String email;
    private final String password;

    public LoginUserParameterizedTests(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters(name = "Ошибочная авторизация пользователя. " +
            "Тестовые данные: {0}, {1}")
    public static Object[][] setData() {
        return new Object[][] {
                {"olgaleto@yandex.ru", "qaws111"},
                {"olgazima@yandex.ru", "qaws1234"},
                {"olgaleto@yandex.ru", null},
                {null, "qaws1234"},
        };
    }

    @Test
    @DisplayName("Ошибка при некорректной авторизации пользователя")
    @Step("Отправка запроса авторизации")
    public void loginUserWrongFieldsShouldReturnError() {
        LoginUser loginUser = new LoginUser(email, password);
        Response loginUserResponse = loginUser.getLoginUserResponse(loginUser);
        loginUserResponse.then().statusCode(401)
                .and()
                .assertThat().body("message", equalTo("email or password are incorrect"));
    }
}
