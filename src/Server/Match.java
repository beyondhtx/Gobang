package Server;

import com.model.Datagram;

import java.net.Socket;

public class Match extends Thread{
    private int[][] data;
    private String black_user;
    private String white_user;
    private Socket black;
    private Socket white;
    private Point pre_black;
    private Point pre_white;

    public Match(String black_user, String white_user, Socket black, Socket white){
        data = new int[Utils.BOARD_SIZE+1][Utils.BOARD_SIZE+1];
        for(int i = 0; i < Utils.BOARD_SIZE+1; i++){
            for(int j = 0; j < Utils.BOARD_SIZE+1; j++){
                data[i][j] = 0;
            }
        }
        this.black_user = black_user;
        this.white_user = white_user;
        this.black = black;
        this.white = white;
        this.pre_black = null;
        this.pre_white = null;
    }

    private boolean checkVertical(Point p){
        int total_num = -1; // 竖直方向连续颜色棋子的数量，初始化为-1是因为p本身计算了两次，这里提前减去一次
        for(int y = p.getY(); y >= p.getY() - 4 && y >= 1; y--){
            if(data[p.getX()][y] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        for(int y = p.getY(); y <= p.getY() + 4 && y <= Utils.BOARD_SIZE; y++){
            if(data[p.getX()][y] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        return total_num >= 5;
    }

    private boolean checkHorizon(Point p){
        int total_num = -1; //水平方向连续颜色棋子的数量，初始化为-1是因为p本身计算了两次，这里提前减去一次
        for(int x = p.getX(); x >= p.getX() - 4 && x >= 1; x--){
            if(data[x][p.getY()] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        for(int x = p.getX(); x >= p.getX() + 4 && x <= Utils.BOARD_SIZE; x++){
            if(data[x][p.getY()] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        return total_num >= 5;
    }

    private boolean checkForwardSlash(Point p){
        // 检查正斜杠方向（即右上到左下）是否有五子以上连珠
        int total_num = -1; //水平方向连续颜色棋子的数量，初始化为-1是因为p本身计算了两次，这里提前减去一次
        for(int delta = 0; delta >= -4; delta--){
            // 先右上
            if(p.getX() - delta > Utils.BOARD_SIZE || p.getY() + delta < 1){
                break;
            }
            if(data[p.getX() - delta][p.getY() + delta] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        for(int delta = 0; delta <= 4; delta++){
            // 再左下
            if(p.getX() - delta < 1 || p.getY() + delta > Utils.BOARD_SIZE){
                break;
            }
            if(data[p.getX() - delta][p.getY() + delta] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        return total_num >= 5;
    }

    private boolean checkBackwardSlash(Point p){
        // 检查反斜杠方向（即左上到右下）是否有五子以上连珠
        int total_num = -1; //水平方向连续颜色棋子的数量，初始化为-1是因为p本身计算了两次，这里提前减去一次
        for(int delta = 0; delta >= -4; delta--){
            if(p.getX() + delta < 1 || p.getY() + delta < 1){
                break;
            }
            if(data[p.getX() + delta][p.getY() + delta] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        for(int delta = 0; delta <= 4; delta++){
            if(p.getX() + delta > Utils.BOARD_SIZE || p.getY() + delta > Utils.BOARD_SIZE){
                break;
            }
            if(data[p.getX() + delta][p.getY() + delta] == p.getColor()){
                total_num++;
            }
            else{
                break;
            }
        }
        return total_num >= 5;
    }

    private boolean checkWin(Point p){
        return checkVertical(p) || checkHorizon(p) || checkForwardSlash(p) || checkBackwardSlash(p);
    }

    @Override
    public void run() {
        System.out.println("新的一局开始...");
        System.out.println(String.format("开始游戏的双方玩家: %s (执黑)vs %s (执白)", black_user, white_user));
        int cur_color = PointColor.BLACK.ordinal();
        String cur_user;
        Socket cur_sock = null;
        String oppo_user;
        Socket oppo_sock = null;
        boolean complete = false; // 分出胜负，本局结束
        try {
            while (!complete) {
                // 每一次循环完成一次消息的接收和发送
                Thread.sleep(100);
                if(cur_color == PointColor.BLACK.ordinal()){
                    // 当前在等待黑色玩家发消息
                    cur_user = black_user;
                    cur_sock = black;
                    oppo_user = white_user;
                    oppo_sock = white;
                }
                else{
                    // 当前在等待白色玩家发消息
                    oppo_user = black_user;
                    oppo_sock = black;
                    cur_user = white_user;
                    cur_sock = white;
                }
                Datagram recv_data = Utils.recvMsg(cur_sock, Server.in_map);
                System.out.println(recv_data);
                switch(MsgType.values()[recv_data.type]){
                    case NORMAL:
                        if(data[recv_data.x][recv_data.y] != 0){
                            System.out.println("落子位置已经有棋");
                            complete = true;
                        }
                        data[recv_data.x][recv_data.y] = cur_color;
                        boolean full = true; // 当前棋盘是否已满
                        for(int i = 0; i < Utils.BOARD_SIZE; i++){
                            for(int j = 0; j < Utils.BOARD_SIZE; j++){
                                if(data[i][j] == 0) {
                                    full = false;
                                    break;
                                }
                            }
                        }
                        if(full){
                            // 棋盘已满，双方和棋
                            DatabaseUtil.update(cur_user, "draw");
                            DatabaseUtil.update(oppo_user, "draw");
                            Utils.sendMsg(cur_sock, new Datagram(MsgType.DRAW.ordinal()), Server.out_map);
                            Utils.sendMsg(oppo_sock, new Datagram(MsgType.DRAW.ordinal()), Server.out_map);
                            complete = true;
                        }
                        else{
                            // 棋盘未满
                            if(checkWin(new Point(recv_data.x, recv_data.y, recv_data.color))){
                                // 当前玩家获胜，游戏结束
                                DatabaseUtil.update(cur_user, "win");
                                DatabaseUtil.update(oppo_user, "lose");
                                Utils.sendMsg(cur_sock, new Datagram(MsgType.WIN.ordinal(),"normal"), Server.out_map);
                                Utils.sendMsg(oppo_sock, new Datagram(MsgType.LOSE.ordinal(), recv_data.x, recv_data.y, 0, 0, cur_color,""), Server.out_map);
                                complete = true;
                            }
                            else{
                                // 游戏尚未结束，更新当前步骤为上步棋，便于之后悔棋
                                Utils.sendMsg(oppo_sock, new Datagram(MsgType.NORMAL.ordinal(), recv_data.x, recv_data.y, 0, 0, cur_color,""), Server.out_map);
                                if(cur_color == PointColor.BLACK.ordinal()){
                                    if(pre_black == null)
                                        pre_black = new Point(recv_data.x, recv_data.y, PointColor.BLACK.ordinal());
                                    else{
                                        pre_black.setX(recv_data.x);
                                        pre_black.setY(recv_data.y);
                                    }
                                }
                                else if(cur_color == PointColor.WHITE.ordinal()){
                                    if(pre_white == null)
                                        pre_white = new Point(recv_data.x, recv_data.y, PointColor.WHITE.ordinal());
                                    else{
                                        pre_white.setX(recv_data.x);
                                        pre_white.setY(recv_data.y);
                                    }
                                }
                                else{
                                    System.out.println("In Match.java, case NORMAL, Should not reach here!");
                                }
                            }
                        }
                        break;
                    case REGRET:
                        switch (recv_data.msg){
                            case "同意":
                                // cur_user同意回退棋局到oppo_user的上一步
                                if(pre_white != null && pre_black != null){
                                    data[pre_white.getX()][pre_white.getY()] = 0;
                                    data[pre_black.getX()][pre_black.getY()] = 0;
                                    recv_data.x = pre_black.getX();
                                    recv_data.y = pre_black.getY();
                                    recv_data.rx = pre_white.getX();
                                    recv_data.ry = pre_white.getY();
                                    pre_white = null;
                                    pre_black = null;
                                    Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                                }
                                else{
                                    System.out.println("In Match.java, case REGRET, Should not reach here!");
                                }
                                // 不能连续悔棋，如果有空的就啥都不干
                                break;
                            case "不同意":
                                // cur_user不同意回退棋局到oppo_user的上一步
                                Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                                break;
                            default:
                                // cur_user发送悔棋请求
                                if(pre_black != null && pre_white != null) {
                                    recv_data.x = pre_black.getX();
                                    recv_data.y = pre_black.getY();
                                    recv_data.rx = pre_white.getX();
                                    recv_data.ry = pre_white.getY();
                                    Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                                }
                                else{
                                    // 颜色不变，啥都不干
                                    cur_color = cur_color == PointColor.BLACK.ordinal()? PointColor.WHITE.ordinal() : PointColor.BLACK.ordinal();
                                    Utils.sendMsg(cur_sock, new Datagram(MsgType.ERROR.ordinal(), "不能连续悔棋"), Server.out_map);
                                }
                        }
                        break;
                    case SURRENDER:
                        // cur_user认输，更新数据库
                        DatabaseUtil.update(cur_user, "lose");
                        DatabaseUtil.update(oppo_user, "win");
                        Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                        Utils.sendMsg(cur_sock, new Datagram(MsgType.ERROR.ordinal()), Server.out_map);
                        complete = true;
                        break;
                    default:
                        System.out.println("数据包接收有误");
                }
                // 改变当前待接收信息的玩家颜色
                cur_color = cur_color == PointColor.BLACK.ordinal()? PointColor.WHITE.ordinal() : PointColor.BLACK.ordinal();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                white.close();
                black.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                System.out.println("游戏结束...");
                System.out.println(String.format("结束游戏的双方玩家: %s (执黑)vs %s (执白)", black_user, white_user));
            }
        }
    }
}
