
package com.example.automute;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCommands {
    private final InsultConfig config;

    public ModCommands(InsultConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        
        LiteralArgumentBuilder<CommandSource> automute = Commands.literal("automute")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("add")
                .then(Commands.argument("type", Commands.string())
                    .suggests((ctx, builder) -> 
                        ISuggestionProvider.suggest(new String[]{"simple", "family"}, builder))
                    .then(Commands.argument("word", Commands.greedyString())
                        .executes(ctx -> {
                            String type = ctx.getArgument("type", String.class);
                            String word = ctx.getArgument("word", String.class).toLowerCase();
                            
                            if (type.equals("simple")) {
                                config.getSimpleInsults().add(word);
                            } else if (type.equals("family")) {
                                config.getFamilyInsults().add(word);
                            }
                            config.save();
                            ctx.getSource().sendSuccess(
                                new StringTextComponent("Added to " + type + ": " + word), true);
                            return 1;
                        })))
            .then(Commands.literal("remove")
                .then(Commands.argument("type", Commands.string())
                    .suggests((ctx, builder) -> 
                        ISuggestionProvider.suggest(new String[]{"simple", "family"}, builder))
                    .then(Commands.argument("word", Commands.greedyString())
                        .executes(ctx -> {
                            String type = ctx.getArgument("type", String.class);
                            String word = ctx.getArgument("word", String.class).toLowerCase();
                            
                            boolean removed = switch (type) {
                                case "simple" -> config.getSimpleInsults().remove(word);
                                case "family" -> config.getFamilyInsults().remove(word);
                                default -> false;
                            };
                            
                            if (removed) {
                                config.save();
                                ctx.getSource().sendSuccess(
                                    new StringTextComponent("Removed from " + type + ": " + word), true);
                            } else {
                                ctx.getSource().sendFailure(
                                    new StringTextComponent("Word not found!"));
                            }
                            return 1;
                        })))
            .then(Commands.literal("list")
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                        new StringTextComponent("Simple: " + String.join(", ", config.getSimpleInsults()) + 
                        "\nFamily: " + String.join(", ", config.getFamilyInsults())), false);
                    return 1;
                }));

        dispatcher.register(automute);
    }
}