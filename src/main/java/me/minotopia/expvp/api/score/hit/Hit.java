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

package me.minotopia.expvp.api.score.hit;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single hit that a known culprit executed on a known victim.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-05-01
 */
public interface Hit {
    UUID getCulpritId();

    Instant getInstant();

    double getDamage();

    boolean isOlderThan(Duration duration);
}
