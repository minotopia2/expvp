/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.ui.menu;

import com.google.inject.Inject;
import li.l1t.common.inventory.SlotPosition;
import li.l1t.common.inventory.gui.element.Placeholder;
import li.l1t.common.util.inventory.InventoryHelper;
import li.l1t.common.util.inventory.ItemStackFactory;
import me.minotopia.expvp.EPPlugin;
import me.minotopia.expvp.api.i18n.DisplayNameService;
import me.minotopia.expvp.api.score.TalentPointService;
import me.minotopia.expvp.api.service.ResearchService;
import me.minotopia.expvp.api.skill.SkillService;
import me.minotopia.expvp.i18n.I18n;
import me.minotopia.expvp.i18n.exception.I18nInternalException;
import me.minotopia.expvp.skilltree.SkillTree;
import me.minotopia.expvp.skilltree.SkillTreeManager;
import me.minotopia.expvp.ui.element.BackButton;
import me.minotopia.expvp.ui.element.skill.ObtainableSkillElement;
import me.minotopia.expvp.ui.renderer.TreeStructureRenderer;
import me.minotopia.expvp.ui.renderer.exception.RenderingException;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

/**
 * An inventory menu that provides the frontend for a skill tree.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-07-22
 */
public class SkillTreeMenu extends AbstractEPMenu {
    private final SkillTree tree;
    private TreeStructureRenderer renderer;

    private SkillTreeMenu(EPPlugin plugin, Player player, SkillTree tree, Runnable backButtonHandler,
                          TalentPointService talentPoints, String displayName,
                          SkillService skills, ResearchService researchService, SkillTreeManager manager) {
        super(plugin, displayName, player);
        this.tree = tree;
        this.renderer = new TreeStructureRenderer(tree,
                node -> new ObtainableSkillElement(this, node, skills, researchService)
        );
        if (backButtonHandler != null) {
            addElement(0, new BackButton(inventoryMenu -> backButtonHandler.run(), getPlayer()));
        }
        addElement(1, createTreeInfoElement(manager, talentPoints));
    }

    private Placeholder createTreeInfoElement(SkillTreeManager manager, TalentPointService talentPoints) {
        ItemStackFactory factory = new ItemStackFactory(manager.createIconFor(tree, getPlayer()));
        int talentPointCount = talentPoints.getCurrentTalentPointCount(getPlayer());
        if (talentPointCount > 0) {
            factory.enchantUnsafe(Enchantment.WATER_WORKER, 1).hideEnchants()
                    .amount(talentPointCount)
                    .lore(I18n.loc(getPlayer(), "core!inv.tree.tp-count", talentPointCount));
        } else {
            factory.lore(I18n.loc(getPlayer(), "core!inv.tree.no-tp"));
        }
        return new Placeholder(factory.produce());
    }

    private void applyRenderer() {
        if (!renderer.isRendered()) {
            attemptRender(renderer);
        }
        clearContents();
        renderer.getTemplate().applySoft(this);
    }

    private void clearContents() {
        IntStream.range(SlotPosition.ofXYValidated(0, 1).toSlotId(), InventoryHelper.DEFAULT_MAX_INVENTORY_SIZE)
                .forEach(slotId -> setElementRaw(slotId, null));
    }

    private void attemptRender(TreeStructureRenderer renderer) {
        try {
            renderer.render();
        } catch (RenderingException e) {
            throw new I18nInternalException("error!tree.render-fail");
        }
    }

    private static void openInTransaction(SkillTreeMenu menu) {
        menu.getPlugin().getSessionProvider().inSession(ignored -> menu.open());
    }

    @Override
    @SuppressWarnings("deprecation")
    protected ItemStackFactory getPlaceholderFactory() {
        return new ItemStackFactory(Material.STAINED_GLASS_PANE)
                .legacyData((byte) 15)
                .displayName("§7§l*");
    }

    public SkillTree getTree() {
        return renderer.getTree();
    }

    public static class Factory {
        private final EPPlugin plugin;
        private final TalentPointService talentPoints;
        private final DisplayNameService names;
        private final SkillService skills;
        private final ResearchService researchService;
        private final SkillTreeManager manager;

        @Inject
        public Factory(EPPlugin plugin, TalentPointService talentPoints, DisplayNameService names, SkillService skills,
                       ResearchService researchService, SkillTreeManager manager) {
            this.plugin = plugin;
            this.talentPoints = talentPoints;
            this.names = names;
            this.skills = skills;
            this.researchService = researchService;
            this.manager = manager;
        }

        public SkillTreeMenu createMenuWithBackButton(Player player, SkillTree tree, Runnable backButtonHandler) {
            SkillTreeMenu menu = new SkillTreeMenu(
                    plugin, player, tree, backButtonHandler, talentPoints,
                    I18n.loc(player, names.displayName(tree)),
                    skills, researchService, manager
            );
            menu.applyRenderer();
            return menu;
        }

        public SkillTreeMenu createMenuWithoutBackButton(Player player, SkillTree tree) {
            return createMenuWithBackButton(player, tree, null);
        }

        public SkillTreeMenu openMenu(Player player, SkillTree tree, Runnable backButtonHandler) {
            SkillTreeMenu menu;
            if (backButtonHandler != null) {
                menu = createMenuWithBackButton(player, tree, backButtonHandler);
            } else {
                menu = createMenuWithoutBackButton(player, tree);
            }
            openInTransaction(menu);
            return menu;
        }
    }
}
