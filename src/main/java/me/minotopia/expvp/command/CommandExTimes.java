/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.command;

import com.google.inject.Inject;
import com.sk89q.intake.Command;
import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.i18n.Message;
import me.minotopia.expvp.Permission;
import me.minotopia.expvp.api.extimes.*;
import me.minotopia.expvp.command.permission.EnumRequires;
import me.minotopia.expvp.i18n.Format;
import me.minotopia.expvp.i18n.I18n;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides the /extimes command which allows management of ExTimes activation times.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-05-17
 */
@AutoRegister(value = "ext", aliases = "extimes")
public class CommandExTimes {
    private final ExTimesConfig config;
    private final ExTimesService times;

    @Inject
    public CommandExTimes(ExTimesConfig config, ExTimesService times) {
        this.config = config;
        this.times = times;
    }

    @Command(aliases = "list", desc = "List ExTimes")
    @EnumRequires(Permission.ADMIN_EXTIMES)
    public void list(CommandSender sender) {
        times.cleanUp();
        listTimes(sender, "admin!extimes.header-special", this::toSpecialTimeMessage, config.getSpecialTimes());
        listTimes(sender, "admin!extimes.header-week", this::toDOWTimeMessage, config.getWeekTimes());
        listTimes(sender, "admin!extimes.header-weekend", this::toDOWTimeMessage, config.getWeekendTimes());
    }

    private <T extends ExTime> void listTimes(CommandSender sender, String headerKey,
                                              Function<T, Message> messageFunction, List<? extends T> times) {
        I18n.sendLoc(sender, Format.listHeader(headerKey));
        if (times.isEmpty()) {
            I18n.sendLoc(sender, "admin!extimes.none");
        } else {
            times.forEach(sendAsListItemTo(sender, messageFunction));
        }
    }

    private <T extends ExTime> Consumer<T> sendAsListItemTo(CommandSender sender, Function<T, Message> messageFunction) {
        return time -> ComponentSender.sendTo(sender,
                TextComponent.fromLegacyText(I18n.loc(sender, messageFunction.apply(time))),
                new XyComponentBuilder(" [-] ", ChatColor.DARK_RED)
                        .appendIf(sender instanceof ConsoleCommandSender, time.getUniqueId())
                        .hintedCommand("/ext remove " + time.getUniqueId())
                        .create()
        );
    }

    private Message toSpecialTimeMessage(SpecialExTime time) {
        return Message.of("admin!extimes.list-special",
                time.getApplicableDate(), time.getStart(), time.getEnd(),
                toReadableString(time)
        );
    }

    private Message toDOWTimeMessage(DayOfWeekExTime time) {
        return Message.of("admin!extimes.list-dow",
                time.getStart(), time.getEnd(),
                toReadableString(time)
        );
    }

    private String toReadableString(ExTime time) {
        return time.getDuration().toString().substring(2);
    }

    @Command(aliases = "addweek", desc = "Adds a Mon-Thu ExTime", usage = "[start HH:mm] [end HH:mm]")
    @EnumRequires(Permission.ADMIN_EXTIMES)
    public void addWeek(CommandSender sender, LocalTime start, LocalTime end) {
        config.addWeekTime(start, end);
        I18n.sendLoc(sender, Format.success("admin!extimes.added-dow", start, end));
    }

    @Command(aliases = "addweekend", desc = "Adds a Fri-Sun ExTime", usage = "[start HH:mm] [end HH:mm]")
    @EnumRequires(Permission.ADMIN_EXTIMES)
    public void addWeekend(CommandSender sender, LocalTime start, LocalTime end) {
        config.addWeekendTime(start, end);
        I18n.sendLoc(sender, Format.success("admin!extimes.added-dow", start, end));
    }

    @Command(aliases = "addspecial", desc = "Adds a special ExTime", usage = "[start HH:mm] [end HH:mm] [date YYYY-MM-dd]")
    @EnumRequires(Permission.ADMIN_EXTIMES)
    public void addSpecial(CommandSender sender, LocalTime start, LocalTime end, LocalDate date) {
        config.addSpecialTime(date, start, end);
        I18n.sendLoc(sender, Format.success("admin!extimes.added-special", date, start, end));
    }

    @Command(aliases = "remove", desc = "Remove an ExTime", usage = "[uuid]")
    @EnumRequires(Permission.ADMIN_EXTIMES)
    public void remove(CommandSender sender, UUID uuid) {
        times.cleanUp();
        Predicate<ExTime> idPredicate = time -> time.getUniqueId().equals(uuid);
        if (config.getSpecialTimes().removeIf(idPredicate) ||
                config.getWeekTimes().removeIf(idPredicate) ||
                config.getWeekendTimes().removeIf(idPredicate)) {
            I18n.sendLoc(sender, Format.success("admin!extimes.removed"));
        } else {
            I18n.sendLoc(sender, Format.userError("admin!extimes.no-such"));
        }
    }
}
