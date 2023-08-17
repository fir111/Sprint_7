package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.greaterThan;
import static ru.yandex.practicum.ApiPaths.GET_ORDERS_API;
import static ru.yandex.practicum.ApiPaths.SITE_URL;

import java.util.List;

@RunWith(Parameterized.class)
public class OrderTest {
    private static final List<String> bothColor = List.of("BLACK", "GREY");
    private static final List<String> blackColor = List.of("BLACK");
    private static final List<String> emptyColorList = List.of();
    private final List<String> color;
    private final int expectedStatusCode;

    public OrderTest(List<String> color, int expectedStatusCode) {
        this.color = color;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters(name = "Order should be available {1}")
    public static Object data() {
        return new Object[][] {
                { bothColor, 201 },
                { blackColor, 201 },
                { emptyColorList, 201 }
        };
    }

    @Before
    @DisplayName("Настройка теста")
    @Description("Инициализируем API")
    public void setUp() {
        RestAssured.baseURI = SITE_URL;
    }

    @Test
    @DisplayName("Проверяем создание заказа")
    @Description("Позитивный тест - создаем заказ с заданными параметрами")
    public void createOrder(){
        Order order = new Order("Ivan", "Ivanov", "Kremlin 1",
                "2", "89997776655", 2, "01.09.2023",
                "Zvonite", color);
        given().header("Content-type", "application/json")
                .and().body(order).when().post(GET_ORDERS_API)
                .then()
                .assertThat().body("track", greaterThan(0)).and().statusCode(expectedStatusCode);
    }

}