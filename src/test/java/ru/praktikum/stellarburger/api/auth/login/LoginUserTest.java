package ru.praktikum.stellarburger.api.auth.login;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.praktikum.stellarburger.api.TestBase;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends TestBase {
    @Step("Отправка запроса авторзации")
    public Response sendRequestLoginUser(String login, String password) {
        LoginUser loginUser = new LoginUser(login, password);
        return loginUser.getLoginUserResponse(loginUser);
    }

    @Test
    @DisplayName("Авторизация существующим пользователем")
    public void loginUserPositiveCheck() {
        Response loginUserResponse = sendRequestLoginUser("olgaleto@yandex.ru", "qaws1234");
        loginUserResponse.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));
    }
}