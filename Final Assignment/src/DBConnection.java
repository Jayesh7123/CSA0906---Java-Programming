import java.sql.*;

public class DBConnection {
    private static final String SERVER_URL="jdbc:mysql://localhost:3306/";
    private static final String DB_URL="jdbc:mysql://localhost:3306/canteen_db";
    private static final String USER="root";
    private static final String PASSWORD="Jayesh@7123"; // change if needed
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(DB_URL,USER,PASSWORD); }
    public static void initializeDatabase() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try(Connection c=DriverManager.getConnection(SERVER_URL,USER,PASSWORD); Statement s=c.createStatement()){
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS canteen_db");
        }
        try(Connection c=getConnection(); Statement s=c.createStatement()){
            s.executeUpdate("CREATE TABLE IF NOT EXISTS users(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL,username VARCHAR(50) UNIQUE NOT NULL,password VARCHAR(100) NOT NULL,role VARCHAR(20) NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS food_items(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL,category VARCHAR(50) NOT NULL DEFAULT 'Meals',price DOUBLE NOT NULL,stock INT NOT NULL)");
            ensureColumn(c);
            s.executeUpdate("CREATE TABLE IF NOT EXISTS orders(id INT AUTO_INCREMENT PRIMARY KEY,user_id INT NOT NULL,total DOUBLE NOT NULL,status VARCHAR(30) NOT NULL DEFAULT 'Placed',order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(user_id) REFERENCES users(id))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS order_items(id INT AUTO_INCREMENT PRIMARY KEY,order_id INT NOT NULL,food_id INT NOT NULL,quantity INT NOT NULL,price DOUBLE NOT NULL,FOREIGN KEY(order_id) REFERENCES orders(id),FOREIGN KEY(food_id) REFERENCES food_items(id))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS bills(id INT AUTO_INCREMENT PRIMARY KEY,order_id INT NOT NULL,amount DOUBLE NOT NULL,bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(order_id) REFERENCES orders(id))");
            try(ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM users WHERE username='admin'")){ if(rs.next()&&rs.getInt(1)==0) try(PreparedStatement p=c.prepareStatement("INSERT INTO users(name,username,password,role) VALUES(?,?,?,'ADMIN')")){p.setString(1,"Administrator");p.setString(2,"admin");p.setString(3,"admin123");p.executeUpdate();}}
            try(ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM food_items")){ if(rs.next()&&rs.getInt(1)==0) seedFood(c); }
        }
    }
    private static void ensureColumn(Connection c){
        try(Statement s=c.createStatement()){ s.executeUpdate("ALTER TABLE food_items ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT 'Meals'"); } catch(Exception ignored){}
    }
    private static void seedFood(Connection c)throws SQLException{
        String q="INSERT INTO food_items(name,category,price,stock) VALUES(?,?,?,?)";
        try(PreparedStatement p=c.prepareStatement(q)){
            add(p,"Chicken Biryani","Rice",120,30); add(p,"Veg Fried Rice","Rice",90,35); add(p,"Masala Dosa","South Indian",60,40); add(p,"Paneer Burger","Fast Food",85,25); add(p,"Veg Sandwich","Fast Food",55,30); add(p,"Samosa","Snacks",20,80); add(p,"Tea","Beverages",15,120); add(p,"Cold Coffee","Beverages",45,60); add(p,"Fresh Lime Juice","Beverages",35,50); add(p,"Chocolate Muffin","Dessert",40,25);
        }
    }
    private static void add(PreparedStatement p,String n,String cat,double pr,int st)throws SQLException{p.setString(1,n);p.setString(2,cat);p.setDouble(3,pr);p.setInt(4,st);p.executeUpdate();}
}
