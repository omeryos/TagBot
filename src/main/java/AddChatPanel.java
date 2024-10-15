import javax.swing.*;
import java.awt.*;

public class AddChatPanel extends JPanel {
    private TagUserBot bot;
    private JTextField chatIdField;
    private Gui gui;

    public AddChatPanel(TagUserBot bot, Gui gui) {
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

        // Add label
        JLabel addChatLabel = new JLabel("Add Chat: ");
        innerPanel.add(addChatLabel);

        // Add text field
        chatIdField = new JTextField(15);
        chatIdField.setPreferredSize(new Dimension(150, 25));  // Set preferred size for the text field
        innerPanel.add(chatIdField);

        // Add button
        JButton addChatButton = new JButton("Add Chat");
        addChatButton.addActionListener(e -> addChatAction());
        innerPanel.add(addChatButton);

        // Add the inner panel to the main panel
        add(innerPanel);

        // Add vertical spacing after the components
        add(Box.createRigidArea(new Dimension(0, 5)));  // Bottom margin
    }

    /**
     * This method handles the action when the "Add Chat" button is pressed.
     */
    private void addChatAction() {
        String chatId = chatIdField.getText();
        if (!chatId.isEmpty()) {
            if (bot.chatIdsList.contains(chatId)) {
                gui.logMessage("Chat ID already exists: " + chatId);
            } else {
                bot.chatIdsList.add(chatId);  // Add chat ID to the list
                gui.logMessage("Added chat ID: " + chatId);  // Log to the GUI log area
            }
        } else {
            gui.logMessage("Please enter a valid chat ID.");
        }
    }
}
