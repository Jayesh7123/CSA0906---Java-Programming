import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public final class UITheme {
    public static final Color BG = new Color(245,247,251);
    public static final Color SIDEBAR = new Color(29,43,83);
    public static final Color PRIMARY = new Color(45,91,255);
    public static final Color PRIMARY_DARK = new Color(31,70,196);
    public static final Color SUCCESS = new Color(34,139,94);
    public static final Color DANGER = new Color(210,65,65);
    public static final Color WARNING = new Color(235,153,38);
    public static final Color TEXT = new Color(38,45,57);
    public static final Color MUTED = new Color(110,120,135);
    public static final Color CARD = Color.WHITE;
    private UITheme(){}
    public static void apply() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Table.rowHeight", 28);
    }
    public static Border roundedBorder(Color color, int radius){
        return new LineBorder(color,1,true);
    }
    public static JPanel card(LayoutManager layout){
        JPanel p=new JPanel(layout);
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(225,229,236),1,true), new EmptyBorder(16,16,16,16)));
        return p;
    }
    public static JButton button(String text, Color bg){
        JButton b=new JButton(text);
        b.setForeground(Color.WHITE); b.setBackground(bg); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,18,10,18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    public static JLabel heading(String text,int size){
        JLabel l=new JLabel(text); l.setForeground(TEXT); l.setFont(new Font("Segoe UI",Font.BOLD,size)); return l;
    }
    public static JLabel muted(String text){
        JLabel l=new JLabel(text); l.setForeground(MUTED); return l;
    }
    public static void styleTable(JTable table){
        table.setShowVerticalLines(false); table.setGridColor(new Color(235,238,242));
        table.getTableHeader().setBackground(new Color(235,240,255));
        table.getTableHeader().setForeground(TEXT); table.setSelectionBackground(new Color(220,230,255));
        table.setSelectionForeground(TEXT);
    }
}
