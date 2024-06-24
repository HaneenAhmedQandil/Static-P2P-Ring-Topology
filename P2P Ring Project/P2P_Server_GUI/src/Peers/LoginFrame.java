package Peers;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private int port;
    private int nextPort;
    private JTextField nameField;
    private JButton joinButton;

    public LoginFrame(int port, int nextPort) {
        setSize(800, 600);
        getContentPane().setBackground(new Color(173, 216, 230));

        setTitle("Welcome to Multiplayer Online Game");
        this.port = port;
        this.nextPort = nextPort;

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome to Multiplayer Online Game");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
        add(welcomeLabel);

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Enter your nickname:");
        nameLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        fieldPanel.add(nameLabel);

        nameField = new JTextField(20);
        fieldPanel.add(nameField);
        add(fieldPanel);

        joinButton = new JButton("Join the game chat");
        joinButton.setPreferredSize(new Dimension(150, 20));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(joinButton);
        add(buttonPanel);
        joinButton.addActionListener(e -> {
            String name = nameField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty. Please enter a name.");
                return;
            }

            try ( Socket socket = new Socket("localhost", 4000);  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(name);
                String response = in.readLine();
                if ("EXISTS".equals(response)) {
                    JOptionPane.showMessageDialog(this, "Name already exists. Enter a different name.");
                    nameField.setText("");
                    return;
                }
                System.out.println("Connected to the server");
            } catch (IOException ex) {
                System.out.println("Failed to connect to the server");
                ex.printStackTrace();
                return;
            }

            Player peer = new Player(name, port, "localhost", nextPort);
            PlayerChatFrame peerFrame = new PlayerChatFrame(peer);
            peerFrame.setLocationRelativeTo(null);
            peer.setPeerFrame(peerFrame);
            peer.start();
            dispose();
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

}
