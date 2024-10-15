import javax.swing.*;
import java.awt.*;

public class RemoveChatPanel extends JPanel {
    private TagUserBot bot;
    private JTextField chatIdField;
    private Gui gui;

    public RemoveChatPanel(TagUserBot bot, Gui gui) {
        this.bot = bot;
        this.gui = gui;

        // Set BoxLayout for vertical arrangement
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Set maximum size of the panel to allow it to stretch across the available width
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setPreferredSize(new Dimension(400, 50)); // Optional preferred size

        // Add vertical spacing using rigid areas
        add(Box.createRigidArea(new Dimension(0, 5)));  // Top margin

        // Create an inner panel with FlowLayout for horizontal alignment
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Ensure inner panel stretches horizontally
        innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        innerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));


        JLabel removeChatLabel = new JLabel("Remove Chat:");
        innerPanel.add(removeChatLabel);

        chatIdField = new JTextField(15);
        chatIdField.setPreferredSize(new Dimension(150, 25));  // Set preferred size for the text field
        innerPanel.add(chatIdField);


        JButton removeChatButton = new JButton("Remove Chat");
        removeChatButton.addActionListener(e -> {
            removeChatAction();
            gui.clearTextArea(chatIdField);
        });
        innerPanel.add(removeChatButton);

        // Add the inner panel containing the label, text field, and button
        add(innerPanel);

        // Add vertical spacing after the components
        add(Box.createRigidArea(new Dimension(0, 5)));  // Bottom margin
    }

    private void removeChatAction() {
        String chatId = chatIdField.getText();
        if (!chatId.isEmpty()) {
            if (bot.chatIdsList.contains(chatId)) {
                bot.chatIdsList.remove(chatId);
                gui.logMessage("Removed chat ID: " + chatId);
            } else {
                gui.logMessage("Chat ID not found: " + chatId);
            }
        } else {
            gui.logMessage("Please enter a valid chat ID.");
        }
    }
}
