package net.efimka.automutemod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AutoMuteCommand {
    
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("automute")
            .then(Commands.literal("add")
                .then(Commands.literal("simple")
                    .then(Commands.argument("insult", StringArgumentType.greedyString())
                        .executes(context -> addSimpleInsult(context))))
                .then(Commands.literal("family")
                    .then(Commands.argument("insult", StringArgumentType.greedyString())
                        .executes(context -> addFamilyInsult(context)))))
            .then(Commands.literal("remove")
                .then(Commands.literal("simple")
                    .then(Commands.argument("insult", StringArgumentType.greedyString())
                        .executes(context -> removeSimpleInsult(context))))
                .then(Commands.literal("family")
                    .then(Commands.argument("insult", StringArgumentType.greedyString())
                        .executes(context -> removeFamilyInsult(context)))))
            .then(Commands.literal("list")
                .then(Commands.literal("simple")
                    .executes(context -> listSimpleInsults(context)))
                .then(Commands.literal("family")
                    .executes(context -> listFamilyInsults(context))))
            .then(Commands.literal("help")
                .executes(context -> showHelp(context))));
    }
    
    private static int addSimpleInsult(CommandContext<CommandSource> context) {
        String insult = StringArgumentType.getString(context, "insult");
        
        if (AutoMuteMod.addSimpleInsult(insult)) {
            context.getSource().sendFeedback(
                new StringTextComponent("Простое оскорбление '" + insult + "' добавлено в список")
                    .mergeStyle(TextFormatting.GREEN), 
                false
            );
            return 1;
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Оскорбление '" + insult + "' уже есть в списке")
                    .mergeStyle(TextFormatting.YELLOW), 
                false
            );
            return 0;
        }
    }
    
    private static int addFamilyInsult(CommandContext<CommandSource> context) {
        String insult = StringArgumentType.getString(context, "insult");
        
        if (AutoMuteMod.addFamilyInsult(insult)) {
            context.getSource().sendFeedback(
                new StringTextComponent("Семейное оскорбление '" + insult + "' добавлено в список")
                    .mergeStyle(TextFormatting.GREEN), 
                false
            );
            return 1;
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Оскорбление '" + insult + "' уже есть в списке")
                    .mergeStyle(TextFormatting.YELLOW), 
                false
            );
            return 0;
        }
    }
    
    private static int removeSimpleInsult(CommandContext<CommandSource> context) {
        String insult = StringArgumentType.getString(context, "insult");
        
        if (AutoMuteMod.removeSimpleInsult(insult)) {
            context.getSource().sendFeedback(
                new StringTextComponent("Простое оскорбление '" + insult + "' удалено из списка")
                    .mergeStyle(TextFormatting.GREEN), 
                false
            );
            return 1;
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Оскорбление '" + insult + "' не найдено в списке")
                    .mergeStyle(TextFormatting.RED), 
                false
            );
            return 0;
        }
    }
    
    private static int removeFamilyInsult(CommandContext<CommandSource> context) {
        String insult = StringArgumentType.getString(context, "insult");
        
        if (AutoMuteMod.removeFamilyInsult(insult)) {
            context.getSource().sendFeedback(
                new StringTextComponent("Семейное оскорбление '" + insult + "' удалено из списка")
                    .mergeStyle(TextFormatting.GREEN), 
                false
            );
            return 1;
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Оскорбление '" + insult + "' не найдено в списке")
                    .mergeStyle(TextFormatting.RED), 
                false
            );
            return 0;
        }
    }
    
    private static int listSimpleInsults(CommandContext<CommandSource> context) {
        var insults = AutoMuteMod.getSimpleInsults();
        
        if (insults.isEmpty()) {
            context.getSource().sendFeedback(
                new StringTextComponent("Список простых оскорблений пуст")
                    .mergeStyle(TextFormatting.YELLOW), 
                false
            );
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Простые оскорбления (" + insults.size() + "):")
                    .mergeStyle(TextFormatting.AQUA), 
                false
            );
            
            for (String insult : insults) {
                context.getSource().sendFeedback(
                    new StringTextComponent("  - " + insult)
                        .mergeStyle(TextFormatting.WHITE), 
                    false
                );
            }
        }
        return 1;
    }
    
    private static int listFamilyInsults(CommandContext<CommandSource> context) {
        var insults = AutoMuteMod.getFamilyInsults();
        
        if (insults.isEmpty()) {
            context.getSource().sendFeedback(
                new StringTextComponent("Список семейных оскорблений пуст")
                    .mergeStyle(TextFormatting.YELLOW), 
                false
            );
        } else {
            context.getSource().sendFeedback(
                new StringTextComponent("Семейные оскорбления (" + insults.size() + "):")
                    .mergeStyle(TextFormatting.AQUA), 
                false
            );
            
            for (String insult : insults) {
                context.getSource().sendFeedback(
                    new StringTextComponent("  - " + insult)
                        .mergeStyle(TextFormatting.WHITE), 
                    false
                );
            }
        }
        return 1;
    }
    
    private static int showHelp(CommandContext<CommandSource> context) {
        context.getSource().sendFeedback(
            new StringTextComponent("=== AutoMuteMod v1.0.0 by efimka ===")
                .mergeStyle(TextFormatting.GOLD), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("Команды:")
                .mergeStyle(TextFormatting.AQUA), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute add simple <текст> - добавить простое оскорбление")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute add family <текст> - добавить семейное оскорбление")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute remove simple <текст> - удалить простое оскорбление")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute remove family <текст> - удалить семейное оскорбление")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute list simple - показать простые оскорбления")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("/automute list family - показать семейные оскорбления")
                .mergeStyle(TextFormatting.WHITE), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("Простые оскорбления: мут на 20m 3.6")
                .mergeStyle(TextFormatting.YELLOW), 
            false
        );
        
        context.getSource().sendFeedback(
            new StringTextComponent("Семейные оскорбления: мут на 3.4")
                .mergeStyle(TextFormatting.RED), 
            false
        );
        
        return 1;
    }
}