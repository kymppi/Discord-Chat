package dev.midka.DiscordChat.features;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.bukkit.entity.Player;

public class WebhookFeature {
    private String url;
    private WebhookClient client;

    public WebhookFeature(String url) {
        this.url = url;
    }


    public void use() {
        WebhookClientBuilder builder = new WebhookClientBuilder(url); // or id, token
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Webhook-Thread");
            thread.setDaemon(true);
            return thread;
        });
        this.client = builder.build();
    }

    public void sendMessage(Player player, String message) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(player.getDisplayName())
                .setAvatarUrl("https://crafatar.com/avatars/" +
                        player.getUniqueId().toString())
                .setContent(message);
        client.send(builder.build());
    }

}
