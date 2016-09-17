/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2016.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.command;

import me.minotopia.expvp.command.service.CommandService;

/**
 * Represents a command that operates through a service.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-14
 */
public interface ServiceBackedCommand<T extends CommandService> {
    void setCommandService(T service);
}