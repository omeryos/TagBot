import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
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


    private final String botToken = "1";

    protected Long myChatId = 1L;
    static ArrayList<String> taggedUsers = new ArrayList<>();
    static ArrayList<String> stickersToSend = new ArrayList<>();
    static ArrayList<String> animationsToSend = new ArrayList<>();
    static ArrayList<String> chatIdsList = new ArrayList<>();
    HashMap<String, List<Integer>> replyMap = new HashMap<>();
    String baseMessage = "Hello";
    String baseReplyMessage = "Hello";
    private ScheduledExecutorService scheduler;



    boolean isBotActive = false;

    private Gui gui;

    public TagUserBot() {

    }
    public void setGui(Gui gui) {
        this.gui = gui;
    }
    public static void addUsersToList() {

    }

    public static void addStickersToSend() {

    }

    public static void addChatIdsList() {

    }

    @Override
    public String getBotUsername() {
        return "TagBot";
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
                if(!isBotActive){
                    isBotActive = true;
                    startScheduler();
                    sendMessage(chatId, "bot activated");
                   gui.setBotRunning(isBotActive);
                }else{
                    sendMessage(chatId, "bot is already running");
                }

            } else if (messageText.equals("/stop")) {
                if(isBotActive){
                    isBotActive = false;
                    stopScheduler();
                    sendMessage(chatId, "bot stopped");
                    gui.setBotRunning(isBotActive);
                }else{
                    sendMessage(chatId, "bot is already down!");
                }

            } else if (messageText.startsWith("/addchat")) {
                String chatIdToAdd = messageText.split(" ")[1];
                if (chatIdsList.contains(chatIdToAdd)) {
                    sendMessage(chatId, "chat id " + chatIdToAdd + " is already on the list");
                } else {
                    chatIdsList.add(chatIdToAdd);
                    sendMessage(chatId, "chat Added");
                }

            }else if(messageText.startsWith("/removechat")){
                String chatIdToRemove = messageText.split(" ")[1];
                if (chatIdsList.contains(chatIdToRemove)) {
                    chatIdsList.remove(chatIdToRemove);
                    sendMessage(chatId, "chat removed from the list");
                } else {
                    sendMessage(chatId, "chat id " + chatIdToRemove +" was not on the list!");
                }
            }
        }
        if (update.getMessage().hasSticker()&& update.getMessage().getFrom().getId().equals(myChatId)) {
            String fileId = update.getMessage().getSticker().getFileId();
            if (stickersToSend.contains(fileId)) {
                stickersToSend.remove(fileId);
                sendMessage(myChatId, "sticker removed");
            } else {
                stickersToSend.add(update.getMessage().getSticker().getFileId());
                sendMessage(myChatId, "sticker added");
            }
        }
        if(update.getMessage().hasAnimation()&& update.getMessage().getFrom().getId().equals(myChatId)){
            String fileId = update.getMessage().getAnimation().getFileId();
            if (animationsToSend.contains(fileId)) {
                animationsToSend.remove(fileId);
                sendMessage(myChatId, "animation removed");
            } else {
                animationsToSend.add(update.getMessage().getAnimation().getFileId());
                sendMessage(myChatId, "animation added");
            }
        }

    }

    public void addUserToTaggedList(String username, Long chatId) {
        if(username.startsWith("@")){
            if (!taggedUsers.contains(username)) {
                taggedUsers.add(username);
                sendMessage(chatId, "Added username: " + username);
            } else {
                sendMessage(chatId, "Username already exists in the list: " + username);
            }
        }else{
            sendMessage(chatId, username + " is not a valid username");
        }

    }

    public void removeUserFromTaggedList(String username, Long chatId) {
        if(username.startsWith("@")){
            if (taggedUsers.contains(username)) {
                taggedUsers.remove(username);
                sendMessage(chatId, "Removed username: " + username);
            } else {
                sendMessage(chatId, "Username not found in the list: " + username);
            }
        }else{
            sendMessage(chatId, username + " is not a valid username");
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
        return (currentTime.format(formatter));
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
            System.out.println("Scheduler already running");
            gui.logMessage("Scheduler already running");
            return;
        }
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::sendTagMessage, 0, 15, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::replyToUserInGroup, 2, 15, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::sendSticker, 5, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::sendAnimation, 7, 20, TimeUnit.SECONDS);
        isBotActive = true;
    }

    // Method to stop the scheduler
    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            isBotActive = false;
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
    public void sendAnimation() {
        if (isBotActive) {
            for(String chatId : chatIdsList){
                for (String stickerId : animationsToSend) {
                    SendAnimation animation = new SendAnimation();
                    animation.setChatId(chatId);
                    animation.setAnimation(new InputFile(stickerId));  // Use the retrieved file_id
                    try {
                        execute(animation);
                        System.out.println("Sent animation " + stickerId);
                    } catch (TelegramApiException e) {
                        System.err.println("Failed to send animation: " + e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        }
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
