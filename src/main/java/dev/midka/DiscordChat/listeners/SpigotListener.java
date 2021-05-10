package dev.midka.DiscordChat.listeners;

import dev.midka.DiscordChat.DiscordChatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class SpigotListener implements Listener {

    private DiscordChatPlugin plugin = DiscordChatPlugin.getPlugin(DiscordChatPlugin.class);

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getConfig().getBoolean("use-webhooks")) {
            plugin.getWebhookFeature().sendMessage(event.getPlayer(), event.getMessage());
        } else {
            plugin.sendMessage(event.getPlayer(), event.getMessage(), false, Color.GRAY);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
            plugin.sendMessage(
                    event.getPlayer(),
                    event.getPlayer().getDisplayName() +
                            " joined the game.",
                    true,
                    Color.GREEN);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.sendMessage(
                event.getPlayer(),
                event.getPlayer().getDisplayName() +
                        " left the game.",
                true,
                Color.RED);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String deathsMessage = event.getDeathMessage() == null ?
                player.getDisplayName() + " died." :
                event.getDeathMessage();
        plugin.sendMessage(player, deathsMessage, true, Color.RED);


    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        String advancementKey = event.getAdvancement().getKey().getKey();
        String display = plugin.getAdvancementList().get(advancementKey);
        if (display == null) return;
        String message = event.getPlayer().getDisplayName() + " has made the advancement ["+display+"]";
        plugin.sendMessage(event.getPlayer(), message, true, Color.CYAN);
    }
}
