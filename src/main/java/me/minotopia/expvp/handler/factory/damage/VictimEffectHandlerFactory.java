/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.handler.factory.damage;

import me.minotopia.expvp.handler.damage.VictimEffectHandler;
import me.minotopia.expvp.handler.factory.HandlerArgs;
import me.minotopia.expvp.skill.meta.Skill;

/**
 * Creates victim effect handlers.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-03-23
 */
public class VictimEffectHandlerFactory extends AbstractDamageHandlerFactory {
    public VictimEffectHandlerFactory(String ownHandlerSpec) {
        super(ownHandlerSpec);
    }

    @Override
    public String getDescription() {
        return "victim effect handler: probability,effect_type,duration_in_seconds,potion_level";
    }

    @Override
    protected VictimEffectHandler createHandler(Skill skill, HandlerArgs args) {
        return new VictimEffectHandler(
                skill, probabilityPerCent(args), potionEffectType(args),
                durationSeconds(args), potionLevel(args)
        );
    }
}