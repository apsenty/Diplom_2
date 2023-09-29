package ru.praktikum.stellarburger.api.orders;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.praktikum.stellarburger.api.TestBase;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest extends TestBase {
    final String URI_GET_ORDER = "/api/orders";

    @Step("Отправка запроса на создание заказа")
    public Response sendRequestCreateOrder() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");//bun
        ingredients.add("61c0c5a71d1f82001bdaaa6f");//main
        ingredients.add("61c0c5a71d1f82001bdaaa72");//sauce
        CreateOrder createOrder = new CreateOrder(ingredients);
        return given()
                .header("Content-Type", "application/json")
                .auth().oauth2(super.userAccessToken)
                .and()
                .body(createOrder)
                .when()
                .post("/api/orders");
    }

    @Step("Отправка запроса списка заказов с авторизацией")
    public Response sendRequestGetOrderListWithToken() {
        return given()
                .auth().oauth2(super.userAccessToken)
                .get(URI_GET_ORDER);
    }

    @Test
    @DisplayName("Получение списка заказов пользователя - с авторизацией")
    public void getOrderListWithTokenShouldReturnOk() {
        //шаг 1 - отправляем запрос на создание заказа авторизованным пользователем
        Response createOrderResponse = sendRequestCreateOrder();
        createOrderResponse.then().statusCode(200);
        //шаг 2 - запрос списка заказов пользователя
        Response orderListResponse = sendRequestGetOrderListWithToken();
        orderListResponse.then().statusCode(200)
                .and().assertThat().body("success", equalTo(true))
                .and().assertThat().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов пользователя - без авторизации")
    @Step("Отправка запроса списка заказов без авторизации")
    public void getOrderListWithoutTokenShouldReturnError() {
        given()
                .get(URI_GET_ORDER).then().statusCode(401)
                .and()
                .assertThat().body("message", equalTo("You should be authorised"));
    }
}