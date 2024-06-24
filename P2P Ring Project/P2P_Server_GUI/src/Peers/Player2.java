package Peers;
import javax.swing.SwingUtilities;

public class Player2 {
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new LoginFrame(6001, 6002));
    }
}