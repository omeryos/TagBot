import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gui {
    private JTextArea logArea;  // bot log information
    TagUserBot bot;

    public Gui(TagUserBot bot) {
        this.bot = bot;

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);
        frame.setTitle("Tagging Bot 1.31");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.getGlassPane().setBackground(Color.blue);
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);

        //menu bar
        JMenuBar jMenuBar = new JMenuBar();
        frame.setJMenuBar(jMenuBar);

        JMenu jMenuLog = new JMenu("Log");
        JMenu jMenuHelp = new JMenu("Help");

        jMenuBar.add(jMenuLog);
        jMenuBar.add(jMenuHelp);


        JMenuItem logMenuItem = new JMenuItem("Get log file");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Tagging bot 1.31 with GUI, created by omer", "About", JOptionPane.INFORMATION_MESSAGE);
        });
       // produce a file with the logging screen contents
        logMenuItem.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Log File");  // Set dialog title

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
            fileChooser.setFileFilter(filter);

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy_HH:mm:ss");
            String dateString = formatter.format(new Date());

            fileChooser.setSelectedFile(new java.io.File("Log - " + dateString + ".txt"));

            int userSelection = fileChooser.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Get the file that was selected
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Check if the file has the correct extension, if not, add ".txt" as the default extension
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".txt");
                }

                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(logArea.getText());
                    JOptionPane.showMessageDialog(frame, "Log file saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving log file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        jMenuLog.add(logMenuItem);
        jMenuHelp.add(aboutMenuItem);


        // main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.blue);
        mainPanel.setLayout(new FlowLayout());
        mainPanel.setPreferredSize(new Dimension(420,50));

        // Text area for user input
        TextArea textArea = new TextArea("Enter command here...");
        textArea.setPreferredSize(new Dimension(200, 50));
        textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        // Button to process the entered command
        Button processButton = new Button("Process Command");
        processButton.setPreferredSize(new Dimension(150, 30));
        processButton.addActionListener(e -> {
            String command = textArea.getText();
            handleCommand(command);
            textArea.setText("");
        });

        // adding the command and exec command button components to the main panel
        mainPanel.add(textArea);
        mainPanel.add(processButton);


        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(10);
        logArea.setColumns(30);
        logArea.createToolTip();
        JScrollPane scrollPane = new JScrollPane(logArea);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start Bot");
        startButton.addActionListener(e -> {
            logMessage("Bot started");
            bot.isBotActive = true;
            bot.startScheduler();
        });

        JButton stopButton = new JButton("Stop Bot");
        stopButton.addActionListener(e -> {
            logMessage("Bot stopped");
            bot.isBotActive = false;
            bot.stopScheduler();
        });

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        RemoveChatPanel removeChatPanel = new RemoveChatPanel(bot,this);
        AddChatPanel addChatPanel = new AddChatPanel(bot,this);
        ChangeTextPanel changeTextPanel = new ChangeTextPanel(bot,this);

        // add all the panels to the frame
        frame.add(mainPanel);
        frame.add(addChatPanel);
        frame.add(removeChatPanel);
        frame.add(changeTextPanel);
        frame.add(scrollPane);
        frame.add(controlPanel);
        frame.setVisible(true);
    }

    /**
     * Method to handle the command entered in the text area.
     */
    private void handleCommand(String command) {
        if (command.startsWith("/addusername ")) {
            String username = command.split(" ")[1];
            bot.addUserToTaggedList(username, bot.getInternalMsgChatid());  // Simulate chat command
            logMessage("Added username: " + username);
        } else if (command.startsWith("/removeusername ")) {
            String username = command.split(" ")[1];
            bot.removeUserFromTaggedList(username, bot.getInternalMsgChatid());  // Simulate chat command
            logMessage("Removed username: " + username);
        } else if (command.startsWith("/changetext,")) {
            String newMessage = command.split(",")[1];
            bot.changeText(newMessage, bot.getInternalMsgChatid());  // Simulate chat command
            logMessage("Changed base message to: " + newMessage);
        } else if (command.equals("/start")) {
            bot.isBotActive = true;
            bot.startScheduler();
            logMessage("Bot started");
        } else if (command.equals("/stop")) {
            bot.isBotActive = false;
            bot.stopScheduler();
            logMessage("Bot stopped");
        } else if (command.startsWith("/addchat")) {
            String chatIdToAdd = command.split(" ")[1];
            if (bot.chatIdsList.contains(chatIdToAdd)) {
                bot.chatIdsList.remove(chatIdToAdd);
                logMessage("chat removed from the list");
            } else {
                bot.chatIdsList.add(chatIdToAdd);
                logMessage("chat Added");
            }
        } else {
            logMessage("Unknown command: " + command);
        }
    }

    /**
     * Method to log messages to the GUI log area.
     */
    public void logMessage(String message) {
        logArea.append(message + "\n");
    }
}
