package ru.yandex.practicum;

public class CreateOrderResponse {
    private int track;
    public CreateOrderResponse(int track){
        this.track = track;
    }
    public CreateOrderResponse(){}

    public int getTrack() {
        return track;
    }
}
