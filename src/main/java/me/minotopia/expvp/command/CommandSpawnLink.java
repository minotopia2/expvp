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
import li.l1t.common.intake.provider.annotation.Sender;
import me.minotopia.expvp.Permission;
import me.minotopia.expvp.api.spawn.MapSpawn;
import me.minotopia.expvp.api.spawn.button.VoteButtonService;
import me.minotopia.expvp.command.permission.EnumRequires;
import me.minotopia.expvp.i18n.Format;
import me.minotopia.expvp.i18n.I18n;
import org.bukkit.entity.Player;

/**
 * Allows players to link buttons to vote for spawns.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-04-19
 */
@AutoRegister("splink")
public class CommandSpawnLink {
    private final VoteButtonService buttons;

    @Inject
    public CommandSpawnLink(VoteButtonService buttons) {
        this.buttons = buttons;
    }

    @Command(aliases = "link",
            desc = "cmd!splink.link.desc", help = "cmd!splink.link.help",
            usage = "cmd!splink.link.usage", min = 1)
    @EnumRequires(Permission.ADMIN_SPAWN)
    public void link(@Sender Player player, MapSpawn spawn) {
        buttons.startLinkingSession(player, spawn);
        I18n.sendLoc(player, Format.success("admin!spawn.link.session-start"));
    }

    @Command(aliases = "unlink",
            desc = "cmd!splink.link.desc", help = "cmd!splink.link.help")
    @EnumRequires(Permission.ADMIN_SPAWN)
    public void unlink(@Sender Player player) {
        buttons.startLinkingSession(player, null);
        I18n.sendLoc(player, Format.success("admin!spawn.unlink.session-start"));
    }
}
