/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.spawn;

import com.google.inject.Inject;
import li.l1t.common.util.task.TaskService;
import me.minotopia.expvp.api.misc.ConstructOnEnable;
import me.minotopia.expvp.api.spawn.SpawnChangeService;
import org.bukkit.Server;
import org.bukkit.World;

import java.time.Duration;

/**
 * A task that updates the in-game time based on the current fractional progress to the next spawn
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-04-23
 */
@ConstructOnEnable
public class SpawnDayTimeTask implements Runnable {
    private final Server server;
    private final SpawnChangeService spawnChangeService;

    @Inject
    public SpawnDayTimeTask(Server server, SpawnChangeService spawnChangeService, TaskService tasks) {
        this.server = server;
        this.spawnChangeService = spawnChangeService;
        tasks.repeating(this, Duration.ofSeconds(1));
    }

    @Override
    public void run() {
        server.getWorlds().forEach(this::setWorldTimeBasedOnProgress);
    }

    private void setWorldTimeBasedOnProgress(World world) {
        world.setTime(findDayTimeForSpawnProgress(spawnChangeService.findFractionProgressToNextSpawn()));
    }

    private int findDayTimeForSpawnProgress(float fractionProgress) {
        return Math.round(22_500F + (fractionProgress * 17520F));
    }
}
