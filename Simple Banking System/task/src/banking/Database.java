package banking;

import java.sql.*;

public class Database {
    private Connection conn;
    private static Database instance = null;

    private Database(String dbUrl) {
        String url = "jdbc:sqlite:" + dbUrl;
        try {
            this.conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createTables();
    }

    static public Database getInstance(String dbUrl) {
        if (instance == null) {
            instance = new Database(dbUrl);
        }
        return instance;
    }

    private void createTables() {
        String pre = "drop table if exists card";
        String sql = "create table card (\n"
                +    "    id INTEGER, \n"
                +    "    number TEXT, \n"
                +    "    pin TEXT, \n"
                +    "    balance INTEGER DEFAULT 0\n"
                +    ");";
        try {
            Connection conn = this.conn;
            Statement stmt = conn.createStatement();
            stmt.execute(pre);
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String num, String pin, int balance) {
        String sql = "insert into card(number, pin, balance) values(?,?,?)";
        try {
            Connection conn = this.conn;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, num);
            pstmt.setString(2, pin);
            pstmt.setInt(3, balance);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean contains(String num) {
        String sql = "select count(*) from card where number = ?";
        int ans = 0;
        try {
            Connection conn = this.conn;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, num);
            ResultSet rs = pstmt.executeQuery();
            ans = rs.getInt(1);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ans != 0;
    }

    public String getPin(String num) {
        String sql = "select pin from card where number = ?";
        String ret = "";
        try {
            Connection conn = this.conn;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, num);
            ResultSet rs = pstmt.executeQuery();
            ret = rs.getString(1);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    public int getBalance(String num) {
        String sql = "select balance from card where number = ?";
        int ret = 0;
        try {
            Connection conn = this.conn;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, num);
            ResultSet rs = pstmt.executeQuery();
            ret = rs.getInt("balance");
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
