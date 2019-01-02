package com.model;

import java.io.Serializable;

public class UserData implements Serializable {
    public String username;
    public int win;
    public int lose;
    public int draw;
    public UserData(){

    }

    public UserData(String username, int win, int lose, int draw) {
        this.username = username;
        this.win = win;
        this.lose = lose;
        this.draw = draw;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", win=" + win +
                ", lose=" + lose +
                ", draw=" + draw +
                '}';
    }
}
