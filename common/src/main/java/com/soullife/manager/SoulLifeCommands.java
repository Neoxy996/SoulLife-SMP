package com.soullife.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.soullife.util.MessageUtil;
import com.soullife.util.ScoreboardManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SoulLife - SoulLifeCommands
 *
 * Player Commands:
 *   /soullife check
 *   /soullife next
 *   /soullife info
 *   /soullife gui
 *   /soullife language [list|reset|<code>]
 *
 * Admin Commands:
 *   /soullife edititem <death#> <item>
 *   /soullife reset
 *   /soullife add    <player> <amount>
 *   /soullife remove <player> <amount>
 *   /soullife set    <player> <amount>
 */
public class SoulLifeCommands {

    private static final int PERM_USER  = 0;
    private static final int PERM_ADMIN = 2;

    // ── Autocomplete Providers ────────────────────────────────────────────────

    // death1 → death20
    private static final SuggestionProvider<CommandSourceStack> DEATH_SLOT_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(
            IntStream.rangeClosed(1, 20)
                     .mapToObj(i -> "death" + i)
                     .collect(Collectors.toList()), builder);

    // All items + blocks in registry (including modded)
    private static final SuggestionProvider<CommandSourceStack> ALL_ITEM_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggestResource(
            BuiltInRegistries.ITEM.keySet().stream(), builder);

    // Online player names
    private static final SuggestionProvider<CommandSourceStack> ONLINE_PLAYER_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(
            ctx.getSource().getServer()
               .getPlayerList().getPlayers()
               .stream().map(p -> p.getName().getString()), builder);

    // Supported languages
    private static final SuggestionProvider<CommandSourceStack> LANGUAGE_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(
            LanguageManager.SUPPORTED_LANGUAGES.keySet(), builder);

    // ══════════════════════════════════════════════════════════════════════════
    //  REGISTER
    // ══════════════════════════════════════════════════════════════════════════
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> root =
            Commands.literal("soullife");

        // /soullife → help
        root.executes(ctx -> executeHelp(ctx));

        // /soullife check
        root.then(Commands.literal("check")
            .requires(src -> src.hasPermission(PERM_USER))
            .executes(ctx -> executeCheck(ctx)));

        // /soullife next
        root.then(Commands.literal("next")
            .requires(src -> src.hasPermission(PERM_USER))
            .executes(ctx -> executeNext(ctx)));

        // /soullife info
        root.then(Commands.literal("info")
            .requires(src -> src.hasPermission(PERM_USER))
            .executes(ctx -> executeInfo(ctx)));

        // /soullife gui
        root.then(Commands.literal("gui")
            .requires(src -> src.hasPermission(PERM_USER))
            .executes(ctx -> executeGui(ctx)));

        // /soullife language [list|reset|<code>]
        root.then(Commands.literal("language")
            .requires(src -> src.hasPermission(PERM_USER))
            .executes(ctx -> executeLanguageCurrent(ctx))
            .then(Commands.literal("list")
                .executes(ctx -> executeLanguageList(ctx)))
            .then(Commands.literal("reset")
                .executes(ctx -> executeLanguageReset(ctx)))
            .then(Commands.argument("lang", StringArgumentType.word())
                .suggests(LANGUAGE_SUGGESTIONS)
                .executes(ctx -> executeLanguageSet(ctx))));

