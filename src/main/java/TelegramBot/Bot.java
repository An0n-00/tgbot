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

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;

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
        System.out.println(update.toString() + "\n\n==============================\n\n");

        // VARIABLES DECLARATION
        var next = InlineKeyboardButton.builder()
                .text("Next")
                .callbackData("next")
                .build();

        var back = InlineKeyboardButton.builder()
                .text("Back")
                .callbackData("back")
                .build();

        var url = InlineKeyboardButton.builder()
                .text("Tutorial")
                .url("https://core.telegram.org/bots/api")
                .build();

        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next))
                .build();

        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back))
                .keyboardRow(List.of(url))
                .build();


        var msg = update.getMessage();
        var user = msg.getFrom();
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        // VARIABLES DECLARATION END

        // COMMANDS

        // START COMMAND
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (messageText.startsWith("/start")) {
                if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                    sendText(chatId, "Hello! Welcome to " + getBotUsername() + ". I would reccomend you starting me in PM ", true, msg.getMessageId());
                } else if (msg.isUserMessage()) {
                    sendText(chatId, "Hello " + user.getUserName() +"! Welcome to " + getBotUsername() + ". You can start using commands now. Read the /help ", true, msg.getMessageId());
                }



                //HELP COMMAND
            } else if (messageText.startsWith("/help")) {
                if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                    sendText(chatId, """
                            click here: https://t.me/SimpleUsefulBot?start=help
                            """, true, msg.getMessageId());
                } else if (msg.isUserMessage()) {
                    sendText(chatId, """
                            This is a simple bot showing you all your Information from Telegram.

                            Commands:
                            /id - gives your user ID
                            /all - shows the most important Information about you via bot

                            Advanced: 
                            /allraw - will give you all the information raw
                            
                            Coming soon: 
                            /id {@username | @groupname | @channelname} - gives the id of the specified option.
                            /all {@username | @groupname | @channelname | name | id} - gives the most important Information of the specified option. (such as admin list, membercount, url and many more)
                            /allraw {@username | @groupname | @channelname | name | id} - will give you all the information raw about the specified option. """, true, msg.getMessageId());
                }


                //ALL COMMAND
            } else if (messageText.equals("/all")) {
                if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                    if (user.getUserName() != null) {
                        sendText(chatId, "ID = " + user.getId()
                                        + "\nFirst Name = " + user.getFirstName()
                                        + "\nLast Name = " + user.getLastName()
                                        + "\nUser Name = @" + user.getUserName()
                                        + "\nLanguage Code = " + user.getLanguageCode()
                                        + "\n(is bot) = " + user.getIsBot()
                                , true, msg.getMessageId());
                    }
                } else if (user.getUserName() == null) {
                    sendText(chatId, "ID = " + user.getId()
                                    + "\nFirst Name = " + user.getFirstName()
                                    + "\nLast Name = " + user.getLastName()
                                    + "\nLanguage Code = " + user.getLanguageCode()
                                    + "\n(is bot) = " + user.getIsBot()
                            , true, msg.getMessageId());
                }


                //ID COMMAND
            } else if (messageText.startsWith("/id")) {
                if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
                    sendText(chatId, "Your personal ID is: " + user.getId()
                                    + "\nThis Chat ID is: " + msg.getChatId()
                            , true, msg.getMessageId());
                } else if (msg.isUserMessage()) {
                    sendText(chatId, "Your personal ID is: " + user.getId()
                                    + "\nThis Chat ID is: " + msg.getChatId()
                                    + "\n\nBecause you are in PM with the bot. The two IDs should be the same."
                            , true, msg.getMessageId());
                }

                //ALLRAW COMMAND
            } else if (messageText.equals("/allraw") || (msg.equals("yes") && msg.isReply())){
                if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage() && messageText.equals("/allraw")) {
                    sendText(chatId, "This will result in a very long message. Please use /all for a better view \n\nIf you are sure to send it into this group, reply to this message with 'yes'", true, msg.getMessageId());
                }
                else if ((msg.isUserMessage() && messageText.equals("/allraw"))){
                    sendText(chatId, update.toString(), true, msg.getMessageId());
                }

                //ALLRAW COMMAND FOR GROUPS
            } else if (messageText.equals("yes") && msg.isReply() && Objects.requireNonNull(msg.getReplyToMessage().getText()).equals("This will result in a very long message. Please use /all for a better view \n\nIf you are sure to send it into this group, reply to this message with 'yes'")) {
                sendText(chatId, update.toString(), true, msg.getMessageId());
            }

            //SOURCE CODE COMMAND
            else if (messageText.equals("/source")) {
                sendText(chatId, "The source code is available at: https://github.com/An0n-00/tgbot", true, msg.getMessageId());
            }

            

            //BUTTON LOGIC?
        }else if(update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData().toString();
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
