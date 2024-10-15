import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TagUserBot extends TelegramLongPollingBot {

    // Replace with your Telegram bot token
    private final String botToken = "1";

    // Replace with the group or chat ID where the bot will send messages

    protected Long myChatId = 1L;
    static ArrayList<String> taggedUsers = new ArrayList<>();
    static ArrayList<String> stickersToSend = new ArrayList<>();
    static ArrayList<String> chatIdsList = new ArrayList<>();
    HashMap<String, List<Integer>> replyMap = new HashMap<>();
    String baseMessage = "Hello";
    String baseReplyMessage = "Hello";
    private ScheduledExecutorService scheduler;



    protected Long internalMsgChatid = 0L;
    boolean isBotActive = true;
    private Gui gui;

    public TagUserBot() {

    }
    public void setGui(Gui gui) {
        this.gui = gui;
    }
    public static void addUsersToList() {
//        taggedUsers.add("@Labogustavo");
//        taggedUsers.add("@zlabiazella");
//        taggedUsers.add("@ethana");
    }

    public static void addStickersToSend() {
//        stickersToSend.add("CAACAgQAAyEFAASKkYiOAALeN2b-piZiK9gz0yJIBggqPbALe_dKAAK_FwAC9Rf4U_wbFJ7dXy67NgQ");
//        stickersToSend.add("CAACAgQAAxkBAAEuIZdm_rMSEiq7AAEcp0gGcqHp-SJ2R78AAkgUAALTpalSgtPs70pTccg2BA");
//        stickersToSend.add("CAACAgQAAyEFAASKkYiOAALefWb-pqlZLhtEIPYeuVooQMqvsWXeAAJvFQACTjHxUy5lBiz_JqPHNgQ");
    }

    public static void addChatIdsList() {

    }

    @Override
    public String getBotUsername() {
        return "TagBot";  // Replace with your bot's username
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
    public void replyToUserInGroup() {
        if (isBotActive) {
            for (String chat : chatIdsList) {
                if (replyMap.containsKey(chat)) {
                    for (Integer msgId : replyMap.get(chat)) {
                        SendMessage message = new SendMessage();
                        message.setChatId(chat.toString());
                        message.setText(baseReplyMessage);
                        message.setReplyToMessageId(msgId);

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();

        if (update.hasMessage() && update.getMessage().hasText() && messageText.startsWith("/addreply") && update.getMessage().getFrom().getId().equals(myChatId) ) {
            String chatId = String.valueOf(update.getMessage().getChatId());
            if (update.getMessage().getReplyToMessage() != null) {
                Integer originalMessageId = update.getMessage().getReplyToMessage().getMessageId();
                replyMap.putIfAbsent(chatId, new ArrayList<>());  // Initialize if not already present
                replyMap.get(chatId).add(originalMessageId);
                System.out.println("Added reply target");
            }
        }

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isUserMessage()&& update.getMessage().getFrom().getId().equals(myChatId)) {
            Long chatId = update.getMessage().getChatId();
            internalMsgChatid = chatId;
            if (messageText.startsWith("/addusername ")) {
                String usernameToAdd = messageText.split(" ")[1];  // Extract the username
                addUserToTaggedList(usernameToAdd, chatId);
            } else if (messageText.startsWith("/removeusername ")) {
                String usernameToRemove = messageText.split(" ")[1];  // Extract the username
                removeUserFromTaggedList(usernameToRemove, chatId);
            } else if (messageText.startsWith("/changetext,")) {
                baseMessage = messageText.split(",")[1]; //extract text
                changeText(baseMessage, chatId);
            } else if (messageText.equals("/start")) {
                isBotActive = true;
                startScheduler();
                sendMessage(chatId, "bot activated");
            } else if (messageText.equals("/stop")) {
                isBotActive = false;
                stopScheduler();
                sendMessage(chatId, "bot stopped");
            } else if (messageText.startsWith("/addchat")) {
                String chatIdToAdd = messageText.split(" ")[1];
                if (chatIdsList.contains(chatIdToAdd)) {
                    chatIdsList.remove(chatIdToAdd);
                    sendMessage(chatId, "chat removed from the list");
                } else {
                    chatIdsList.add(chatIdToAdd);
                    sendMessage(chatId, "chat Added");
                }

            }
        }
        if (update.getMessage().hasSticker()&& update.getMessage().getFrom().getId().equals(myChatId)) {
            Long chatId = update.getMessage().getChatId();
            internalMsgChatid = chatId;
            String fileId = update.getMessage().getSticker().getFileId();
            if (stickersToSend.contains(fileId)) {
                stickersToSend.remove(fileId);
                sendMessage(myChatId, "sticker removed");
            } else {
                stickersToSend.add(update.getMessage().getSticker().getFileId());
                sendMessage(myChatId, "sticker added");
            }
        }
    }

    public void addUserToTaggedList(String username, Long chatId) {
        if (!taggedUsers.contains(username)) {
            taggedUsers.add(username);
            sendMessage(chatId, "Added username: " + username);
        } else {
            sendMessage(chatId, "Username already exists in the list: " + username);
        }
    }

    public void removeUserFromTaggedList(String username, Long chatId) {
        if (taggedUsers.contains(username)) {
            taggedUsers.remove(username);
            sendMessage(chatId, "Removed username: " + username);
        } else {
            sendMessage(chatId, "Username not found in the list: " + username);
        }
    }

    public void changeText(String text, Long chatId) {
        sendMessage(chatId, "text changed to: " + text);
    }

    // Helper method to send a message
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        gui.logMessage(text);

        try {
            execute(message);  // Send the message
            System.out.println("sent message: " + text);
        } catch (TelegramApiException e) {
            System.out.println("failed to send message");
            e.printStackTrace();
        }
    }

    public String logCurrentTime() {
        // Get the current time
        LocalTime currentTime = LocalTime.now();

        // Define a formatter for the HH:mm:ss format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the current time and print it
        return ("Tagged at " + currentTime.format(formatter));
    }

    // Method to send a message tagging the user
    public void sendTagMessage() {
        if (isBotActive) {
        for(String chatId : chatIdsList){
            for (String user : taggedUsers) {
                String messageText = "yo, " + user + ", " + baseMessage;
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(messageText);
                try {
                    execute(message);  // Send the message

                    System.out.println("Tagged " + logCurrentTime());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                } finally {
                    message = null;
                }
            }
        }

        }
    }

    public void startScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::sendTagMessage, 0, 15, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::replyToUserInGroup, 2, 15, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::sendSticker, 5, 30, TimeUnit.SECONDS);  // 5 seconds after tags
    }

    // Method to stop the scheduler
    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Scheduler stopped.");
        }
    }

    public void sendSticker() {
        if (isBotActive) {
            for(String chatId : chatIdsList){
                for (String stickerId : stickersToSend) {
                    SendSticker sticker = new SendSticker();
                    sticker.setChatId(chatId);
                    sticker.setSticker(new InputFile(stickerId));  // Use the retrieved file_id
                    try {
                        execute(sticker);
                        System.out.println("Sent sticker " + stickerId);
                    } catch (TelegramApiException e) {
                        System.err.println("Failed to send sticker: " + e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        }
    }
    public Long getInternalMsgChatid() {
        return internalMsgChatid;
    }
    public static ArrayList<String> getChatIdsList() {
        return chatIdsList;
    }
    public static void main(String[] args) {

        try {
            // Create the bot and GUI objects and link them
            TagUserBot bot = new TagUserBot();  // Create the bot first
            Gui gui = new Gui(bot);  // Pass the bot to the GUI during construction
            bot.setGui(gui);  // Set the GUI in the bot

            // Register the bot with Telegram API
            registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to register the bot with Telegram API.
     */
    private static void registerBot(TagUserBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }

}
