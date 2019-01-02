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
        int total_num = -1; // ��ֱ����������ɫ���ӵ���������ʼ��Ϊ-1����Ϊp������������Σ�������ǰ��ȥһ��
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
        int total_num = -1; //ˮƽ����������ɫ���ӵ���������ʼ��Ϊ-1����Ϊp������������Σ�������ǰ��ȥһ��
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
        // �����б�ܷ��򣨼����ϵ����£��Ƿ���������������
        int total_num = -1; //ˮƽ����������ɫ���ӵ���������ʼ��Ϊ-1����Ϊp������������Σ�������ǰ��ȥһ��
        for(int delta = 0; delta >= -4; delta--){
            // ������
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
            // ������
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
        // ��鷴б�ܷ��򣨼����ϵ����£��Ƿ���������������
        int total_num = -1; //ˮƽ����������ɫ���ӵ���������ʼ��Ϊ-1����Ϊp������������Σ�������ǰ��ȥһ��
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
        System.out.println("�µ�һ�ֿ�ʼ...");
        System.out.println(String.format("��ʼ��Ϸ��˫�����: %s (ִ��)vs %s (ִ��)", black_user, white_user));
        int cur_color = PointColor.BLACK.ordinal();
        String cur_user;
        Socket cur_sock = null;
        String oppo_user;
        Socket oppo_sock = null;
        boolean complete = false; // �ֳ�ʤ�������ֽ���
        try {
            while (!complete) {
                // ÿһ��ѭ�����һ����Ϣ�Ľ��պͷ���
                Thread.sleep(100);
                if(cur_color == PointColor.BLACK.ordinal()){
                    // ��ǰ�ڵȴ���ɫ��ҷ���Ϣ
                    cur_user = black_user;
                    cur_sock = black;
                    oppo_user = white_user;
                    oppo_sock = white;
                }
                else{
                    // ��ǰ�ڵȴ���ɫ��ҷ���Ϣ
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
                            System.out.println("����λ���Ѿ�����");
                            complete = true;
                        }
                        data[recv_data.x][recv_data.y] = cur_color;
                        boolean full = true; // ��ǰ�����Ƿ�����
                        for(int i = 0; i < Utils.BOARD_SIZE; i++){
                            for(int j = 0; j < Utils.BOARD_SIZE; j++){
                                if(data[i][j] == 0) {
                                    full = false;
                                    break;
                                }
                            }
                        }
                        if(full){
                            // ����������˫������
                            DatabaseUtil.update(cur_user, "draw");
                            DatabaseUtil.update(oppo_user, "draw");
                            Utils.sendMsg(cur_sock, new Datagram(MsgType.DRAW.ordinal()), Server.out_map);
                            Utils.sendMsg(oppo_sock, new Datagram(MsgType.DRAW.ordinal()), Server.out_map);
                            complete = true;
                        }
                        else{
                            // ����δ��
                            if(checkWin(new Point(recv_data.x, recv_data.y, recv_data.color))){
                                // ��ǰ��һ�ʤ����Ϸ����
                                DatabaseUtil.update(cur_user, "win");
                                DatabaseUtil.update(oppo_user, "lose");
                                Utils.sendMsg(cur_sock, new Datagram(MsgType.WIN.ordinal(),"normal"), Server.out_map);
                                Utils.sendMsg(oppo_sock, new Datagram(MsgType.LOSE.ordinal(), recv_data.x, recv_data.y, 0, 0, cur_color,""), Server.out_map);
                                complete = true;
                            }
                            else{
                                // ��Ϸ��δ���������µ�ǰ����Ϊ�ϲ��壬����֮�����
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
                            case "ͬ��":
                                // cur_userͬ�������ֵ�oppo_user����һ��
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
                                // �����������壬����пյľ�ɶ������
                                break;
                            case "��ͬ��":
                                // cur_user��ͬ�������ֵ�oppo_user����һ��
                                Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                                break;
                            default:
                                // cur_user���ͻ�������
                                if(pre_black != null && pre_white != null) {
                                    recv_data.x = pre_black.getX();
                                    recv_data.y = pre_black.getY();
                                    recv_data.rx = pre_white.getX();
                                    recv_data.ry = pre_white.getY();
                                    Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                                }
                                else{
                                    // ��ɫ���䣬ɶ������
                                    cur_color = cur_color == PointColor.BLACK.ordinal()? PointColor.WHITE.ordinal() : PointColor.BLACK.ordinal();
                                    Utils.sendMsg(cur_sock, new Datagram(MsgType.ERROR.ordinal(), "������������"), Server.out_map);
                                }
                        }
                        break;
                    case SURRENDER:
                        // cur_user���䣬�������ݿ�
                        DatabaseUtil.update(cur_user, "lose");
                        DatabaseUtil.update(oppo_user, "win");
                        Utils.sendMsg(oppo_sock, recv_data, Server.out_map);
                        Utils.sendMsg(cur_sock, new Datagram(MsgType.ERROR.ordinal()), Server.out_map);
                        complete = true;
                        break;
                    default:
                        System.out.println("���ݰ���������");
                }
                // �ı䵱ǰ��������Ϣ�������ɫ
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
                System.out.println("��Ϸ����...");
                System.out.println(String.format("������Ϸ��˫�����: %s (ִ��)vs %s (ִ��)", black_user, white_user));
            }
        }
    }
}
