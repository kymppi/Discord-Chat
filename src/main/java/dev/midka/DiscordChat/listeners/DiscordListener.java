package dev.midka.DiscordChat.listeners;

import dev.midka.DiscordChat.DiscordChatPlugin;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public class DiscordListener extends ListenerAdapter {

    private DiscordChatPlugin plugin;

    public DiscordListener(DiscordChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getChannel().equals(plugin.getChatChannel())) return;

        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) return;

        String message = event.getMessage().getContentDisplay();
        Bukkit.broadcastMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        plugin.getConfig().getString("format")
                .replaceAll("%sender%", member.getEffectiveName())
                .replaceAll("%message%", message)));
    }
}
