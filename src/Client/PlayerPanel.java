package Client;

import com.model.Datagram;
import com.model.UserData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class PlayerPanel extends JPanel implements MouseListener, ActionListener {
    static{
        board = new ImageIcon("drawable/board.png");
        icon_white = new ImageIcon("drawable/white.png"); // ָ��������Դ
        icon_black = new ImageIcon("drawable/black.png"); // ָ��������Դ
    }
    private static final ImageIcon board;
    private static final ImageIcon icon_white;
    private static final ImageIcon icon_black;
    private java.util.List<Point> points;
    private int[][] datas = new int[Utils.BOARD_SIZE+1][Utils.BOARD_SIZE+1];
    private int state_color;
    private boolean is_my_turn;
    private Socket sock;
    private Thread t;
    private BlankPanel help_panel, regret_panel, surrender_panel;
    private JLabel label_messagge;
    private ImageButton btnOK, btnRegret, btnSurrender, btnRegretOK, btnRegretNO;
    private boolean is_panel;
    private boolean is_over;
    private JLabel label_win, label_lose, label_draw, label_username, label_oppo_win, label_oppo_lose, label_oppo_draw, label_oppo_username, label_regret_message;
    private String username, oppo_username;
    private int win, lose, draw, oppo_win, oppo_lose, oppo_draw;
    private Thread regret_t;

    public PlayerPanel(int state_color, Socket sock, UserData cur, UserData oppo) {
        this.state_color = state_color;
        this.sock = sock;
        this.is_panel = true;
        this.is_over = false;
        this.username = cur.username;
        this.oppo_username = oppo.username;
        this.win = cur.win;
        this.oppo_win = oppo.win;
        this.lose = cur.lose;
        this.oppo_lose = oppo.lose;
        this.draw = cur.draw;
        this.oppo_draw = oppo.draw;

        if(state_color == PointColor.BLACK.ordinal()){
            is_my_turn = true;
        }
        else{
            is_my_turn = false;
        }
        points = new ArrayList<>();
        addMouseListener(this);
        for (int i = 0; i < datas.length; i++) {
            for (int j = 0; j < datas[0].length; j++) {
                datas[i][j] = 0;
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("�Կͻ�����ʽ������");
        t = new Thread(
                // �û���¼/ע��/��ȡ������Ϣ�߳�
                () -> {
                    try {
                        boolean complete = false;
                        while(!complete) {
                            Datagram data = Utils.recvMsg(sock, Client.in_map);
                            switch (MsgType.values()[data.type]) {
                                case SURRENDER:
                                    // �Է�����
                                    label_messagge.setText("�Է����䣡��ʤ���ˡ�");
                                    this.add(help_panel);
                                    updateUI();
                                    is_panel = true;
                                    is_over = true;
                                    win++;
                                    complete = true;
                                    break;
                                case REGRET:
                                    if(data.msg.length() == 0) {
                                        // �Է��������
                                        this.add(regret_panel);
                                        updateUI();
                                        is_panel = true;
                                        regret_t = new Thread(
                                                () -> {
                                                    datas[data.x][data.y] = 0;
                                                    datas[data.rx][data.ry] = 0;
                                                    points.removeIf(s -> (s.getX() == data.x && s.getY() == data.y) || (s.getX() == data.rx && s.getY() == data.ry));
                                                }
                                        );
                                    }
                                    else if(data.msg.equals("ͬ��")){
                                        // �Է�ͬ���ҷ�����
                                        label_messagge.setText("�Է�ͬ�������Ļ�������");
                                        this.add(help_panel);
                                        updateUI();
                                        is_panel = true;
                                        is_my_turn = true;
                                        datas[data.x][data.y] = 0;
                                        datas[data.rx][data.ry] = 0;
                                        points.removeIf(s -> (s.getX() == data.x && s.getY() == data.y) || (s.getX() == data.rx && s.getY() == data.ry));
                                    }
                                    else{
                                        // �Է���ͬ���ҷ�����
                                        label_messagge.setText("�Է��ܾ������Ļ�������");
                                        this.add(help_panel);
                                        updateUI();
                                        is_panel = true;
                                        is_my_turn = true;
                                    }
                                    break;
                                case NORMAL:
                                    points.add(new Point(data.x, data.y, data.color));
                                    datas[data.x][data.y] = 1;
                                    updateUI();
                                    is_my_turn = true;
                                    break;
                                case WIN:
                                    label_messagge.setText("��Ϸ��������ʤ���ˡ�");
                                    this.add(help_panel);
                                    updateUI();
                                    is_panel = true;
                                    is_over = true;
                                    win++;
                                    complete = true;
                                    break;
                                case LOSE:
                                    points.add(new Point(data.x, data.y, data.color));
                                    datas[data.x][data.y] = 1;
                                    updateUI();
                                    lose++;
                                    label_messagge.setText("��Ϸ��������ʧ���ˡ�");
                                    this.add(help_panel);
                                    updateUI();
                                    is_panel = true;
                                    is_over = true;
                                    complete = true;
                                    break;
                                case DRAW:
                                    label_messagge.setText("��Ϸ������˫��ƽ�֡�");
                                    this.add(help_panel);
                                    updateUI();
                                    draw++;
                                    is_panel = true;
                                    is_over = true;
                                    complete = true;
                                    break;
                                case ERROR:
                                    if(data.msg.length() == 0) {
                                        complete = true;
                                    }
                                    else if(data.msg.equals("������������")){
                                        is_my_turn = true;
                                        is_panel = true;
                                        label_messagge.setText("�������������������ʱ���壡");
                                        this.add(help_panel);
                                        updateUI();
                                    }
                                    break;
                                case LOGIN:
                                    break;
                                case REGISTER:
                                    break;
                                case PROFILE:
                                    break;
                                case NEWMATCH:
                                    break;
                            }
                        }
                    }
                    catch (IOException e){
                        // donothing
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
        );
        t.start();
        Font f = new Font("��Բ", Font.PLAIN, 15);
        Font f_label = new Font("��Բ", Font.PLAIN, 17);
        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.black);
        UIManager.put("Button.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("List.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("Table.font", f);
        label_username = new JLabel("    ��ǰ��ң� " + username);
        label_win = new JLabel("    ʤ������ " + win);
        label_lose = new JLabel("    �������� " + lose);
        label_draw = new JLabel("    ƽ������ " + draw);

        label_oppo_username = new JLabel("    �Է���ң� " + oppo_username);
        label_oppo_win = new JLabel("    ʤ������ " + oppo_win);
        label_oppo_lose = new JLabel("    �������� " + oppo_lose);
        label_oppo_draw = new JLabel("    ƽ������ " + oppo_draw);

        help_panel = new BlankPanel(200);
        regret_panel = new BlankPanel(200);
        surrender_panel = new BlankPanel(200);
        label_messagge = new JLabel();
        label_regret_message = new JLabel();
        btnOK = new ImageButton("ȷ��");
        btnSurrender = new ImageButton("����");
        btnRegret = new ImageButton("����");
        btnRegretOK = new ImageButton("ͬ��");
        btnRegretNO = new ImageButton("��ͬ��");
        label_username.setBounds(760, 0, 200, 30);
        label_win.setBounds(760, 30, 200, 30);
        label_lose.setBounds(760, 60, 200, 30);
        label_draw.setBounds(760, 90, 200, 30);

        label_oppo_username.setBounds(760, 350, 200, 30);
        label_oppo_win.setBounds(760, 380, 200, 30);
        label_oppo_lose.setBounds(760, 410, 200, 30);
        label_oppo_draw.setBounds(760, 440, 200, 30);

        help_panel.setBounds(250, 250, 240, 240);
        help_panel.setLayout(null);

        regret_panel.setBounds(250, 250, 240, 240);
        regret_panel.setLayout(null);

        surrender_panel.setBounds(250, 250, 240, 240);
        surrender_panel.setLayout(null);

        label_messagge.setBounds(10, 100, 220, 40);
        label_regret_message.setBounds(10, 100, 220, 40);
        //Jbutton
        btnOK.setBounds(80, 180, 80, 40);
        btnOK.addActionListener(this);

        btnRegret.setBounds(770, 600, 80, 40);
        btnRegret.addActionListener(this);

        btnSurrender.setBounds(860, 600, 80, 40);
        btnSurrender.addActionListener(this);

        btnRegretOK.setBounds(20, 180, 80, 40);
        btnRegretOK.addActionListener(this);

        btnRegretNO.setBounds(120, 180, 80, 40);
        btnRegretNO.addActionListener(this);

        label_messagge.setText(String.format("��Ϸ��ʼ������ִ%s�塣", this.state_color == PointColor.BLACK.ordinal()?"��":"��"));
        help_panel.add(label_messagge);
        help_panel.add(btnOK);

        label_regret_message.setText("�Է�������壬���Ƿ�ͬ�⣿");
        regret_panel.add(label_regret_message);
        regret_panel.add(btnRegretOK);
        regret_panel.add(btnRegretNO);

        this.add(label_username);
        this.add(label_win);
        this.add(label_lose);
        this.add(label_draw);
        this.add(label_oppo_username);
        this.add(label_oppo_win);
        this.add(label_oppo_lose);
        this.add(label_oppo_draw);
        this.add(btnRegret);
        this.add(btnSurrender);
        this.add(help_panel);
        setLayout(null); // ���ò���Ϊ��

    }

    //����������ӵ�����
    public void addPoint(Point point) {
        points.add(point);  // ������ӵ�list��
        datas[point.getX()][point.getY()] = 1;
        updateUI();
    }


    //�����Ʒ���
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(board.getImage(), 0, 0, 740, 740, null);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Point point : points) {
            ImageIcon icon = null;
            if (point.getColor() == PointColor.WHITE.ordinal()) {
                icon = icon_white;
            } else {
                icon = icon_black;
            }
            //ָ��λ�û���ָ��ͼƬ
            //�˴������ѿ��Ǳ߽磬ʹ��point��15*15�����е����꼴��
            g.drawImage(icon.getImage(),(point.getX() - 1)*49+26-24, (point.getY() - 1)*49+26-24, 49, 49, null);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!is_panel && is_my_turn) {
            int x = e.getX();
            int y = e.getY();
            System.out.println(x + "," + y);
            Point point = null;
            System.out.println((x - 26) % 49);
            int X = (x - 26) % 49 > 24 ? (x - 26) / 49 + 2 : (x - 26) / 49 + 1;
            int Y = (y - 26) % 49 > 24 ? (y - 26) / 49 + 2 : (y - 26) / 49 + 1;
            System.out.println(X + ":" + Y);
            if (X > 0 && X < 16 && Y > 0 && Y < 16) {
                if(datas[X][Y] == 0){
                    System.out.println("�Ϸ�λ��");
                    point = new Point(X, Y, state_color);
                    points.add(point);
                    addPoint(point);
                    is_my_turn = false;
                    try {
                        Utils.sendMsg(sock, new Datagram(MsgType.NORMAL.ordinal(), X, Y, 0, 0, state_color, ""), Client.out_map);
                    }
                    catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnOK) {
                this.remove(help_panel);
                is_panel = false;
                if (is_over) {
                    // ��Ϸ�����ˣ�Ӧ�÷���MatchPanel����
                    new MatchPanel(username, win, lose, draw);
                    JFrame parent = (JFrame) this.getTopLevelAncestor();
                    parent.dispose();
                } else {
                    updateUI();
                }
            } else if (e.getSource() == btnRegret) {
                if(is_my_turn) {
                    // ���ͻ�������
                    is_my_turn = false;
                    Utils.sendMsg(sock, new Datagram(MsgType.REGRET.ordinal(), ""), Client.out_map);
                }
                else{
                    label_messagge.setText("��ǰ�������Ļغϣ����ܻ��塣");
                    this.add(help_panel);
                    updateUI();
                    is_panel = true;
                }
            } else if (e.getSource() == btnSurrender) {
                if(is_my_turn) {
                    // ������������
                    Utils.sendMsg(sock, new Datagram(MsgType.SURRENDER.ordinal()), Client.out_map);
                    // ��ʾ������
                    lose++;
                    label_messagge.setText("��Ϸ��������ʧ���ˡ�");
                    this.add(help_panel);
                    is_panel = true;
                    is_over = true;
                    //t.stop();
                    updateUI();
                }
                else{
                    label_messagge.setText("��ǰ�������Ļغϣ��������䡣");
                    this.add(help_panel);
                    updateUI();
                    is_panel = true;
                }
            } else if (e.getSource() == btnRegretOK) {
                // ͬ�����
                // �ȸ����Լ��Ľ���
                regret_t.start();
                // �ٷ���ͬ���������
                Utils.sendMsg(sock, new Datagram(MsgType.REGRET.ordinal(), "ͬ��"), Client.out_map);
                this.remove(regret_panel);
                is_panel = false;
                updateUI();
            } else if (e.getSource() == btnRegretNO) {
                // ��ͬ�����
                Utils.sendMsg(sock, new Datagram(MsgType.REGRET.ordinal(), "��ͬ��"), Client.out_map);
                this.remove(regret_panel);
                is_panel = false;
                updateUI();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
    }
}
