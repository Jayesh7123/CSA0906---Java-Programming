import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField username; private JPasswordField password;
    public LoginFrame(){
        setTitle("Canteen Food Ordering & Billing System"); setSize(900,560); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setResizable(false);
        JPanel root=new JPanel(new GridLayout(1,2)); root.setBackground(UITheme.BG);
        JPanel left=new JPanel(); left.setBackground(UITheme.SIDEBAR); left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS)); left.setBorder(BorderFactory.createEmptyBorder(55,45,55,45));
        JLabel brand=new JLabel("CANTEEN"); brand.setForeground(Color.WHITE); brand.setFont(new Font("Segoe UI",Font.BOLD,30)); left.add(brand); left.add(Box.createVerticalStrut(8));
        JLabel sub=new JLabel("Food Ordering & Billing"); sub.setForeground(new Color(210,220,255)); sub.setFont(new Font("Segoe UI",Font.PLAIN,18)); left.add(sub); left.add(Box.createVerticalStrut(40));
        String[] features={"✓ Browse available food","✓ Check live availability","✓ Manage cart and orders","✓ Automatic bill calculation","✓ Track order status"};
        for(String f:features){ JLabel l=new JLabel(f); l.setForeground(Color.WHITE); l.setFont(new Font("Segoe UI",Font.PLAIN,15)); l.setAlignmentX(Component.LEFT_ALIGNMENT); left.add(l); left.add(Box.createVerticalStrut(14)); }
        left.add(Box.createVerticalGlue()); JLabel footer=new JLabel("College Canteen Portal"); footer.setForeground(new Color(190,200,235)); left.add(footer);
        JPanel right=new JPanel(new GridBagLayout()); right.setBackground(UITheme.BG); GridBagConstraints g=new GridBagConstraints(); g.gridx=0; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1; g.insets=new Insets(8,40,8,40);
        JPanel card=UITheme.card(new GridBagLayout()); card.setPreferredSize(new Dimension(360,430)); GridBagConstraints c=new GridBagConstraints(); c.gridx=0;c.fill=GridBagConstraints.HORIZONTAL;c.insets=new Insets(8,24,8,24);
        JLabel welcome=UITheme.heading("Welcome Back",24); c.gridy=0; card.add(welcome,c); JLabel desc=UITheme.muted("Login to continue to the canteen portal"); c.gridy=1; card.add(desc,c);
        c.gridy=2; card.add(new JLabel("Username"),c); username=new JTextField(); username.setBorder(UITheme.roundedBorder(new Color(210,215,225),12)); c.gridy=3; card.add(username,c);
        c.gridy=4; card.add(new JLabel("Password"),c); password=new JPasswordField(); password.setBorder(UITheme.roundedBorder(new Color(210,215,225),12)); c.gridy=5; card.add(password,c);
        JButton login=UITheme.button("Login",UITheme.PRIMARY); c.gridy=6; card.add(login,c); JButton register=UITheme.button("Create Customer Account",UITheme.SUCCESS); c.gridy=7; card.add(register,c);
        c.gridy=8; card.add(Box.createVerticalStrut(8),c); g.gridy=0; right.add(card,g); root.add(left); root.add(right); setContentPane(root);
        login.addActionListener(e->login()); register.addActionListener(e->register()); getRootPane().setDefaultButton(login); setVisible(true);
    }
    private void login(){ String u=username.getText().trim(); String p=new String(password.getPassword()); if(u.isEmpty()||p.isEmpty()){JOptionPane.showMessageDialog(this,"Enter username and password.");return;} String q="SELECT id,name,username,role FROM users WHERE username=? AND password=?"; try(Connection con=DBConnection.getConnection(); PreparedStatement ps=con.prepareStatement(q)){ps.setString(1,u);ps.setString(2,p);try(ResultSet rs=ps.executeQuery()){if(rs.next()){User user=rs.getString("role").equalsIgnoreCase("ADMIN")?new Admin(rs.getInt("id"),rs.getString("name"),u):new Customer(rs.getInt("id"),rs.getString("name"),u); dispose(); user.openDashboard();}else JOptionPane.showMessageDialog(this,"Invalid username or password.","Login",JOptionPane.ERROR_MESSAGE);}}catch(Exception ex){JOptionPane.showMessageDialog(this,"Login failed:\n"+ex.getMessage());}}
    private void register(){ JTextField n=new JTextField(),u=new JTextField(); JPasswordField p=new JPasswordField(); JPanel panel=new JPanel(new GridLayout(0,1,6,6)); panel.add(new JLabel("Full Name"));panel.add(n);panel.add(new JLabel("Username"));panel.add(u);panel.add(new JLabel("Password"));panel.add(p); if(JOptionPane.showConfirmDialog(this,panel,"Create Customer Account",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return; if(n.getText().trim().isEmpty()||u.getText().trim().isEmpty()||new String(p.getPassword()).isEmpty()){JOptionPane.showMessageDialog(this,"All fields are required.");return;} try(Connection con=DBConnection.getConnection(); PreparedStatement ps=con.prepareStatement("INSERT INTO users(name,username,password,role) VALUES(?,?,?,'CUSTOMER')")){ps.setString(1,n.getText().trim());ps.setString(2,u.getText().trim());ps.setString(3,new String(p.getPassword()));ps.executeUpdate();JOptionPane.showMessageDialog(this,"Registration successful. Please login.");}catch(SQLIntegrityConstraintViolationException ex){JOptionPane.showMessageDialog(this,"Username already exists.");}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}}
}
