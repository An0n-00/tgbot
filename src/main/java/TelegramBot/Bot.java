package TelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.*;

import java.sql.*;
import java.util.List;
import java.util.Objects;


public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return ""; // Replace with your bot's username
    }

    @Override
    public String getBotToken() {
        return ""; // Replace with your bot's token
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    public void sendText(Long who, String what, boolean reply, Integer replyToMessageId) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .build();
        if (reply && replyToMessageId != null) {
            sm.setReplyToMessageId(replyToMessageId);
        }
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }



    private void executeSQLStatement(Update update) {
        // JDBC connection parameters
        var user = update.getMessage().getFrom().getId();
        var firstname = update.getMessage().getFrom().getFirstName();
        var lastname = update.getMessage().getFrom().getLastName();
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        String jdbcUrl = "jdbc:mariadb://localhost:3306/dbname";
        String username = "root";
        String password = "";

        // Connect to the database and insert the message and user details
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            // Check if the user exists in the users table
            String checkUserSql = "SELECT userid FROM users WHERE userid = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserSql)) {
                checkUserStatement.setLong(1, user);
                ResultSet resultSet = checkUserStatement.executeQuery();

                if (!resultSet.next()) {
                    // User does not exist, insert into the users table
                    String insertUserSql = "INSERT INTO users (userid, firstname, lastname) VALUES (?, ?, ?)";
                    try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserSql)) {
                        insertUserStatement.setLong(1, user);
                        insertUserStatement.setString(2, firstname);
                        insertUserStatement.setString(3, lastname);
                        insertUserStatement.executeUpdate();
                    }
                }

                // Insert message into the message table
                String insertMessageSql = "INSERT INTO message (msg, userid, groupid) VALUES (?, ?, ?)";
                try (PreparedStatement insertMessageStatement = connection.prepareStatement(insertMessageSql)) {
                    insertMessageStatement.setString(1, messageText);
                    insertMessageStatement.setLong(2, user);
                    insertMessageStatement.setLong(3, chatId);
                    insertMessageStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }


    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;
    public void genKbs() {
        var next = InlineKeyboardButton.builder()
                .text("Next").callbackData("next")
                .build();

        var back = InlineKeyboardButton.builder()
                .text("Back").callbackData("back")
                .build();

        var url = InlineKeyboardButton.builder()
                .text("Tutorial")
                .url("https://core.telegram.org/bots/api")
                .build();
        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next)).build();

        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back))
                .keyboardRow(List.of(url))
                .build();
    }
    public void sendButtons(Long who, String txt, InlineKeyboardMarkup kb){

        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        genKbs();
//        sendButtons(chatId, "test", keyboardM1);
        var msg = update.getMessage();
        var user = msg.getFrom();
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (messageText.startsWith("/start")) {
                    sendText(chatId, "Hello! Welcome to " + getBotUsername() + ". I would reccomend you starting me in PM ", true, msg.getMessageId());
                }
            } else if (msg.isUserMessage() && messageText.startsWith("/start")) {
                sendText(chatId, "Hello " + user.getUserName() +"! Welcome to " + getBotUsername() + ". You can start using commands now. Read the /help ", true, msg.getMessageId());
            }
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (messageText.startsWith("/help")) {
                    sendText(chatId, """
                            This is a simple bot showing you all your Information from Telegram and cool other stuff

                            Commands:
                            /id - gives your User ID
                            /all - shows all Information about you via Bot

                             Advanced: /allraw - will give you all the information raw.""", true, msg.getMessageId());
                }

            } else if (msg.isUserMessage() && messageText.startsWith("/help")) {
                sendText(chatId, """
                        This is a simple bot showing you all your Information from Telegram.

                        Commands:
                        /id - gives your User ID
                        /all - shows all Information about you via Bot

                        Advanced:\s
                        /allraw - will give you all the information raw.""", true, msg.getMessageId());
            }
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (messageText.startsWith("/beta")) {
                    sendText(chatId, "I am currently working on Buttons!", true, msg.getMessageId());
                    sendButtons(chatId, "test", keyboardM1);
                    sendButtons(chatId, "test", keyboardM2);
//                    executeSQLStatement(update);
                }

            } else if (msg.isUserMessage() && messageText.startsWith("/beta")) {
                sendText(chatId, "I am currently working on Buttons!", true, msg.getMessageId());
                sendButtons(chatId, "test", keyboardM1);
                sendButtons(chatId, "test", keyboardM2);
//                executeSQLStatement(update);
            }
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (Objects.equals(messageText, "/all")) {
                    sendText(chatId, "ID = " + user.getId()
                            + "\nFirst Name = " + user.getFirstName()
                            + "\nLast Name = " + user.getLastName()
                            + "\nUser Name = @" + user.getUserName()
                            + "\nPlease note that if you do not have a public username, ignore the line above."
                            + "\nLanguage Code = " + user.getLanguageCode()
                            + "\n(is bot) = " + user.getIsBot()
                    , true, msg.getMessageId());
                }

            } else if (msg.isUserMessage() && Objects.equals(messageText, "/all")) {
                sendText(chatId, "ID = " + user.getId()
                        + "\nFirst Name = " + user.getFirstName()
                        + "\nLast Name = " + user.getLastName()
                        + "\nUser Name = @" + user.getUserName()
                        + "\nPlease note that if you do not have a public username, ignore the line above." +
                        "\nLanguage Code = " + user.getLanguageCode()
                        + "\n(is bot) = " + user.getIsBot()
                , true, msg.getMessageId());
            }
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (messageText.startsWith("/id")) {
                    sendText(chatId, "Your personal ID is: " + user.getId()
                            + "\nThis Chat ID is: " + msg.getChatId()
                            + "\n\nIf you are in PM with the bot. The two IDs should be the same."
                    , true, msg.getMessageId());
                }
            } else if (msg.isUserMessage() && messageText.startsWith("/id")) {
                sendText(chatId, "Your personal ID is: " + user.getId()
                        + "\nThis Chat ID is: " + msg.getChatId()
                        + "\n\nIf you are in PM with the bot. The two IDs should be the same."
                , true, msg.getMessageId());
            }
            if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                if (messageText.startsWith("/allraw")) {
                    sendText(chatId, "this will result in a very long message. Please use /all for a better view", true, msg.getMessageId());
                }
            } else if (msg.isUserMessage() && messageText.startsWith("/allraw")) {
                sendText(chatId, update.toString(), true, msg.getMessageId());
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId_button = update.getCallbackQuery().getFrom().getId();

            switch (callbackData) {
                case "next":
                    // Handle 'Next' button press
                    sendText(chatId_button, "Next button pressed", true, update.getCallbackQuery().getMessage().getMessageId());
                    break;
                case "back":
                    // Handle 'Back' button press
                    sendText(chatId_button, "Back button pressed", true, update.getCallbackQuery().getMessage().getMessageId());
                    break;
                default:
                    // Handle unknown button press
                    sendText(chatId_button, "Unknown button pressed", true, update.getCallbackQuery().getMessage().getMessageId());
                    break;
            }
        }
    }




}