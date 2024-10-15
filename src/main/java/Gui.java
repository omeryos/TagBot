import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Formatter;

public class Gui {
    private JTextArea logArea;
    TagUserBot bot;
    private JLabel statusLabel;
    private Icon greenIcon;
    private Icon redIcon;
    public Gui(TagUserBot bot) {
        this.bot = bot;

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);
        frame.setTitle("Tagging Bot 1.3.2");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);

        // Create icons
        JPanel statusPanel = new JPanel();
        greenIcon = new ColorIcon(Color.GREEN);
        redIcon = new ColorIcon(Color.RED);

        // Initialize and place the status label in the frame
        statusLabel = new JLabel("Bot Status: Stopped", redIcon, JLabel.LEFT);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.NORTH);

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
            JOptionPane.showMessageDialog(frame, "Tagging bot 1.3.2 with GUI, created by omer", "About", JOptionPane.INFORMATION_MESSAGE);
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

        //  process the entered command
        JButton processButton = new JButton("CMD");
        processButton.setPreferredSize(new Dimension(60, 30));
        processButton.addActionListener(e -> {
            String command = textArea.getText();
            handleCommand(command);
            textArea.setText("");
        });
        processButton.setToolTipText("Process the command");
        // clear the log
        JButton clearButton = new JButton("CLR");
        clearButton.addActionListener(e -> {
            clearLog();
        });
        clearButton.setToolTipText("Clear the log");
        // adding the command and exec command button components to the main panel
        mainPanel.add(textArea);
        mainPanel.add(processButton);



        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(6);
        logArea.setColumns(30);
        logArea.createToolTip();
        JScrollPane scrollPane = new JScrollPane(logArea);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start Bot");
        startButton.addActionListener(e -> {
            if(!bot.isBotActive){
                logMessage("Bot started");
                bot.isBotActive = true;
                bot.startScheduler();
                setBotRunning(bot.isBotActive);
            }else{
                logMessage("Bot is already running!");
            }

        });

        JButton stopButton = new JButton("Stop Bot");
        stopButton.addActionListener(e -> {
            if(bot.isBotActive){
                logMessage("Bot stopped");
                bot.isBotActive = false;
                bot.stopScheduler();
                setBotRunning(bot.isBotActive);
            }else{
                logMessage("Bot is already down!");
            }

        });

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        RemoveChatPanel removeChatPanel = new RemoveChatPanel(bot,this);
        AddChatPanel addChatPanel = new AddChatPanel(bot,this);
        ChangeTextPanel changeTextPanel = new ChangeTextPanel(bot,this);
        AddUserPanel addUserPanel = new AddUserPanel(bot,this);
        RemoveUserPanel removeUserPanel = new RemoveUserPanel(bot,this);


        // add all the panels to the frame
        frame.add(mainPanel);
        frame.add(addChatPanel);
        frame.add(removeChatPanel);
        frame.add(changeTextPanel);
        frame.add(addUserPanel);
        frame.add(removeUserPanel);
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
            bot.addUserToTaggedList(username, bot.myChatId);  // Simulate chat command
            logMessage("Added username: " + username);
        } else if (command.startsWith("/removeusername ")) {
            String username = command.split(" ")[1];
            bot.removeUserFromTaggedList(username, bot.myChatId);  // Simulate chat command
            logMessage("Removed username: " + username);
        } else if (command.startsWith("/changetext,")) {
            String newMessage = command.split(",")[1];
            bot.changeText(newMessage, bot.myChatId);  // Simulate chat command
            logMessage("Changed base message to: " + newMessage);
        } else if (command.equals("/start")) {
            if(!bot.isBotActive){
                bot.isBotActive = true;
                bot.startScheduler();
                setBotRunning(bot.isBotActive);
                logMessage("Bot started");
            }else{
                logMessage("Bot is already running!");
            }

        } else if (command.equals("/stop")) {
            if(bot.isBotActive){
                bot.isBotActive = false;
                bot.stopScheduler();
                setBotRunning(bot.isBotActive);
                logMessage("Bot stopped");
            }else{
                logMessage("Bot is already down!");
            }

        } else if (command.startsWith("/addchat")) {
            String chatIdToAdd = command.split(" ")[1];
            if (bot.chatIdsList.contains(chatIdToAdd)) {
                logMessage("chat id " + chatIdToAdd + " is already on the list");
            } else {
                bot.chatIdsList.add(chatIdToAdd);
                logMessage("chat Added");
            }
        }else if(command.startsWith("/removechat")){
            String chatIdToRemove = command.split(" ")[1];
            if (bot.chatIdsList.contains(chatIdToRemove)) {
                bot.chatIdsList.remove(chatIdToRemove);
                logMessage("chat id " + chatIdToRemove + " was removed from the list");
            } else {
                logMessage("chat id " + chatIdToRemove + " was NOT on the list");
            }
        }
        else {
            logMessage("Unknown command: " + command);
        }
    }

    /**
     * Method to log messages to the GUI log area.
     */
    public void logMessage(String message) {
        logArea.append(bot.logCurrentTime() + ": " +message + "\n");
    }
    protected void clearTextArea(JTextField field){
        field.setText("");
    }
    private void clearLog() {
        logArea.setText("");
    }
    public void setBotRunning(boolean isRunning) {
        if (isRunning) {
            statusLabel.setIcon(greenIcon);
            statusLabel.setText("Bot Status: Running");
        } else {
            statusLabel.setIcon(redIcon);
            statusLabel.setText("Bot Status: Stopped");
        }
    }
}
