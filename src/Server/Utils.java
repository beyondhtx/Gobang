package Server;

import com.model.Datagram;
import com.model.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Utils {
    public static final int BOARD_SIZE = 15; // 棋盘大小
    public static final int LOG_PORT = 12345; // 登录端口
    public static final int CONNECT_PORT = 23456; // 登录后连接端口
    public static void sendMsg(Socket socket, Datagram datagram, HashMap<Socket, ObjectOutputStream> map) throws IOException {
        ObjectOutputStream out;
        if(map.containsKey(socket)){
            out = map.get(socket);
        }
        else {
            out = new ObjectOutputStream(socket.getOutputStream());
            map.put(socket, out);
        }
        out.writeObject(datagram);
        out.flush();
    }
    public static Datagram recvMsg(Socket socket, HashMap<Socket, ObjectInputStream> map)throws IOException, ClassNotFoundException{
        ObjectInputStream in;
        if(map.containsKey(socket)){
            in = map.get(socket);
        }
        else {
            in = new ObjectInputStream(socket.getInputStream());
            map.put(socket, in);
        }
        return (Datagram)in.readObject();
    }
    public static void sendUserData(Socket socket, UserData userdata, HashMap<Socket, ObjectOutputStream> map) throws IOException{
        ObjectOutputStream out;
        if(map.containsKey(socket)){
            out = map.get(socket);
        }
        else {
            out = new ObjectOutputStream(socket.getOutputStream());
            map.put(socket, out);
        }
        out.writeObject(userdata);
        out.flush();
    }
    public static UserData recvUserData(Socket socket, HashMap<Socket, ObjectInputStream> map)throws IOException, ClassNotFoundException{
        ObjectInputStream in;
        if(map.containsKey(socket)){
            in = map.get(socket);
        }
        else {
            in = new ObjectInputStream(socket.getInputStream());
            map.put(socket, in);
        }
        return (UserData) in.readObject();
    }
}

enum MsgType{
    WIN, LOSE, DRAW, NORMAL, REGRET, SURRENDER, ERROR, LOGIN, REGISTER, PROFILE, NEWMATCH
}

enum PointColor{
    NONEUSED, BLACK, WHITE
}