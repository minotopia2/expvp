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

package me.minotopia.expvp.ui.renderer;

import li.l1t.common.inventory.SlotPosition;
import li.l1t.common.inventory.gui.holder.ElementHolder;
import li.l1t.common.inventory.gui.holder.TemplateElementHolder;
import li.l1t.common.test.util.MockHelper;
import me.minotopia.expvp.skilltree.SimpleSkillTreeNode;
import me.minotopia.expvp.skilltree.SkillTree;
import me.minotopia.expvp.skilltree.SkillTreeNode;
import me.minotopia.expvp.ui.element.skill.ObtainableSkillElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for the node structure renderer.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-08-14
 */
public class NodeStructureRendererTest {
    private static final ItemStack MELON_STACK = new ItemStack(Material.MELON_BLOCK);

    @Test
    public void render__root_only() throws Exception {
        //given
        MockHelper.mockServer();
        SkillTree tree = givenASkillTree();
        NodeStructureRenderer renderer = givenANodeRenderer(tree);
        //when
        renderer.render();
        //then
        thenTheNodeAtIs(SlotPosition.ofXY(0, 3), tree, renderer);
    }

    private NodeStructureRenderer givenANodeRenderer(SkillTree tree) {
        return new TreeStructureRenderer(tree, node -> new ObtainableSkillElement(null, node, null, null, null)).createNodeRenderer();
    }

    private SkillTree givenASkillTree() {
        SkillTree tree = new SkillTree("test-tree");
        tree.setIconStack(MELON_STACK);
        return tree;
    }

    @Test
    public void render__single_child() throws Exception {
        //given
        MockHelper.mockServer();
        SkillTree tree = givenASkillTree();
        SimpleSkillTreeNode child = tree.createChild();
        NodeStructureRenderer renderer = givenANodeRenderer(tree);
        //when
        renderer.render();
        //then
        thenTheNodeAtIs(SlotPosition.ofXY(0, 3), tree, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(2, 3), child, renderer);
        thenThereIsAPlaceholderAt(SlotPosition.ofXY(1, 3), renderer);
    }

    private void thenTheNodeAtIs(SlotPosition position, SkillTreeNode<?> expected,
                                 NodeStructureRenderer renderer) {
        ElementHolder template = renderer.getTarget();
        ObtainableSkillElement element = (ObtainableSkillElement) template.getElement(position.toSlotId());
        assertThat("wrong element at pos " + position, element.getNode(), is(expected));
    }

    private void thenThereIsAPlaceholderAt(SlotPosition position, NodeStructureRenderer renderer) {
        TemplateElementHolder template = (TemplateElementHolder) renderer.getTarget();
        assertThat("expected placeholder at " + position,
                template.hasPlaceholderAt(position.toSlotId()), is(true));
    }

    @Test
    public void render__three_children() throws Exception {
        //given
        MockHelper.mockServer();
        SkillTree tree = givenASkillTree();
        SimpleSkillTreeNode child1 = tree.createChild();
        SimpleSkillTreeNode child2 = tree.createChild();
        SimpleSkillTreeNode child3 = tree.createChild();
        NodeStructureRenderer renderer = givenANodeRenderer(tree);
        //when
        renderer.render();
        //then
        thenTheNodeAtIs(SlotPosition.ofXY(0, 3), tree, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(2, 1), child1, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(2, 3), child2, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(2, 5), child3, renderer);
        thenThereArePlaceholdersVerticallyAtBetween(1, 1, 5, renderer);
    }

    private void thenThereArePlaceholdersVerticallyAtBetween(int x, int startY, int endY,
                                                             NodeStructureRenderer renderer) {
        for (int y = startY; y <= endY; y++) {
            thenThereIsAPlaceholderAt(SlotPosition.ofXY(x, y), renderer);
        }
    }

    @Test
    public void render__single_grandchild() throws Exception {
        //given
        MockHelper.mockServer();
        SkillTree tree = givenASkillTree();
        SimpleSkillTreeNode child = tree.createChild();
        SimpleSkillTreeNode grandchild = child.createChild();
        NodeStructureRenderer renderer = givenANodeRenderer(tree);
        //when
        renderer.render();
        //then
        thenTheNodeAtIs(SlotPosition.ofXY(0, 3), tree, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(2, 3), child, renderer);
        thenTheNodeAtIs(SlotPosition.ofXY(4, 3), grandchild, renderer);
        thenThereIsAPlaceholderAt(SlotPosition.ofXY(1, 3), renderer);
        thenThereIsAPlaceholderAt(SlotPosition.ofXY(3, 3), renderer);
    }
}
