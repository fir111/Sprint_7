package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

import io.restassured.RestAssured;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.yandex.practicum.ApiPaths.GET_ORDERS_API;
import static ru.yandex.practicum.ApiPaths.CANCEL_ORDER_API;
import static ru.yandex.practicum.ApiPaths.SITE_URL;

public class GetOrderListTest {
    private int trackId;
    @Before
    @DisplayName("Настройка теста")
    @Description("Инициализируем и создаем заказ и API")
    public void setUp() {
        RestAssured.baseURI = SITE_URL;

        Order order = new Order("Ivan", "Ivanov", "Kremlin 1",
                "1", "89997776655", 2, "01.09.2023",
                "Zvonite", List.of("BLACK"));

        Response response = given()
                .header("Content-type", "application/json")
                .and().body(order).when().post(GET_ORDERS_API);
        trackId = response.body().as(CreateOrderResponse.class).getTrack();
    }

    @After
    @DisplayName("Завершение теста")
    @Description("Отменяем заказ")
    public void tearDown(){
        CreateOrderResponse orderRequest = new CreateOrderResponse(trackId);
        given().
                header("Content-type", "application/json")
                .and().body(orderRequest).when().put(CANCEL_ORDER_API);
    }
    @Test
    @DisplayName("Получение списка заказов")
    @Description("Позитивный тест - получаем список заказов и проверяем его структуру")
    public void GetOrderList(){
        Orders orders = given().get(GET_ORDERS_API).body().as(Orders.class);
        assertThat(orders, Matchers.notNullValue(Orders.class));
    }
}