import javax.swing.*;
import java.awt.*;

public class AddUserPanel extends JPanel {
    private TagUserBot bot;
    private JTextField userNameField;
    private Gui gui;
    public AddUserPanel(TagUserBot bot, Gui gui){
        this.bot = bot;
        this.gui = gui;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // set maximum size of the panel to allow it to stretch across the available width
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setPreferredSize(new Dimension(400, 50));

        add(Box.createRigidArea(new Dimension(0, 5)));  // Top margin

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        innerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));

        //label
        JLabel addUserLabel = new JLabel("Add user:");
        innerPanel.add(addUserLabel);

        //text field
        userNameField = new JTextField(15);
        userNameField.setPreferredSize(new Dimension(150, 25));  // Set preferred size for the text field
        innerPanel.add(userNameField);

        //button
        JButton addChatButton = new JButton("Add");
        addChatButton.setPreferredSize(new Dimension(100,20));
        addChatButton.addActionListener(e -> bot.addUserToTaggedList(userNameField.getText(),bot.myChatId));
        innerPanel.add(addChatButton);

        add(innerPanel);

        // Add vertical spacing after the components
        add(Box.createRigidArea(new Dimension(0, 5)));  // Bottom margin
    }


}
