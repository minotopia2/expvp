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

package me.minotopia.expvp.skill.obtainment;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import li.l1t.common.exception.UserException;
import me.minotopia.expvp.api.handler.HandlerService;
import me.minotopia.expvp.api.i18n.DisplayNameService;
import me.minotopia.expvp.api.model.MutablePlayerData;
import me.minotopia.expvp.api.model.ObtainedSkill;
import me.minotopia.expvp.api.model.PlayerData;
import me.minotopia.expvp.api.service.PlayerDataService;
import me.minotopia.expvp.api.service.ResearchService;
import me.minotopia.expvp.i18n.exception.I18nUserException;
import me.minotopia.expvp.skill.meta.Skill;
import me.minotopia.expvp.skilltree.SimpleSkillTreeNode;
import me.minotopia.expvp.skilltree.SkillTreeNode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service that provides an interface for performing actions related to obtainment of skills.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-09-07
 */
@Singleton
public class SimpleResearchService implements ResearchService {
    private final PlayerDataService playerDataService;
    private final DisplayNameService names;
    private final HandlerService handlerService;

    @Inject
    public SimpleResearchService(PlayerDataService playerDataService, DisplayNameService names, HandlerService handlerService) {
        this.playerDataService = playerDataService;
        this.names = names;
        this.handlerService = handlerService;
    }

    @Override
    public void research(Player player, SimpleSkillTreeNode node) {
        Preconditions.checkNotNull(node, "node");
        Skill skill = node.getValue();
        Preconditions.checkNotNull(skill, "node.getValue()");
        MutablePlayerData playerData = playerDataService.findOrCreateDataMutable(player.getUniqueId());
        checkIsNotObtained(player.getUniqueId(), skill);
        checkIsObtainable(player.getUniqueId(), node);
        playerData.addSkill(skill);
        playerDataService.saveData(playerData);
        handlerService.registerHandlers(playerData);
    }

    private void checkIsNotObtained(UUID playerId, Skill skill) {
        if (has(playerId, skill)) {
            throw new UserException("Das ist bereits erforscht!");
        }
    }

    @Override
    public void forget(Player player, Skill skill) {
        Preconditions.checkNotNull(skill, "skill");
        MutablePlayerData playerData = playerDataService.findOrCreateDataMutable(player.getUniqueId());
        checkIsObtained(player.getUniqueId(), skill);
        playerData.removeSkill(skill);
        playerDataService.saveData(playerData);
    }

    private void checkIsObtained(UUID playerId, Skill skill) {
        if (!has(playerId, skill)) {
            throw new UserException("Das ist noch nicht erforscht!");
        }
    }

    private void checkIsObtainable(UUID playerId, SimpleSkillTreeNode node) {
        if (!doesParentPermitObtainment(node, playerId)) {
            throw new I18nUserException(
                    "error!tree.missing-parent",
                    names.displayName(node.getValue()), names.displayName(node.getParent().getValue())
            );
        }
    }

    private boolean doesParentPermitObtainment(SimpleSkillTreeNode node, UUID playerId) {
        SkillTreeNode<?> parent = node.getParent();
        return parent == null || (parent.getValue() != null && has(playerId, parent.getValue()));
    }

    @Override
    public boolean has(UUID playerId, Skill skill) {
        return playerDataService.findData(playerId)
                .map(PlayerData::getSkills).orElseGet(Collections::emptySet)
                .stream().anyMatch(skill::matches);
    }

    @Override
    public Collection<String> getObtainedSkills(UUID playerId) {
        PlayerData playerData = playerDataService.findOrCreateData(playerId);
        return playerData.getSkills().stream()
                .map(ObtainedSkill::getSkillId)
                .collect(Collectors.toList());
    }
}
