package Server;
import com.model.UserData;

import java.sql.*;

public class DatabaseUtil {
    private static Connection con; //声明Connection对象
    private static final String db_name = "gobang";
    private static final String table_name = "gobang_user";
    static{
        init();
    }
    private static void init(){
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://localhost:3306/" + db_name + "?characterEncoding=utf-8&serverTimezone=GMT%2B8";
        //MySQL配置时的用户名
        String user = "gobang";
        //MySQL配置时的密码
        String password = "password";
        try {
            //Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if(!con.isClosed()){
                System.out.println("数据库连接成功");
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
                // 用户名或密码不正确
                rs.close();
                return "用户名或密码不正确";
            }
        }
        catch (SQLException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
            return "数据库连接失败！";
        }
        catch (Exception e){
            System.out.println("未知错误");
            e.printStackTrace();
            return "未知错误";
        }
    }

    public static String check(String username){
        try {
            Statement statement = con.createStatement();
            String sql = String.format("select * from %s where username = '%s'", table_name, username);
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                rs.close();
                return "已有同名用户注册！";
            }
            else{
                // 数据库中没有同名用户，用户名合法
                rs.close();
                return "";
            }
        }
        catch (SQLException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
            return "数据库连接失败！";
        }
        catch (Exception e){
            System.out.println("未知错误");
            e.printStackTrace();
            return "未知错误";
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
            psql.executeUpdate(); // 执行更新
            psql.close();
            return "";
        }
        catch (Exception e){
            System.out.println("未知错误");
            e.printStackTrace();
            return "未知错误";
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
            psql.executeUpdate(); // 执行更新
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
            System.out.println("数据库连接失败");
            e.printStackTrace();
            throw e;
        }
        catch (Exception e){
            System.out.println("未知错误");
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String args[]){
        System.out.println("验证");
        System.out.println(valid("htx", "123456"));
        System.out.println("注册");
        System.out.println(register("htx", "123456"));
        System.out.println("验证");
        System.out.println(valid("htx", "123456"));
        System.out.println("注册");
        System.out.println(register("htx", "123456"));
        System.out.println("胜场++");
        System.out.println(update("htx", "win"));
        System.out.println("查询");
        try {
            System.out.println(getUserData("htx"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Test DatabaseUtil");
    }
}
