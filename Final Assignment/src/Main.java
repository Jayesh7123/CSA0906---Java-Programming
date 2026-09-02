import javax.swing.*;
public class Main {
    public static void main(String[] args){
        UITheme.apply();
        try{ DBConnection.initializeDatabase(); SwingUtilities.invokeLater(LoginFrame::new); }
        catch(Exception e){ JOptionPane.showMessageDialog(null,"Database startup failed:\n"+e.getMessage()+"\n\nCheck MySQL and DBConnection password.","Startup Error",JOptionPane.ERROR_MESSAGE); }
    }
}
