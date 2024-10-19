package TelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.ArrayList;
import java.util.List;

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
                .parseMode("Markdown")
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

    private void sendStart(Integer msgId, Message msg, long chatId, User user) {
        if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
            sendText(chatId, "Hello! Welcome to " + getBotUsername() + ". I would reccomend you starting me in PM ", true, msg.getMessageId());
        } else if (msg.isUserMessage()) {
            sendText(chatId, "Hello " + user.getUserName() +"! Welcome to " + getBotUsername() + ". You can start using commands now. Read the /help", true, msg.getMessageId());
        }
    }

    private void sendHelp(Integer msgId, Message msg, long chatId, User user) {
        if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
            sendText(chatId, String.format("Click [here](https://t.me/%s?start=help)", getBotUsername()), true, msg.getMessageId());
        } else if (msg.isUserMessage()) {
            sendText(chatId, """
                            This is a simple bot showing you all your Information from Telegram.

                            Commands:
                            /id - gives your user ID
                            /all - shows the most important Information about you via bot
                            /help - sends this message
                            /source - Check out the GitHub Repository for this Bot!
                            """, true, msg.getMessageId());
        }
    }

    private void sendAll(Integer msgId, Message msg, long chatId, User user) {
        if (user.getUserName() != null) {
            sendText(chatId, String.format("ID = `%s`\nUser Name = `@%s`\nLanguage Code = `%s`", user.getId(), user.getUserName(), user.getLanguageCode()), true, msg.getMessageId());
        } else {
            sendText(chatId, String.format("ID = `%s`\nName = `%s %s`\nLanguage Code = `%s`", user.getId(), user.getFirstName(), user.getLastName(), user.getLanguageCode()), true, msg.getMessageId());
        }
    }

    private void sendId(Integer msgId, Message msg, long chatId, User user) {
        if (msg.isGroupMessage() || msg.isChannelMessage() || msg.isSuperGroupMessage()) {
            sendText(chatId, String.format("User ID = `%s`\nChat ID = `%s`", user.getId(), chatId), true, msg.getMessageId());
        } else if (msg.isUserMessage()) {
            sendText(chatId, String.format("User ID = `%s`\nChat ID = `%s`\nThey are the same since you are in PM with the bot.", user.getId(), chatId), true, msg.getMessageId());
        }
    }

    private void sendSource(Integer msgId, Message msg, long chatId, User user) {
        sendText(chatId, "The source code is available at: https://github.com/An0n-00/tgbot", true, msg.getMessageId());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            Integer msgId = msg.getMessageId();
            User user = msg.getFrom();
            long chatId = update.getMessage().getChatId();
            String msgTxt = update.getMessage().getText();

            // COMMANDS
            if (msgTxt.startsWith("/start")) {
                sendStart(msgId, msg, chatId, user);
            } else if (msgTxt.startsWith("/help")) {
                sendHelp(msgId, msg, chatId, user);
            } else if (msgTxt.startsWith("/all")) {
                sendAll(msgId, msg, chatId, user);
            } else if (msgTxt.startsWith("/id")) {
                sendId(msgId, msg, chatId, user);
            } else if (msgTxt.startsWith("/source")) {
                sendSource(msgId, msg, chatId, user);
            }
        }
    }
}
