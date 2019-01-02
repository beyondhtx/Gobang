package Server;


import com.model.Datagram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Server {
    public static HashMap<Socket, ObjectOutputStream> out_map;
    public static HashMap<Socket, ObjectInputStream> in_map;
    public static LinkedList<Elem> matching_queue;
    private static String tmp_username;
    static{
        matching_queue = new LinkedList<>();
        out_map = new HashMap<>();
        in_map = new HashMap<>();
    }
    static class Elem{
        public Socket sock;
        public String username;
        public Elem(Socket sock, String username) {
            this.sock = sock;
            this.username = username;
        }
    }
    public static void main(String[] args){
        new Thread(
                // 用户登录/注册/获取个人信息线程
                () -> {
                    try {
                        ServerSocket ss = new ServerSocket(Utils.LOG_PORT);
                        while(true) {
                            Socket client = ss.accept();
                            varify(client);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
        ).start();
        try {
            // 主线程处理用户匹配的请求
            ServerSocket ss = new ServerSocket(Utils.CONNECT_PORT);
            while(true) {
                Socket client = ss.accept();
                String username;
                if(varify(client).length() == 0)
                {
                    username = tmp_username;
                    // 加到队列里
                    matching_queue.add(new Elem(client, username));
                    System.out.println(matching_queue.size());
                    if(matching_queue.size() >= 2){
                        Elem e1 = matching_queue.remove();
                        Elem e2 = matching_queue.remove();
                        Match new_match = new Match(e1.username, e2.username, e1.sock, e2.sock);
                        System.out.println(e1.username +"\n"+ e2.username);
                        Utils.sendMsg(e1.sock, new Datagram(MsgType.NEWMATCH.ordinal(), "black"), Server.out_map);
                        Utils.sendUserData(e1.sock, DatabaseUtil.getUserData(e2.username), Server.out_map);
                        Utils.sendMsg(e2.sock, new Datagram(MsgType.NEWMATCH.ordinal(), "white"), Server.out_map);
                        Utils.sendUserData(e2.sock, DatabaseUtil.getUserData(e1.username), Server.out_map);
                        new_match.start();
                    }
                }
                // else do nothing
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String varify(Socket sock) throws IOException, ClassNotFoundException {
        System.out.println("varify");
        Datagram data = Utils.recvMsg(sock, Server.in_map);
        System.out.println("data.msg="+data.msg);
        System.out.println("data.type="+data.type);
        String[] log_info = data.msg.split("\n");
        if(data.type == MsgType.NEWMATCH.ordinal()){
            tmp_username = data.msg;
            return "";
        }
        else if(data.type == MsgType.LOGIN.ordinal()) {
            String res;
            if ((res = DatabaseUtil.valid(log_info[0], log_info[1])).length() == 0) {
                // 认证成功
                Utils.sendMsg(sock, new Datagram(""), Server.out_map);
                return "";
            } else {
                Utils.sendMsg(sock, new Datagram(res), Server.out_map);
                return "Error";
            }
        }
        else if(data.type == MsgType.REGISTER.ordinal()){
            String res;
            if((res = DatabaseUtil.register(log_info[0], log_info[1])).length() == 0){
                Utils.sendMsg(sock, new Datagram(""), Server.out_map);
                return "";
            }
            else{
                Utils.sendMsg(sock, new Datagram(res), Server.out_map);
                return "Error";
            }
        }
        else if(data.type == MsgType.PROFILE.ordinal()){
            // 获取用户信息
            try {
                Utils.sendUserData(sock, DatabaseUtil.getUserData(data.msg), Server.out_map);
                System.out.println(DatabaseUtil.getUserData(data.msg));
                return "";
            }
            catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        }
        else{
            return "Error Type of Msg";
        }
    }
}
