package Client;

import com.model.Datagram;
import com.model.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Utils {
    public static final int BOARD_SIZE = 15; // 棋盘大小
    public static final int BG_WIDTH = 760; // 背景图宽度
    public static final int BG_HEIGHT = 790; // 背景图高度
    public static final String IP = "127.0.0.1"; // 服务器ip
    public static final int LOG_PORT = 12345; // 登录注册端口
    public static final int CONNECT_PORT = 23456; // 连接端口
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
    /**
     * 利用java原生的摘要实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
 　　* 将byte转为16进制
 　　* @param bytes
 　　* @return
 　　*/
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}

enum MsgType{
    WIN, LOSE, DRAW, NORMAL, REGRET, SURRENDER, ERROR, LOGIN, REGISTER, PROFILE, NEWMATCH
}

enum PointColor{
    NONEUSED, BLACK, WHITE
}