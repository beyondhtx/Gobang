package Server;


/**
 * 封装的棋盘中的点的属性
 * 包含三个
 * 在15*15棋盘中的x，y坐标
 * 标示己方对方的棋子颜色
 */
public class Point {
    private int x;
    private int y;
    private int color;

    public Point() {
        this(0, 0, PointColor.WHITE.ordinal());
    }

    public Point(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Point(int x, int y) {
        this(x, y, PointColor.WHITE.ordinal());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return x + ":" + y + ":" + color;
    }
}
