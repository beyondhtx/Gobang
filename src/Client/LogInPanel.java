package Client;

import com.model.Datagram;
import com.model.UserData;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

public class LogInPanel extends JFrame implements ActionListener {
    private JLabel label_background, label_username, label_password, label_msg;
    private JTextField text_username;
    private JPasswordField text_password;
    private JButton btnLogin, btnRegister, btnCancel;

    private ImageIcon icon = new ImageIcon("drawable/loading.png");
    private int mx = 0, my = 0, jfx = 0, jfy = 0;
    private int width = 550;
    private int height = 550;

    //���췽��
    public LogInPanel() {
        System.out.println("Connect Service����");
        initViews();
        setViews();
        addViews();
        this.addMouseMotionListener(new MouseMotionAdapter() {
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
        Font f = new Font("��Բ", Font.PLAIN, 15);
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
        //���Ʊ���
        label_background = new JLabel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                icon.paintIcon(this, g, 0, 0);
            }

        };
        //JLabel
        label_username = new JLabel("�û�����");
        label_password = new JLabel("���룺");
        label_msg = new JLabel("");
        //JTextFiled
        text_username = new JTextField();
        text_password = new JPasswordField();

        //JButton
        btnLogin = new ImageButton("  ��¼  ");
        btnRegister = new ImageButton("  ע��  ");
        btnCancel = new ImageButton("  �˳�  ");
    }

    public void setViews() {
        setUndecorated(true);
//        blankPanel.setBounds(10, 10, 280, 180);
        //JLABEL
        label_username.setBounds(140, 130, 80, 45);
        label_password.setBounds(140, 180, 80, 45);
        label_msg.setBounds(140, 220, 200, 45);

        //JTextFiled
        text_username.setBounds(230, 130, 100, 35);
        text_password.setBounds(230, 180, 100, 35);

        //JButton
        btnLogin.setBounds(120, 300, 100, 35);
        btnLogin.addActionListener(this);
        btnRegister.setBounds(230,300,100,35);
        btnRegister.addActionListener(this);
        btnCancel.setBounds(175, 350, 100, 35);
        btnCancel.addActionListener(this);
    }

    public void addViews() {
        //JLable
        add(label_username);
        add(label_password);
        add(label_msg);
        //JTextFiled
        add(text_username);
        add(text_password);
        //JButton
        add(btnLogin);
        add(btnRegister);
        add(btnCancel);
        //help_panel.add(label_log_fail);
        //help_panel.add(btnLogFail);
        //this.add(help_panel);
        this.add(label_background);
        setUndecorated(true); // ��װ��
        setSize(width, height); // ���ô��ڴ�С
        AWTUtilities.setWindowOpaque(this, false);
        setLocationRelativeTo(null);  //���ô��ھ���
        setVisible(true);
    }

    public static void main(String[] args) {
        LogInPanel connectService = new LogInPanel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            System.out.println("�����¼");
            String username = text_username.getText();
            String password = String.valueOf(text_password.getPassword());
            String encode_pass = Utils.getSHA256StrJava(String.valueOf(text_password.getPassword()));
            System.out.println("�û���="+ username);
            System.out.println("����="+ password);
            if(username.length() == 0 || password.length() == 0){
                label_msg.setText("�û��������������Ϊ��");
                repaint();
                return;
            }
            try {
                Socket socket = new Socket(Utils.IP, Utils.LOG_PORT);
                Utils.sendMsg(socket, new Datagram(MsgType.LOGIN.ordinal(), username + "\n" + encode_pass), Client.out_map);
                Datagram rec = Utils.recvMsg(socket, Client.in_map);
                if(rec.msg.length() == 0){
                    // ��½�ɹ�����ת���м����
                    //label_msg.setText("��¼�ɹ�");
                    //repaint();
                    socket = new Socket(Utils.IP, Utils.LOG_PORT);
                    Utils.sendMsg(socket, new Datagram(MsgType.PROFILE.ordinal(), username), Client.out_map);
                    System.out.println("here1");
                    UserData ud = Utils.recvUserData(socket, Client.in_map);
                    System.out.println("here2");
                    new MatchPanel(ud.username, ud.win, ud.lose, ud.draw);
                    System.out.println("there");
                    this.dispose();
                }
                else{
                    System.out.println(rec.msg);
                    label_msg.setText(rec.msg);
                    repaint();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else if (e.getSource() == btnRegister) {
            System.out.println("���ע��");
            System.out.println("�û���="+text_username.getText());
            System.out.println("����="+ String.valueOf(text_password.getPassword()));
            System.out.println("���ܺ�����=" + Utils.getSHA256StrJava(String.valueOf(text_password.getPassword())));
            String username = text_username.getText();
            String password = String.valueOf(text_password.getPassword());
            String encode_pass = Utils.getSHA256StrJava(String.valueOf(text_password.getPassword()));
            System.out.println("�û���="+ username);
            System.out.println("����="+ password);
            if(username.length() == 0 || password.length() == 0){
                label_msg.setText("�û��������������Ϊ��");
                repaint();
                return;
            }
            try {
                Socket socket = new Socket(Utils.IP, Utils.LOG_PORT);
                Utils.sendMsg(socket, new Datagram(MsgType.REGISTER.ordinal(), username + "\n" + encode_pass), Client.out_map);
                Datagram rec = Utils.recvMsg(socket, Client.in_map);
                if(rec.msg.length() == 0){
                    // ע��ɹ�
                    label_msg.setText("ע��ɹ�");
                    repaint();
                }
                else{
                    System.out.println(rec.msg);
                    label_msg.setText(rec.msg);
                    repaint();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else if(e.getSource() == btnCancel){
            System.out.println("����˳�");
            System.exit(0);
        }
    }

}
