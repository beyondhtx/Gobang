package com.model;

import java.io.Serializable;

public class Datagram implements Serializable {
    public int type; // 消息类型
    public int x; // 新的落子的x位置
    public int y; // 新的落子的y位置
    public int rx; // 悔棋的x位置
    public int ry; // 悔棋的y位置
    public int color; // 落子颜色
    public String msg; // 额外信息

    public Datagram(int type, int x, int y, int rx, int ry, int color, String msg) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rx = rx;
        this.ry = ry;
        this.color = color;
        this.msg = msg;
    }

    public Datagram(int type){
        this(type, "");
    }

    public Datagram(int type, String msg){
        this(type, 0, 0, 0, 0, 0, msg);
    }

    public Datagram(String msg){
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Datagram{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", color=" + color +
                ", msg='" + msg + '\'' +
                '}';
    }
}
