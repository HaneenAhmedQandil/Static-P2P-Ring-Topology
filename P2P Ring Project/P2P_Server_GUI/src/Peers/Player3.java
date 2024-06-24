package Peers;
import javax.swing.SwingUtilities;

public class Player3 
{

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new LoginFrame(6002, 6003));
    }
}
