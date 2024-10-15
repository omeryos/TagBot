import javax.swing.*;
import java.awt.*;

public class ChangeTextPanel extends JPanel {
    private TagUserBot bot;
    private JTextField baseMessageField;
    private Gui gui;

    public ChangeTextPanel(TagUserBot bot, Gui gui) {
        this.bot = bot;
        this.gui = gui;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // set maximum size of the panel to allow it to stretch across the available width
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setPreferredSize(new Dimension(400, 50)); // Optional preferred size

        // add vertical spacing using rigid areas
        add(Box.createRigidArea(new Dimension(0, 5)));  // Top margin

        // create an inner panel with FlowLayout for horizontal alignment
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Ensure inner panel stretches horizontally
        innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        innerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));

        // add label
        JLabel addChatLabel = new JLabel("Change message:");
        innerPanel.add(addChatLabel);

        // add text field
        baseMessageField = new JTextField(15);
        baseMessageField.setPreferredSize(new Dimension(150, 25));  // Set preferred size for the text field
        innerPanel.add(baseMessageField);

        // add button
        JButton addChatButton = new JButton("Change message");
        addChatButton.setPreferredSize(new Dimension(100,20));
        addChatButton.addActionListener(e -> {
            changeTextAction(baseMessageField.getText());
            gui.clearTextArea(baseMessageField);
        });
        innerPanel.add(addChatButton);

        // Add the inner panel to the main panel
        add(innerPanel);

        // Add vertical spacing after the components
        add(Box.createRigidArea(new Dimension(0, 5)));  // Bottom margin
    }
    private void changeTextAction(String text){
        bot.baseMessage = text;
        bot.baseReplyMessage = text;
        bot.sendMessage(bot.myChatId,"Message and reply text changed to: " + text);
    }
}
