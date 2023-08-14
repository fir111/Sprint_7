package ru.yandex.practicum;

public class LoginResponse {
    private int id;
    public LoginResponse(int id){
        this.id = id;
    }
    public LoginResponse(){}

    public int getId() {
        return id;
    }
}
