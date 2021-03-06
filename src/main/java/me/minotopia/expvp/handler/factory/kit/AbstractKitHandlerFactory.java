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

package me.minotopia.expvp.handler.factory.kit;

import li.l1t.common.string.ArgumentFormatException;
import me.minotopia.expvp.api.handler.factory.InvalidHandlerSpecException;
import me.minotopia.expvp.api.handler.factory.KitHandlerFactory;
import me.minotopia.expvp.api.handler.kit.KitHandler;
import me.minotopia.expvp.handler.factory.AbstractHandlerSpecNode;
import me.minotopia.expvp.handler.factory.HandlerArgs;
import me.minotopia.expvp.skill.meta.Skill;
import org.bukkit.Material;

/**
 * Abstract base class for kit handler factories.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-03-10
 */
public abstract class AbstractKitHandlerFactory extends AbstractHandlerSpecNode
        implements KitHandlerFactory {
    public static int SLOT_ID_INDEX = 0;
    public static int MATERIAL_INDEX = 1;
    public static int AMOUNT_INDEX = 2;

    public AbstractKitHandlerFactory(String ownHandlerSpec) {
        super(ownHandlerSpec);
    }

    @Override
    public KitHandler createHandler(Skill skill) throws InvalidHandlerSpecException {
        try {
            String relativeSpec = findRelativeSpec(skill);
            return createHandler(skill, new HandlerArgs(relativeSpec));
        } catch (ArgumentFormatException e) {
            throw new InvalidHandlerSpecException(e.getMessage(), skill.getHandlerSpec(), this);
        } catch (Exception e) {
            throw new InvalidHandlerSpecException(e.getClass().getSimpleName() + ":" + e.getMessage(), skill.getHandlerSpec(), this);
        }
    }

    /**
     * Creates a handler using this kit handler factory's specific creation mechanism.
     *
     * @param skill the skill to create the handler for
     * @param args  the argument list, retrieved from the skill's handler spec
     * @return the created kit handler for given arguments
     */
    protected abstract KitHandler createHandler(Skill skill, HandlerArgs args);

    /**
     * @param args the arguments to get the slot id from
     * @return the slot id for given arguments
     */
    protected int slotId(HandlerArgs args) {
        return args.intArg(SLOT_ID_INDEX);
    }

    protected Material material(HandlerArgs args) {
        return args.enumArg(Material.class, MATERIAL_INDEX);
    }

    protected int amount(HandlerArgs args) {
        return args.intArg(AMOUNT_INDEX);
    }
}
