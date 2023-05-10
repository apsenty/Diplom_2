package ru.praktikum.stellarburger.api.orders;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.praktikum.stellarburger.api.TestBase;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderTest extends TestBase {
    final String URI_CREATE_ORDER = "/api/orders";

    //отдельного теста на возможность создания заказа с ингредиентами нет,
    //т.к. это проверяется в тестах создания заказа с авторизацией и без нее.
    @Step("Отправка запроса на создание заказа")
    public Response sendRequestCreateOrder(List<String> ingredients) {
        CreateOrder createOrder = new CreateOrder(ingredients);
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(createOrder)
                .when()
                .post(URI_CREATE_ORDER);
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами с авторизацией пользователя")
    @Step("Отправка запроса на создание заказа авторизованным пользователем")
    public void createOrderWithTokenShouldReturnOk() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");//bun
        ingredients.add("61c0c5a71d1f82001bdaaa6f");//main
        ingredients.add("61c0c5a71d1f82001bdaaa72");//sauce
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response createOrderResponse = given()
                .header("Content-Type", "application/json")
                .auth().oauth2(super.userAccessToken)
                .and()
                .body(createOrder)
                .when()
                .post("/api/orders");
        createOrderResponse.then().statusCode(200)
                .and()
                .assertThat().body("order.owner.name", equalTo("Apsenty"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами без авторизации пользователя")
    public void createOrderWithoutTokenShouldReturnOk() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");//bun
        ingredients.add("61c0c5a71d1f82001bdaaa6f");//main
        ingredients.add("61c0c5a71d1f82001bdaaa72");//sauce
        Response createOrderResponse = sendRequestCreateOrder(ingredients);
        createOrderResponse.then().statusCode(200)
                .and()
                .assertThat().body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientShouldReturnError() {
        List<String> ingredients = new ArrayList<>();
        Response createOrderResponse = sendRequestCreateOrder(ingredients);
        createOrderResponse.then().statusCode(400)
                .and()
                .assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWrongHashIngredientShouldReturnError() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("12345");
        Response createOrderResponse = sendRequestCreateOrder(ingredients);
        createOrderResponse.then().statusCode(500);
    }
}