        // /soullife edititem <death#> <item> [ADMIN]
        root.then(Commands.literal("edititem")
            .requires(src -> src.hasPermission(PERM_ADMIN))
            .then(Commands.argument("slot", StringArgumentType.word())
                .suggests(DEATH_SLOT_SUGGESTIONS)
                .then(Commands.argument("item", ResourceLocationArgument.id())
                    .suggests(ALL_ITEM_SUGGESTIONS)
                    .executes(ctx -> executeEditItem(ctx)))
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal(
                        "§cUsage: /soullife edititem <death1-20> <item_id>"));
                    return 0;
                }))
            .executes(ctx -> {
                ctx.getSource().sendFailure(Component.literal(
                    "§cUsage: /soullife edititem <death1-20> <item_id>"));
                return 0;
            }));

        // /soullife reset [ADMIN]
        root.then(Commands.literal("reset")
            .requires(src -> src.hasPermission(PERM_ADMIN))
            .executes(ctx -> executeReset(ctx)));

        // /soullife add <player> <amount> [ADMIN]
        root.then(Commands.literal("add")
            .requires(src -> src.hasPermission(PERM_ADMIN))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests(ONLINE_PLAYER_SUGGESTIONS)
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                    .executes(ctx -> executeAdd(ctx)))
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal(
                        "§cUsage: /soullife add <player> <amount>"));
                    return 0;
                }))
            .executes(ctx -> {
                ctx.getSource().sendFailure(Component.literal(
                    "§cUsage: /soullife add <player> <amount>"));
                return 0;
            }));

        // /soullife remove <player> <amount> [ADMIN]
        root.then(Commands.literal("remove")
            .requires(src -> src.hasPermission(PERM_ADMIN))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests(ONLINE_PLAYER_SUGGESTIONS)
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                    .executes(ctx -> executeRemove(ctx)))
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal(
                        "§cUsage: /soullife remove <player> <amount>"));
                    return 0;
                }))
            .executes(ctx -> {
                ctx.getSource().sendFailure(Component.literal(
                    "§cUsage: /soullife remove <player> <amount>"));
                return 0;
            }));

        // /soullife set <player> <amount> [ADMIN]
        root.then(Commands.literal("set")
            .requires(src -> src.hasPermission(PERM_ADMIN))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests(ONLINE_PLAYER_SUGGESTIONS)
                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 20))
                    .executes(ctx -> executeSet(ctx)))
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal(
                        "§cUsage: /soullife set <player> <amount>"));
                    return 0;
                }))
            .executes(ctx -> {
                ctx.getSource().sendFailure(Component.literal(
                    "§cUsage: /soullife set <player> <amount>"));
                return 0;
            }));

        dispatcher.register(root);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXECUTORS
    // ══════════════════════════════════════════════════════════════════════════

    private static int executeHelp(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        src.sendSuccess(() -> Component.literal(""), false);
        src.sendSuccess(() -> Component.literal("§6§l⚰ SoulLife Commands"), false);
        src.sendSuccess(() -> Component.literal("§e/soullife check §7- Your death count"), false);
        src.sendSuccess(() -> Component.literal("§e/soullife next §7- Next sacrifice item"), false);
        src.sendSuccess(() -> Component.literal("§e/soullife info §7- Mod info"), false);
        src.sendSuccess(() -> Component.literal("§e/soullife gui §7- All 20 items"), false);
        src.sendSuccess(() -> Component.literal("§e/soullife language §7- Change language"), false);
        if (src.hasPermission(PERM_ADMIN)) {
            src.sendSuccess(() -> Component.literal("§c§l[Admin Commands]"), false);
            src.sendSuccess(() -> Component.literal("§c/soullife edititem <death#> <item>"), false);
            src.sendSuccess(() -> Component.literal("§c/soullife reset"), false);
            src.sendSuccess(() -> Component.literal("§c/soullife add <player> <amount>"), false);
            src.sendSuccess(() -> Component.literal("§c/soullife remove <player> <amount>"), false);
            src.sendSuccess(() -> Component.literal("§c/soullife set <player> <amount>"), false);
        }
        src.sendSuccess(() -> Component.literal(""), false);
        return 1;
    }

    private static int executeCheck(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int deaths = DeathManager.getDeathCount(player);
        if (DeathManager.isPermanentSpectator(player)) {
            player.sendSystemMessage(Component.literal(
                "§4§l⚰ " + TranslationManager.get(player,
                "soullife.death.permanent.check")));
        } else {
            MessageUtil.sendCheckMessage(player);
        }
        return 1;
    }

    private static int executeNext(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        if (DeathManager.isPermanentSpectator(player)) {
            player.sendSystemMessage(Component.literal("§4No next item. You are permanent spectator."));
            return 0;
        }
        ItemStack next = SacrificeManager.getRequiredItem(player);
        if (next.isEmpty()) {
            player.sendSystemMessage(Component.literal("§aNo sacrifice needed right now!"));
        } else {
            MessageUtil.sendNextMessage(player, next.getHoverName().getString());
        }
        return 1;
    }

    private static int executeInfo(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        MessageUtil.sendInfoMessage(ctx.getSource().getPlayerOrException());
        return 1;
    }

    private static int executeGui(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        MessageUtil.sendGuiMessage(ctx.getSource().getPlayerOrException());
        return 1;
    }

    // ── Language Commands ─────────────────────────────────────────────────────
    private static int executeLanguageCurrent(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        MessageUtil.sendLanguageCurrent(ctx.getSource().getPlayerOrException());
        return 1;
    }

    private static int executeLanguageList(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        MessageUtil.sendLanguageList(ctx.getSource().getPlayerOrException());
        return 1;
    }

    private static int executeLanguageReset(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        LanguageManager.resetLanguage(player);
        MessageUtil.sendLanguageReset(player);
        return 1;
    }

    private static int executeLanguageSet(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        String lang = StringArgumentType.getString(ctx, "lang").toLowerCase();

        if (!LanguageManager.isSupported(lang)) {
            MessageUtil.sendLanguageInvalid(player, lang);
            return 0;
        }

        LanguageManager.setLanguage(player, lang);
        MessageUtil.sendLanguageChanged(player, lang);
        return 1;
    }

    // ── EditItem ──────────────────────────────────────────────────────────────
    private static int executeEditItem(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer admin = ctx.getSource().getPlayerOrException();
        String slot = StringArgumentType.getString(ctx, "slot");
        ResourceLocation itemId = ResourceLocationArgument.getId(ctx, "item");

        int deathIndex = parseDeathSlot(slot);
        if (deathIndex < 0) {
            admin.sendSystemMessage(Component.literal(
                "§cInvalid slot '§f" + slot + "§c'. Use death1 to death20."));
            return 0;
        }

        if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
            admin.sendSystemMessage(Component.literal(
                "§cItem not found: §f" + itemId));
            return 0;
        }

        Item item = BuiltInRegistries.ITEM.get(itemId);
        ItemStack newItem = new ItemStack(item);
        DeathManager.setSacrificeItem(deathIndex, newItem);
        MessageUtil.sendEditItemMessage(admin, deathIndex,
            newItem.getHoverName().getString());

        // Notify other online admins
        ctx.getSource().getServer().getPlayerList().getPlayers().forEach(p -> {
            if (p != admin && p.hasPermissions(PERM_ADMIN)) {
                p.sendSystemMessage(Component.literal(
                    "§7[SoulLife] §f" + admin.getName().getString()
                    + " §7changed death §f" + (deathIndex + 1)
                    + " §7to §f" + newItem.getHoverName().getString()));
            }
        });
        return 1;
    }

    // ── Reset ─────────────────────────────────────────────────────────────────
    private static int executeReset(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer admin = ctx.getSource().getPlayerOrException();
        DeathManager.resetSacrificeItems();
        MessageUtil.sendResetMessage(admin);
        return 1;
    }

    // ── Add ───────────────────────────────────────────────────────────────────
    private static int executeAdd(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer admin = ctx.getSource().getPlayerOrException();
        String playerName = StringArgumentType.getString(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        ServerPlayer target = findPlayer(ctx, playerName);
        if (target == null) {
            admin.sendSystemMessage(Component.literal(
                "§cPlayer not found: §f" + playerName));
            return 0;
        }

        int old = DeathManager.getDeathCount(target);
        DeathManager.addDeaths(target, amount);
        int newDeaths = Math.min(DeathManager.getDeathCount(target), 20);
        DeathManager.setDeaths(target, newDeaths);

        MessageUtil.sendAddMessage(admin, target, amount, old, newDeaths);
        target.sendSystemMessage(Component.literal(
            "§c[SoulLife] Admin added §6" + amount + " §cdeaths. ("
            + newDeaths + "/20)"));

        handleSpectatorCheck(target, newDeaths, admin);
        ScoreboardManager.updateTabDisplay(target);
        return 1;
    }

    // ── Remove ────────────────────────────────────────────────────────────────
    private static int executeRemove(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer admin = ctx.getSource().getPlayerOrException();
        String playerName = StringArgumentType.getString(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        ServerPlayer target = findPlayer(ctx, playerName);
        if (target == null) {
            admin.sendSystemMessage(Component.literal(
                "§cPlayer not found: §f" + playerName));
            return 0;
        }

        int old = DeathManager.getDeathCount(target);
        if (old == 0) {
            admin.sendSystemMessage(Component.literal(
                "§c" + target.getName().getString() + " already has 0 deaths!"));
            return 0;
        }

        DeathManager.removeDeaths(target, amount);
        int newDeaths = DeathManager.getDeathCount(target);

        MessageUtil.sendRemoveMessage(admin, target, amount, old, newDeaths);
        target.sendSystemMessage(Component.literal(
            "§a[SoulLife] Admin removed §6" + amount + " §adeaths. ("
            + newDeaths + "/20)"));

        if (newDeaths < 20 && DeathManager.isPermanentSpectator(target)) {
            DeathManager.setPermanentSpectator(target, false);
            GhostManager.removeGhostState(target);
            admin.sendSystemMessage(Component.literal(
                "§a" + target.getName().getString() + " revived from spectator!"));
        }

        ScoreboardManager.updateTabDisplay(target);
        return 1;
    }

    // ── Set ───────────────────────────────────────────────────────────────────
    private static int executeSet(CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        ServerPlayer admin = ctx.getSource().getPlayerOrException();
        String playerName = StringArgumentType.getString(ctx, "player");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        ServerPlayer target = findPlayer(ctx, playerName);
        if (target == null) {
            admin.sendSystemMessage(Component.literal(
                "§cPlayer not found: §f" + playerName));
            return 0;
        }

        int old = DeathManager.getDeathCount(target);
        DeathManager.setDeaths(target, amount);

        MessageUtil.sendSetMessage(admin, target, old, amount);
        target.sendSystemMessage(Component.literal(
            "§e[SoulLife] Admin set your deaths to §6" + amount + "/20"));

        if (amount >= 20) {
            handleSpectatorCheck(target, amount, admin);
        } else if (amount < 20 && DeathManager.isPermanentSpectator(target)) {
            DeathManager.setPermanentSpectator(target, false);
            GhostManager.removeGhostState(target);
            target.setGameMode(GameType.SURVIVAL);
        } else if (amount == 0 && DeathManager.isGhost(target)) {
            GhostManager.removeGhostState(target);
        }

        ScoreboardManager.updateTabDisplay(target);
        return 1;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private static int parseDeathSlot(String slot) {
        try {
            String lower = slot.toLowerCase().trim();
            if (!lower.startsWith("death")) return -1;
            int num = Integer.parseInt(lower.substring(5));
            return (num >= 1 && num <= 20) ? num - 1 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static ServerPlayer findPlayer(CommandContext<CommandSourceStack> ctx,
                                            String name) {
        return ctx.getSource().getServer().getPlayerList().getPlayerByName(name);
    }

    private static void handleSpectatorCheck(ServerPlayer target, int deaths,
                                              ServerPlayer admin) {
        if (deaths >= 20 && !DeathManager.isPermanentSpectator(target)) {
            DeathManager.setPermanentSpectator(target, true);
            GhostManager.applyGhostState(target);
            MessageUtil.sendPermanentDeathMessage(target);
            admin.sendSystemMessage(Component.literal(
                "§c" + target.getName().getString()
                + " is now a permanent spectator!"));
        }
    }
}
