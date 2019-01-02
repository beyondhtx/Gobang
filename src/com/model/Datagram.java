package com.model;

import java.io.Serializable;

public class Datagram implements Serializable {
    public int type; // ��Ϣ����
    public int x; // �µ����ӵ�xλ��
    public int y; // �µ����ӵ�yλ��
    public int rx; // �����xλ��
    public int ry; // �����yλ��
    public int color; // ������ɫ
    public String msg; // ������Ϣ

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
