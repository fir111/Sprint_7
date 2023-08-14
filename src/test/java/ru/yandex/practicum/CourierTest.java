package ru.yandex.practicum;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.is;

import static ru.yandex.practicum.Courier.getCourierId;
import static ru.yandex.practicum.ApiPaths.CREATE_COURIER_API;
import static ru.yandex.practicum.ApiPaths.SITE_URL;
import static ru.yandex.practicum.ApiPaths.DELETE_COURIER_API;

import io.qameta.allure.junit4.DisplayName;

public class CourierTest {
    private Courier courier;

    @Before
    @DisplayName("Настройка теста")
    @Description("Инициализируем курьера и API")
    public void setUp() {
        RestAssured.baseURI = SITE_URL;
        courier = new Courier("Vanya", "password", "Vanya");
    }
    @After
    @DisplayName("Завершение теста")
    @Description("Удаляем курьера из системы")
    public void tearDown(){
        Login login = new Login(courier.getLogin(), courier.getPassword());
        int id = getCourierId(login);
        given()
                .when()
                .delete(DELETE_COURIER_API + id);
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Позитивный тест - тестируем создание ранее инициализированного курьера")
    public void createCourier(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(CREATE_COURIER_API)
                .then()
                .assertThat().body("ok", is(true)).and().statusCode(201);
    }

    @Test
    @DisplayName("Повторное создание курьера")
    @Description("Негативный тест - нельзя создать ранее созданного курьера")
    public void canNotCreateTheCourierTwice(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier).when().post(CREATE_COURIER_API);

        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(CREATE_COURIER_API)
                .then()
                .assertThat()
                .body("message", is("Этот логин уже используется. Попробуйте другой."))
                .and().statusCode(409);
    }
    @Test
    @DisplayName("Создание курьера без поля login")
    @Description("Негативный тест - нельзя создать курьера без поля login")
    public void canNotCreateCourierWithoutLogin(){
        Courier invalidCourier = new Courier();
        invalidCourier.setPassword("password");
        invalidCourier.setFirstName("Vasya");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(invalidCourier).when().post(CREATE_COURIER_API).then()
                .assertThat()
                .body("message", is("Недостаточно данных для создания учетной записи"))
                .and().statusCode(400);

    }
    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Негативный тест - нельзя создать курьера без поля password")
    public void canNotCreateCourierWithoutPassword(){
        Courier invalidCourier = new Courier();
        invalidCourier.setLogin("Vasya");
        invalidCourier.setFirstName("Vasya");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(invalidCourier).when().post(CREATE_COURIER_API).then()
                .assertThat()
                .body("message", is("Недостаточно данных для создания учетной записи"))
                .and().statusCode(400);

    }

    @Test
    @DisplayName("Создание курьера с неуникальным логином")
    @Description("Негативный тест - нельзя создать курьера с существующим в системе полем login")
    public void canNotCreateCourierWithExistingLogin(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier).when().post(CREATE_COURIER_API);

        Courier invalidCourier = new Courier();
        invalidCourier.setLogin(courier.getLogin());
        invalidCourier.setPassword("another_password");
        invalidCourier.setFirstName("Vasya");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(invalidCourier).when().post(CREATE_COURIER_API).then()
                .assertThat()
                .body("message", is("Этот логин уже используется. Попробуйте другой."))
                .and().statusCode(409);
    }
}