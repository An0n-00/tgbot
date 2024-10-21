package TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            botsApi.registerBot(bot);
            bot.onRegister();
            System.out.println("Bot registered successfully!\nYou will start to get Updates!");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
