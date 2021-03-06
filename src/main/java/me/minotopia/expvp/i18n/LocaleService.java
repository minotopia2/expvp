/*
 * Expvp Minecraft game mode
 * Copyright (C) 2016-2017 Philipp Nowak (https://github.com/xxyy)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.minotopia.expvp.i18n;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import li.l1t.common.i18n.MinecraftLocale;
import me.minotopia.expvp.EPPlugin;
import me.minotopia.expvp.api.misc.PlayerInitService;
import me.minotopia.expvp.api.model.MutablePlayerData;
import me.minotopia.expvp.api.model.PlayerData;
import me.minotopia.expvp.api.service.PlayerDataService;
import me.minotopia.expvp.util.SessionProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Manages the locale preference of players from the database and receives notifications about changed client settings.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-03-07
 */
@Singleton
public class LocaleService {
    private final PlayerDataService players;
    private final SessionProvider sessionProvider;

    @Inject
    public LocaleService(PlayerDataService players, SessionProvider sessionProvider,
                         PlayerInitService initService, EPPlugin plugin) {
        this.players = players;
        this.sessionProvider = sessionProvider;
        initService.registerDeInitHandler(player -> I18n.clearLocaleOf(player.getUniqueId()));
    }

    /**
     * Updates given player's locale preference in the database if they have not explicitly chosen another locale.
     *
     * @param player the player to operate on
     * @return the computed preferred locale of given player
     */
    public Locale recomputeClientLocale(Player player) {
        return sessionProvider.inSessionAnd(ignored -> {
            MutablePlayerData playerData = players.findOrCreateDataMutable(player.getUniqueId());
            Locale locale;
            boolean notify = !I18n.hasLocale(player.getUniqueId());
            if (!playerData.hasCustomLocale()) {
                locale = MinecraftLocale.toJava(player.spigot().getLocale());
                notify = notify || setLocaleIfDifferent(playerData, locale);
            } else {
                locale = playerData.getLocale();
            }
            I18n.setLocaleFor(player.getUniqueId(), locale);
            if (notify) {
                notifySelectedLocale(player, playerData);
            }
            return locale;
        });
    }

    private boolean setLocaleIfDifferent(MutablePlayerData playerData, Locale newLocale) {
        Locale oldLocale = playerData.getLocale();
        if (!newLocale.equals(oldLocale)) {
            playerData.setLocale(newLocale);
            players.saveData(playerData);
            return true;
        }
        return false;
    }

    private void notifySelectedLocale(Player player, PlayerData playerData) {
        if (playerData.hasCustomLocale()) {
            I18n.sendLoc(player, Format.result("core!lang.manual-selected"));
        } else {
            I18n.sendLoc(player, Format.result("core!lang.auto-selected"));
        }
    }

    public void forceLocale(Player player, Locale locale) {
        sessionProvider.inSession(ignored -> {
            MutablePlayerData data = players.findOrCreateDataMutable(player.getUniqueId());
            data.setCustomLocale(true);
            data.setLocale(locale);
            I18n.setLocaleFor(player.getUniqueId(), locale);
            players.saveData(data);
        });
    }

    public void resetLocale(Player player) {
        sessionProvider.inSession(ignored -> {
            MutablePlayerData data = players.findOrCreateDataMutable(player.getUniqueId());
            data.setCustomLocale(false);
            players.saveData(data);
            recomputeClientLocale(player);
        });
    }
}
