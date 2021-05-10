package dev.midka.DiscordChat;

import dev.midka.DiscordChat.features.WebhookFeature;
import dev.midka.DiscordChat.listeners.DiscordListener;
import dev.midka.DiscordChat.listeners.SpigotListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DiscordChatPlugin extends JavaPlugin {
    private JDA jda;
    private TextChannel chatChannel;
    private WebhookFeature webhookFeature;

    private final Map<String, String> advancementToDisplayMap = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        webhookFeature = new WebhookFeature(getConfig().getString("webhook-url"));
        if (getConfig().getBoolean("use-webhooks")) {
            webhookFeature.use();
        }

        getServer().getPluginManager().registerEvents(new SpigotListener(), this);

        String botToken = getConfig().getString("bot-token");

        try {
            jda = JDABuilder.createDefault(botToken).build().awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            getLogger().info(ChatColor.RED + "Invalid token!");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Error connecting
        if (jda == null) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(ChatColor.RED + "Error connecting to discord. Disabling plugin.");
            return;
        }

        String chatChannelId = getConfig().getString("chat-channel-id");
        if (chatChannelId != null) {
            chatChannel = jda.getTextChannelById(chatChannelId);

            if (chatChannel == null) {
                getLogger().info(ChatColor.RED + "Error getting chat channel...");
                getServer().getPluginManager().disablePlugin(this);
            }
        }

        ConfigurationSection advancementMap = getConfig().getConfigurationSection("advancementMap");
        if (advancementMap != null) {
            for (String key : advancementMap.getKeys(false)) {
                advancementToDisplayMap.put(key, advancementMap.getString(key));
            }
        }
        jda.addEventListener(new DiscordListener(this));

        EmbedBuilder builder = new EmbedBuilder().setTitle("Server started!");
        builder.setColor(Color.GREEN);
        chatChannel.sendMessage(builder.build()).queue();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        EmbedBuilder builder = new EmbedBuilder().setTitle("Server stopped!");
        builder.setColor(Color.RED);
        chatChannel.sendMessage(builder.build()).queue();

        // Shutting down jda if not null
        if (jda != null) jda.shutdownNow();
    }

    public void sendMessage(Player player, String content, boolean contentInAuthorLine, Color color) {
        if (chatChannel == null) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(
                        contentInAuthorLine ? content : player.getDisplayName(),
                        null,
                        "https://crafatar.com/avatars/" + player.getUniqueId().toString());
        if (!contentInAuthorLine) {
            builder.setDescription(content);
        }

        builder.setColor(color);

        chatChannel.sendMessage(builder.build()).queue();
    }

    public Map<String, String> getAdvancementList() {
        return advancementToDisplayMap;
    }

    public TextChannel getChatChannel() {
        return chatChannel;
    }

    public WebhookFeature getWebhookFeature() {
        return webhookFeature;
    }

}
