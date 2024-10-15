import javax.swing.*;
import java.awt.*;

public class RemoveUserPanel extends JPanel {

        private TagUserBot bot;
        private JTextField userNameField;
        private Gui gui;
        public RemoveUserPanel(TagUserBot bot, Gui gui){
            this.bot = bot;
            this.gui = gui;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setPreferredSize(new Dimension(400, 50));
            add(Box.createRigidArea(new Dimension(0, 5)));

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            innerPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));

            //label
            JLabel addUserLabel = new JLabel("Remove user:");
            innerPanel.add(addUserLabel);

            //text field
            userNameField = new JTextField(15);
            userNameField.setPreferredSize(new Dimension(150, 25));
            innerPanel.add(userNameField);

            //button
            JButton addChatButton = new JButton("Remove");
            addChatButton.setPreferredSize(new Dimension(100,20));
            addChatButton.addActionListener(e -> bot.removeUserFromTaggedList(userNameField.getText(),bot.myChatId));
            innerPanel.add(addChatButton);

            add(innerPanel);
            add(Box.createRigidArea(new Dimension(0, 5)));  // Bottom margin
        }


    }


