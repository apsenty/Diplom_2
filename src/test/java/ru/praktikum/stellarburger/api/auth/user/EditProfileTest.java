package ru.praktikum.stellarburger.api.auth.user;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.praktikum.stellarburger.api.TestBase;
import ru.praktikum.stellarburger.api.auth.login.LoginUser;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

public class EditProfileTest extends TestBase {
    final String URI_EDIT_PROFILE = "/api/auth/user";

    @Step("Отправка запроса изменения профиля клиента")
    public Response sendRequestEditProfile(String email, String password, String name) {
        EditProfile editProfile = new EditProfile(email, password, name);
        return given()
                .header("Content-Type", "application/json")
                .auth().oauth2(super.userAccessToken)
                .and()
                .body(editProfile)
                .when()
                .patch(URI_EDIT_PROFILE);
    }

    @Step("Отправка запроса авторизации")
    public Response sendRequestLoginUser(String email, String password) {
        LoginUser loginUser = new LoginUser(email, password);
        return loginUser.getLoginUserResponse(loginUser);
    }

    @Test
    @DisplayName("Успешное изменение профиля")
    public void editProfilePositiveCheck(){
        Response editUserResponse = sendRequestEditProfile("olgazima@yandex.ru", "qaws111", "Olga");
        editUserResponse.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true))
                .and().assertThat().body("user.name", equalTo("Olga"))
                .and().assertThat().body("user.email", equalTo("olgazima@yandex.ru"));
    }

    @Test
    @DisplayName("Проверка авторизации с новым паролем")
    public void editProfileNewPasswordLoginShouldReturnOk() {
        //шаг 1 - изменение профиля
        Response editUserResponse = sendRequestEditProfile(null, "qaws111", null);
        editUserResponse.then().statusCode(200);
        //шаг 2 - авторизация с новым паролем
        Response loginUserResponse = sendRequestLoginUser("olgaleto@yandex.ru", "qaws111");
        loginUserResponse.then().statusCode(200)
                .and()
                .assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка при авторизации со старым паролем")
    public void editProfileLoginWithOldPasswordShouldReturnError() {
        //шаг 1 - изменение профиля
        Response editUserResponse = sendRequestEditProfile(null, "qaws111", null);
        editUserResponse.then().statusCode(200);
        //шаг 2 - авторизация со старым паролем
        Response loginUserResponse = sendRequestLoginUser("olgaleto@yandex.ru", "qaws1234");
        loginUserResponse.then().statusCode(401)
                .and()
                .assertThat().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Изменение профиля без авторизации")
    @Step("Отправка запроса изменения профиля без авторизации")
    public void editProfileWithoutTokenShouldReturnError() {
        EditProfile editProfile = new EditProfile("olgazima@yandex.ru", "qaws111", "Olga");
        Response editProfileResponse = given()
                .header("Content-Type", "application/json")
                .and()
                .body(editProfile)
                .when()
                .patch(URI_EDIT_PROFILE);
        editProfileResponse.then().statusCode(401)
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }
}