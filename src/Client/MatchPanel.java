package Client;

import com.model.Datagram;
import com.model.UserData;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

public class MatchPanel extends JFrame implements ActionListener {
    private String username;
    private int win, lose, draw;
    private static final int Frame_Width = 550;
    private static final int Frame_Height = 550;
    private JLabel label_back, label_show_message;
    private JLabel label_win, label_lose, label_draw, label_username, label_oppo_win, label_oppo_lose, label_oppo_draw, label_oppo_username;
    private int port;
    private static final ImageIcon icon = new ImageIcon("drawable/loading.png");
    private JButton btnStartLocal, btnCancle;
    private boolean isConnectionted = false;
    private int state_color; // 用于存放服务器端要选用的棋子颜色
    private BlankPanel blankPanel;
    private int mx = 0, my = 0, jfx = 0, jfy = 0;
    private Thread t;

    public MatchPanel(String username, int win, int lose, int draw) {
        System.out.println("MatchPanel启动");
        this.username = username;
        this.win = win;
        this.lose = lose;
        this.draw = draw;
        t = new Thread(() -> {
            int color = 0;
            try {
                Socket data_sock = new Socket(Utils.IP, Utils.CONNECT_PORT);
                Utils.sendMsg(data_sock, new Datagram(MsgType.NEWMATCH.ordinal(), username), Client.out_map);
                System.out.println("Run thread");
                Datagram res = Utils.recvMsg(data_sock, Client.in_map);
                switch (res.msg){
                    case "black":
                        color = PointColor.BLACK.ordinal();
                        break;
                    case "white":
                        color = PointColor.WHITE.ordinal();
                        break;
                    default:
                        System.out.println("Should not reach here in MatchPanel()");
                }
                UserData oppo_user = Utils.recvUserData(data_sock, Client.in_map);
                System.out.println("========"+oppo_user);
                Thread.sleep(1000);
                System.out.println("此处在匹配成功之后才关闭");
                isConnectionted = true; //用于中断label的不断刷新
                MatchPanel.this.dispose(); // 有客户端连接之后关闭当前窗口
                PlayerPanel playerPanel = new PlayerPanel(color, data_sock, new UserData(username, win, lose, draw), oppo_user);
                JFrame frame = new JFrame();
                frame.setContentPane(playerPanel);
                frame.setSize(Utils.BG_WIDTH + 200, Utils.BG_HEIGHT);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setTitle("TX五子棋");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
        initViews();
        setViews();
        addViews();this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mx + jfx, e.getYOnScreen() - my + jfy);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                jfx = getX();
                jfy = getY();
            }
        });
    }

    public void initViews() {
        Font f = new Font("幼圆", Font.PLAIN, 15);

        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.WHITE);
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
        //绘制背景
        label_back = new JLabel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                icon.paintIcon(this, g, 0, 0);
            }
        };

        label_username = new JLabel("    当前玩家： " + username);
        label_show_message = new JLabel("");
        label_win = new JLabel("    胜场数： " + win);
        label_lose = new JLabel("    负场数： " + lose);
        label_draw = new JLabel("    平场数： " + draw);

        //JButton
        btnStartLocal = new ImageButton("    开始匹配");
        btnCancle = new ImageButton("退出");
        //JPanel
        blankPanel = new BlankPanel(130);
    }

    public void setViews() {

        //Jlabel
        label_show_message.setBounds(120, 250, 200, 35);
        label_username.setBounds(120, 130, 200, 35);
        label_win.setBounds(120, 160, 200, 35);
        label_lose.setBounds(120, 190, 200, 35);
        label_draw.setBounds(120, 220, 200, 35);
        //JButton
        btnStartLocal.setBounds(135, 310, 192, 35);
        btnStartLocal.addActionListener(this);

        btnCancle.setBounds(190, 360, 80, 30);
        btnCancle.addActionListener(this);

        //JPanel
        blankPanel.setBounds(110, 110, 200, 200);

        //JFrame
        setUndecorated(true); // 不装饰
        setSize(Frame_Width, Frame_Height); // 设置窗口大小
        AWTUtilities.setWindowOpaque(this, false);
        setLocationRelativeTo(null);  //设置窗口居中
        setVisible(true);
    }

    public void addViews() {
        this.add(label_username);
        this.add(label_win);
        this.add(label_lose);
        this.add(label_draw);
        this.add(btnCancle);
        this.add(btnStartLocal);
        this.add(label_show_message);
        this.add(label_back); // 最后添加在最后面
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnStartLocal) {
            t.start();
            // 显示正在匹配字样
            String[] mesages = new String[]{
                    "    匹配对手中 .",
                    "    匹配对手中 ...",
                    "    匹配对手中 ....",
                    "    匹配对手中 ....",
                    "    匹配对手中 .....",
                    "    匹配对手中 .......",
            };
            new Thread(() -> {
                int h = 0;
                while (!isConnectionted) {
                    label_show_message.setText(mesages[h]);
                    repaint();
                    h++;
                    h = h % mesages.length;
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            this.remove(btnStartLocal);
        }
        else if(e.getSource() == btnCancle){
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            Socket sock = new Socket(Utils.IP, Utils.LOG_PORT);
            MatchPanel client = new MatchPanel("beyondhtx", 0, 0, 1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}