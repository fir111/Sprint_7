package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static ru.yandex.practicum.ApiPaths.*;
import static ru.yandex.practicum.Courier.getCourierId;

import org.junit.Test;


public class LoginTest {
    private Courier courier;
    private Login login;

    @Before
    @DisplayName("Настройка теста")
    @Description("Инициализируем и создаем курьера и API")
    public void setUp() {
        RestAssured.baseURI = SITE_URL;
        courier = new Courier("Vanya2", "password", "Vanya");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(CREATE_COURIER_API);
    }

    @After
    @DisplayName("Завершение теста")
    @Description("Удаляем курьера из системы")
    public void tearDown() {
        login = new Login(courier.getLogin(), courier.getPassword());
        int id = getCourierId(login);
        given()
                .when()
                .delete(DELETE_COURIER_API + id);
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Позитивный тест - тестируем логин ранее созданного курьера")
    public void courierIsAbleToLogin(){
        login = new Login(courier.getLogin(), courier.getPassword());
        given()
                .header("Content-type", "application/json")
                .and().body(login).when().post(LOGIN_COURIER_API)
                .then()
                .assertThat()
                .body("id", greaterThan(0)).and().statusCode(200);
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Позитивный тест - ранее созданный курьер может залогиниться и ответ соответствует API")
    public void courierIsAbleToLoginAndResponseIsInstanceOfLoginResponse(){
        login = new Login(courier.getLogin(), courier.getPassword());
        Response response = given()
                .header("Content-type", "application/json")
                .and().body(login).when().post(LOGIN_COURIER_API);
        assertThat(response.as(LoginResponse.class), notNullValue(LoginResponse.class));
    }

    @Test
    @DisplayName("Нельзя залогиниться не указав логин")
    @Description("Негативный тест - нельзя залогиниться не указав логин")
    public void courierCanNotLoginWithoutLoginField(){
        Login invalidLogin = new Login();
        invalidLogin.setPassword("password");

        given()
                .header("Content-type", "application/json")
                .and().body(invalidLogin).when().post(LOGIN_COURIER_API)
                .then()
                .assertThat()
                .body("message", is("Недостаточно данных для входа")).and().statusCode(400);
    }

    @Test
    @DisplayName("Нельзя залогиниться не указав пароль")
    @Description("Негативный тест - нельзя залогиниться не указав пароль")
    public void courierCanNotLoginWithoutPasswordField(){
        Login invalidLogin = new Login();
        invalidLogin.setLogin(courier.getLogin());

        given()
                .header("Content-type", "application/json")
                .and().body(invalidLogin).when().post(LOGIN_COURIER_API)
                .then()
                .assertThat()
                .body("message", is("Недостаточно данных для входа")).and().statusCode(400);
    }

    @Test
    @DisplayName("Нельзя залогиниться с невалидным логином")
    @Description("Негативный тест - нельзя залогиниться с невалидным логином")
    public void courierCanNotLoginWithIncorrectLoginField(){
        Login invalidLogin = new Login();
        invalidLogin.setLogin("incorrect" + courier.getLogin());
        invalidLogin.setPassword("password");

        given()
                .header("Content-type", "application/json")
                .and().body(invalidLogin).when().post(LOGIN_COURIER_API)
                .then()
                .assertThat()
                .body("message", is("Учетная запись не найдена")).and().statusCode(404);
    }

    @Test
    @DisplayName("Нельзя залогиниться с некорректным паролем")
    @Description("Негативный тест - нельзя залогиниться используя некорректный пароль")
    public void courierCanNotLoginWithIncorrectPasswordField(){
        Login invalidLogin = new Login();
        invalidLogin.setLogin(courier.getLogin());
        invalidLogin.setPassword("incorrect" + courier.getPassword());

        given()
                .header("Content-type", "application/json")
                .and().body(invalidLogin).when().post(LOGIN_COURIER_API)
                .then()
                .assertThat()
                .body("message", is("Учетная запись не найдена")).and().statusCode(404);
    }
}