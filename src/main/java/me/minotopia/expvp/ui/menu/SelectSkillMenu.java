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

package me.minotopia.expvp.ui.menu;

import com.google.inject.Inject;
import li.l1t.common.inventory.gui.PaginationListMenu;
import me.minotopia.expvp.EPPlugin;
import me.minotopia.expvp.api.i18n.DisplayNameService;
import me.minotopia.expvp.api.skill.SkillService;
import me.minotopia.expvp.i18n.I18n;
import me.minotopia.expvp.skill.meta.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * An inventory menu that allows to select a skill from the list of all available skills and
 * executes a custom handler when a skill is selected.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-08-18
 */
public class SelectSkillMenu extends PaginationListMenu<Skill> {
    private final DisplayNameService names;
    private final SkillService skills;
    private final Consumer<Skill> callback;
    private boolean selectionMade = false;

    public SelectSkillMenu(Plugin plugin, Player player, DisplayNameService names, SkillService skills,
                           Consumer<Skill> callback) {
        super(plugin, player);
        this.names = names;
        this.skills = skills;
        this.callback = callback;
    }

    @Override
    protected void handleValueClick(Skill skill, InventoryClickEvent inventoryClickEvent) {
        selectionMade = true;
        callback.accept(skill);
    }

    @Override
    protected ItemStack drawItem(Skill skill) {
        return skills.createRawSkillIconFor(skill, false, getPlayer()).produce();
    }

    @Override
    protected String formatTitle(int pageNum, int pageCount) {
        return I18n.loc(getPlayer(), "admin!ui.skillsel.title", pageNum, pageCount);
    }

    public static class Factory {
        private final EPPlugin plugin;
        private final SkillService skills;
        private final DisplayNameService names;

        @Inject
        public Factory(EPPlugin plugin, SkillService skills, DisplayNameService names) {
            this.plugin = plugin;
            this.skills = skills;
            this.names = names;
        }

        public SelectSkillMenu createMenu(Player player, Consumer<Skill> callback) {
            SelectSkillMenu menu = new SelectSkillMenu(plugin, player, names, skills, callback);
            menu.addItems(skills.getAllSkills());
            menu.open();
            return menu;
        }

        public SelectSkillMenu openMenu(Player player, Consumer<Skill> callback) {
            SelectSkillMenu menu = createMenu(player, callback);
            menu.open();
            return menu;
        }
    }
}
