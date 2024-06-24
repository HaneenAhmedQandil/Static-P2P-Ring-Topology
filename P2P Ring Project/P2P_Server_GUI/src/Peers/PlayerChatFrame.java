package Peers;

import Server.Server;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class PlayerChatFrame extends JFrame {

    private Player peer;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton leaveButton;
    private JButton instructionsButton;

    public PlayerChatFrame(Player peer) {
        this.peer = peer;
        setTitle(peer.peerName);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JLabel headline = new JLabel("Welcome to Game Chat! ", SwingConstants.CENTER);
        headline.setBorder(new EmptyBorder(10, 10, 10, 10));
        headline.setFont(new Font("Serif", Font.BOLD, 20));
        northPanel.add(headline);

        add(northPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setBackground(new Color(173, 216, 230));
        chatArea.setEditable(false);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new RoundedBorder(10));
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(chatPanel, BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Send Message");
        leaveButton = new JButton("Leave");
        instructionsButton = new JButton("Instructions");

        ActionListener showInstructionsAction = e -> {
            String instructions = "<html>Instructions:<br/>"
                    + "1. To send a message to all users, just type your message and press Enter.<br/>"
                    + "2. To send a private message, start your message with '@username ' (replace 'username' with the recipient's username).<br/>"
                    + "3. To leave the chat, click the 'Leave' button or type 'bye'.</html>";
            JOptionPane.showMessageDialog(this, instructions);
        };

        instructionsButton.addActionListener(showInstructionsAction);

        ActionListener sendMessageAction = e -> {
            String content = inputField.getText();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a message");
                return;
            }
            if (content.equals("@") || content.equals("@ ")) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a name!");
                inputField.setText("");
                return;
            }
            boolean isPrivate = content.startsWith("@");
            String recipient = isPrivate ? content.split(" ")[0].substring(1) : "All";
            content = isPrivate ? content.substring(content.indexOf(" ") + 1) : content;
            if (isPrivate && (recipient.trim().isEmpty() || content.trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Invalid recipient or message. Please enter a valid username and a message!");
                inputField.setText("");
                return;
            }
            if (content.equalsIgnoreCase("bye")) {
                peer.removeSelf();
                dispose();
            } else {
                Message message = new Message(peer.peerName, recipient, content, isPrivate);
                peer.sendMessage(message);
                inputField.setText("");
            }
        };

        ActionListener leaveChatAction = e -> {
            peer.removeSelf();
            dispose();
        };

        inputField.addActionListener(sendMessageAction);
        sendButton.addActionListener(sendMessageAction);
        leaveButton.addActionListener(leaveChatAction);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputField, BorderLayout.CENTER);
        southPanel.add(sendButton, BorderLayout.EAST);
        southPanel.add(leaveButton, BorderLayout.WEST);
        southPanel.add(instructionsButton, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);

        inputField.requestFocus();
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    class RoundedBorder extends AbstractBorder {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = this.radius;
            return insets;
        }
    }

}
