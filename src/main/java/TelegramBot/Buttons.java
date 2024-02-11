//THIS FILE WAS ONLY FOR TESTING. YOU CAN IGNORE THIS. 



package TelegramBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.List;

public class Bott extends TelegramLongPollingBot {
    private boolean screaming = false;

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;
    @Override
    public String getBotUsername() {
        return "";// Replace with your bot's username
    }

    @Override
    public String getBotToken() {
        return "";// Replace with your bot's token
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
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
        if(update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId_button = update.getCallbackQuery().getFrom().getId();
        } else {
            try {
                SendMessage sendMessage = new SendMessage(update.getMessage().getFrom().getId().toString(), "start");
                sendMessage.setReplyMarkup(keyboardM1);
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
