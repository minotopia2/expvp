/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp;

import com.google.inject.AbstractModule;
import me.minotopia.expvp.api.inject.DataFolder;
import me.minotopia.expvp.model.ModelModule;
import me.minotopia.expvp.skill.SkillModule;
import me.minotopia.expvp.skilltree.SkillTreeModule;
import me.minotopia.expvp.util.SessionProvider;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;

/**
 * Provides the dependency wiring for common base functionality common to multiple modules of Expvp.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-03-09
 */
public class EPRootModule extends AbstractModule {
    private final EPPlugin plugin;

    public EPRootModule(EPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(Server.class).toInstance(plugin.getServer());
        bind(BukkitScheduler.class).toInstance(plugin.getServer().getScheduler());
        bind(File.class).annotatedWith(DataFolder.class).toInstance(plugin.getDataFolder());
        bind(SessionProvider.class).toInstance(plugin.getSessionProvider());
        install(new ModelModule());
        install(new SkillModule());
        install(new SkillTreeModule());
    }
}