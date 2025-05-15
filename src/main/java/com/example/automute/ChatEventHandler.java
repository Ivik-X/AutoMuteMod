
package com.example.automute;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEventHandler {
    private final InsultConfig config;

    public ChatEventHandler(InsultConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            String message = event.getMessage().getString().toLowerCase();
            String rawText = event.getMessage().getString();
            String sender = extractSender(rawText);

            if (sender != null && !sender.equals(Minecraft.getInstance().player.getGameProfile().getName())) {
                boolean foundSimple = checkInsults(message, config.getSimpleInsults());
                boolean foundFamily = checkInsults(message, config.getFamilyInsults());

                if (foundFamily) {
                    sendMuteCommand(sender, 3.4);
                } else if (foundSimple) {
                    sendMuteCommand(sender, 20.0);
                }
            }
        }
    }

    private boolean checkInsults(String message, List<String> insults) {
        return insults.stream().anyMatch(message::contains);
    }

    private void sendMuteCommand(String playerName, double duration) {
        String command = String.format("/mute %s %.1fm", playerName, duration);
        Minecraft.getInstance().player.sendChatMessage(command);
    }

    private String extractSender(String rawChatText) {
        Matcher matcher = Pattern.compile("^<(.+?)>").matcher(rawChatText);
        return matcher.find() ? matcher.group(1) : null;
    }
}