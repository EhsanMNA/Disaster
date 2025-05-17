package me.ehsanmna.disaster.util;

import me.ehsanmna.disaster.DisasterPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TextUtils {

    private static String prefix;
    private static MessagesModel messages;

    public static void initialize(DisasterPlugin plugin) {
        messages = new MessagesModel(plugin);
        prefix = messages.prefix;
    }

    public static String getMessage(String key, String... placeholders) {
        if (messages == null) return "&cMessages not initialized: " + key;

        String message = messages.get(key);
        if (message == null) return key;

        for (int i = 0; i < placeholders.length; i++)
            message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));

        return message.replace("{prefix}",prefix);
    }

    public static void sendMessage(Player player, String keyOrMessage, String... placeholders) {
        String message = getMessage(keyOrMessage, placeholders);
        if (message.equals("null")) message = keyOrMessage;
        player.sendMessage(toComponent(message));
    }

    public static void sendActionbar(Player player, String keyOrMessage, String... placeholders) {
        String message = getMessage(keyOrMessage, placeholders);
        if (message.equals("null")) message = keyOrMessage;
        player.sendActionBar(toComponent(colorize(message)));
    }

    public static String toString(Component component){
        return MiniMessage.miniMessage().serialize(component);
    }

    public static void reloadMessages() {
        if (messages != null) {
            messages.reloadMessages();
            prefix = messages.prefix;
        }
    }

    public static void sendTitle(Player player, String title, String subtitles) {
        player.showTitle(Title.title(toComponent(getMessage(title)),toComponent(subtitles)));
    }

    public static String colorize(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public static Component toComponent(String string){
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(string));
    }

}
