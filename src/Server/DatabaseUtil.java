package Server;
import com.model.UserData;

import java.sql.*;

public class DatabaseUtil {
    private static Connection con; //����Connection����
    private static final String db_name = "gobang";
    private static final String table_name = "gobang_user";
    static{
        init();
    }
    private static void init(){
        //����������
        String driver = "com.mysql.jdbc.Driver";
        //URLָ��Ҫ���ʵ����ݿ���mydata
        String url = "jdbc:mysql://localhost:3306/" + db_name + "?characterEncoding=utf-8&serverTimezone=GMT%2B8";
        //MySQL����ʱ���û���
        String user = "gobang";
        //MySQL����ʱ������
        String password = "password";
        try {
            //Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if(!con.isClosed()){
                System.out.println("���ݿ����ӳɹ�");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String valid(String username, String password){
        try {
            Statement statement = con.createStatement();
            String sql = String.format("select * from %s where username = '%s' and password = '%s'", table_name, username, password);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                rs.close();
                return "";
            }
            else{
                // �û��������벻��ȷ
                rs.close();
                return "�û��������벻��ȷ";
            }
        }
        catch (SQLException e){
            System.out.println("���ݿ�����ʧ��");
            e.printStackTrace();
            return "���ݿ�����ʧ�ܣ�";
        }
        catch (Exception e){
            System.out.println("δ֪����");
            e.printStackTrace();
            return "δ֪����";
        }
    }

    public static String check(String username){
        try {
            Statement statement = con.createStatement();
            String sql = String.format("select * from %s where username = '%s'", table_name, username);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                rs.close();
                return "����ͬ���û�ע�ᣡ";
            }
            else{
                // ���ݿ���û��ͬ���û����û����Ϸ�
                rs.close();
                return "";
            }
        }
        catch (SQLException e){
            System.out.println("���ݿ�����ʧ��");
            e.printStackTrace();
            return "���ݿ�����ʧ�ܣ�";
        }
        catch (Exception e){
            System.out.println("δ֪����");
            e.printStackTrace();
            return "δ֪����";
        }
    }

    public static String register(String username, String password){
        String tmp;
        if((tmp = check(username)).length() > 0){
            return tmp;
        }
        try {
            PreparedStatement psql;
            psql = con.prepareStatement("insert into " + table_name + " (username,password) values(?,?)");
            psql.setString(1, username);
            psql.setString(2, password);
            psql.executeUpdate(); // ִ�и���
            psql.close();
            return "";
        }
        catch (Exception e){
            System.out.println("δ֪����");
            e.printStackTrace();
            return "δ֪����";
        }
    }

    public static boolean update(String usrname, String state){
        try{
            System.out.println("update " + table_name + " set win = win + 1 where username = " + usrname);
            PreparedStatement psql;
            switch (state) {
                case "win":
                    psql = con.prepareStatement("update " + table_name + " set win = win + 1 where username = '" + usrname + "'");
                    break;
                case "lose":
                    psql = con.prepareStatement("update " + table_name + " set lose = lose + 1 where username = '" + usrname + "'");
                    break;
                default:
                    psql = con.prepareStatement("update " + table_name + " set draw = draw + 1 where username = '" + usrname + "'");
                    break;
            }
            psql.executeUpdate(); // ִ�и���
            psql.close();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static UserData getUserData(String username) throws Exception{
        try {
            System.out.println("h");
            UserData result = new UserData();
            Statement statement = con.createStatement();
            String sql = String.format("select * from %s where username = '%s'", table_name, username);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                result.win = rs.getInt("win");
                result.lose = rs.getInt("lose");
                result.draw = rs.getInt("draw");
            }
            result.username = username;
            System.out.println("hk");
            rs.close();
            return result;
        }
        catch (SQLException e){
            System.out.println("���ݿ�����ʧ��");
            e.printStackTrace();
            throw e;
        }
        catch (Exception e){
            System.out.println("δ֪����");
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String args[]){
        System.out.println("��֤");
        System.out.println(valid("htx", "123456"));
        System.out.println("ע��");
        System.out.println(register("htx", "123456"));
        System.out.println("��֤");
        System.out.println(valid("htx", "123456"));
        System.out.println("ע��");
        System.out.println(register("htx", "123456"));
        System.out.println("ʤ��++");
        System.out.println(update("htx", "win"));
        System.out.println("��ѯ");
        try {
            System.out.println(getUserData("htx"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Test DatabaseUtil");
    }
}
