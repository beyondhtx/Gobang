package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    public static HashMap<Socket, ObjectOutputStream> out_map;
    public static HashMap<Socket, ObjectInputStream> in_map;
    static{
        out_map = new HashMap<>();
        in_map = new HashMap<>();
    }
    public static void main(String args[]){
        new LogInPanel();
    }
}
