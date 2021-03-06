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

package me.minotopia.expvp.spawn;

import com.google.inject.Inject;
import li.l1t.common.i18n.Message;
import li.l1t.common.util.task.TaskService;
import me.minotopia.expvp.api.i18n.DisplayNameService;
import me.minotopia.expvp.api.misc.ConstructOnEnable;
import me.minotopia.expvp.api.misc.PlayerInitService;
import me.minotopia.expvp.api.spawn.SpawnChangeService;
import me.minotopia.expvp.api.spawn.SpawnDisplayService;
import me.minotopia.expvp.api.spawn.SpawnService;
import me.minotopia.expvp.i18n.I18n;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays spawn status using the boss bar.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-04-14
 */
@ConstructOnEnable
public class BossBarSpawnDisplayService implements SpawnDisplayService {
    private final SpawnService spawnService;
    private final SpawnChangeService spawnChangeService;
    private final Server server;
    private final DisplayNameService names;
    private final TaskService tasks;

    @Inject
    @SuppressWarnings("deprecation")
    public BossBarSpawnDisplayService(SpawnService spawnService, SpawnChangeService spawnChangeService,
                                      Server server, DisplayNameService names, PlayerInitService initService, TaskService tasks) {
        this.spawnService = spawnService;
        this.spawnChangeService = spawnChangeService;
        this.server = server;
        this.names = names;
        this.tasks = tasks;
        initService.registerInitHandler(player -> updateForAllPlayers());
        initService.registerDeInitHandler(BossBarAPI::removeBar); //reloads cause stale bars otherwise
    }

    @Override
    public void updateForAllPlayers() {
        if (spawnService.getCurrentSpawn().isPresent()) {
            updatePlayersBossBars();
        } else {
            resetAllBars();
        }
    }

    private void updatePlayersBossBars() {
        Message statusMessage = createStatusMessage();
        new ArrayList<Player>(server.getOnlinePlayers()).stream()
                .collect(Collectors.groupingBy(player -> I18n.getLocaleFor(player.getUniqueId())))
                .forEach(((locale, players) -> sendToAll(I18n.loc(locale, statusMessage), players)));
    }

    @SuppressWarnings("deprecation")
    private void sendToAll(String message, List<Player> players) {
        float fractionProgress = spawnChangeService.findFractionProgressToNextSpawn();
        players.forEach(player -> {
            BossBarAPI.removeBar(player);
            BossBarAPI.setMessage(player, message, fractionProgress * 100F);
        });
    }

    @SuppressWarnings("deprecation")
    private void resetAllBars() {
        server.getOnlinePlayers().forEach(BossBarAPI::removeBar);
    }

    private Message createStatusMessage() {
        return Message.of("spawn!bossbar-format",
                findCurrentMapName(),
                findMinutesLeftUntilNextChange()
        );
    }

    private long findMinutesLeftUntilNextChange() {
        long timeLeftMinutes = spawnChangeService.findTimeUntilNextChange().toMinutes();
        return Math.max(timeLeftMinutes, 0);
    }

    private Message findCurrentMapName() {
        return spawnService.getCurrentSpawn().map(names::displayName).orElseThrow(IllegalStateException::new);
    }
}
