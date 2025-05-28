package net.efimka.automutemod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod("automutemod")
public class AutoMuteMod {
    public static final String MODID = "automutemod";
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "efimka";
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Списки оскорблений
    private static final Set<String> SIMPLE_INSULTS = new HashSet<>(Arrays.asList(
        "дурак", "идиот", "тупой", "глупый", "дебил", "придурок", "кретин", "тупица"
    ));
    
    private static final Set<String> FAMILY_INSULTS = new HashSet<>(Arrays.asList(
        "твоя мать", "твой отец", "мать твоя", "отец твой", "твоя мама", "твой папа"
    ));
    
    // Паттерн для извлечения имени игрока из сообщения в чате
    private static final Pattern CHAT_PATTERN = Pattern.compile("<([^>]+)>\\s*(.*)");
    
    public AutoMuteMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("AutoMuteMod v{} by {} загружен", VERSION, AUTHOR);
    }
    
    private void setup(final FMLClientSetupEvent event) {
        LOGGER.info("AutoMuteMod инициализирован");
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        AutoMuteCommand.register(event.getDispatcher());
    }
    
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() != ChatType.CHAT) {
            return;
        }
        
        ITextComponent message = event.getMessage();
        String chatText = message.getString();
        
        // Извлекаем имя игрока и сообщение
        Matcher matcher = CHAT_PATTERN.matcher(chatText);
        if (!matcher.find()) {
            return;
        }
        
        String playerName = matcher.group(1);
        String playerMessage = matcher.group(2).toLowerCase();
        
        // Проверяем на оскорбления
        String detectedInsult = checkForInsults(playerMessage);
        if (detectedInsult != null) {
            handleInsult(playerName, detectedInsult);
        }
    }
    
    private String checkForInsults(String message) {
        // Проверяем семейные оскорбления (более серьезные)
        for (String insult : FAMILY_INSULTS) {
            if (message.contains(insult.toLowerCase())) {
                return "family:" + insult;
            }
        }
        
        // Проверяем простые оскорбления
        for (String insult : SIMPLE_INSULTS) {
            if (message.contains(insult.toLowerCase())) {
                return "simple:" + insult;
            }
        }
        
        return null;
    }
    
    private void handleInsult(String playerName, String insultInfo) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        
        String[] parts = insultInfo.split(":", 2);
        String type = parts[0];
        String insult = parts[1];
        
        String command;
        if ("family".equals(type)) {
            command = "/mute " + playerName + " 3.4";
            LOGGER.info("Обнаружено семейное оскорбление '{}' от игрока {}. Выдается мут на 3.4", insult, playerName);
        } else {
            command = "/mute " + playerName + " 20m 3.6";
            LOGGER.info("Обнаружено простое оскорбление '{}' от игрока {}. Выдается мут на 20m 3.6", insult, playerName);
        }
        
        // Отправляем команду в чат
        mc.player.sendChatMessage(command);
    }
    
    // Методы для управления списками оскорблений
    public static boolean addSimpleInsult(String insult) {
        return SIMPLE_INSULTS.add(insult.toLowerCase());
    }
    
    public static boolean removeSimpleInsult(String insult) {
        return SIMPLE_INSULTS.remove(insult.toLowerCase());
    }
    
    public static boolean addFamilyInsult(String insult) {
        return FAMILY_INSULTS.add(insult.toLowerCase());
    }
    
    public static boolean removeFamilyInsult(String insult) {
        return FAMILY_INSULTS.remove(insult.toLowerCase());
    }
    
    public static Set<String> getSimpleInsults() {
        return new HashSet<>(SIMPLE_INSULTS);
    }
    
    public static Set<String> getFamilyInsults() {
        return new HashSet<>(FAMILY_INSULTS);
    }
}