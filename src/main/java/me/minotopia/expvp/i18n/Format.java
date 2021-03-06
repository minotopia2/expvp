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

package me.minotopia.expvp.i18n;


import li.l1t.common.i18n.Message;

/**
 * Static utility class that contains a few helpful methods for wrapping messages in common formats.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-03-29
 */
public class Format {
    private static Message wrapIn(String key, Message message) {
        return Message.of("core!format." + key, message);
    }

    public static Message broadcast(Message message) {
        return wrapIn("broadcast", message);
    }

    public static Message broadcast(String key, Object... arguments) {
        return wrapIn("broadcast", Message.of(key, arguments));
    }

    public static Message internalError(Message message) {
        return wrapIn("error-internal", message);
    }

    public static Message internalError(String key, Object... arguments) {
        return wrapIn("error-internal", Message.of(key, arguments));
    }

    public static Message userError(Message message) {
        return wrapIn("error-user", message);
    }

    public static Message userError(String key, Object... arguments) {
        return wrapIn("error-user", Message.of(key, arguments));
    }

    public static Message warning(Message message) {
        return wrapIn("warning", message);
    }

    public static Message warning(String key, Object... arguments) {
        return wrapIn("warning", Message.of(key, arguments));
    }

    public static Message result(Message message) {
        return wrapIn("result", message);
    }

    public static Message result(String key, Object... arguments) {
        return wrapIn("result", Message.of(key, arguments));
    }

    public static Message success(Message message) {
        return wrapIn("result-success", message);
    }

    public static Message success(String key, Object... arguments) {
        return wrapIn("result-success", Message.of(key, arguments));
    }

    public static Message listHeader(Message message) {
        return wrapIn("list-header", message);
    }

    public static Message listHeader(String key, Object... arguments) {
        return wrapIn("list-header", Message.of(key, arguments));
    }

    public static Message header(Message message) {
        return wrapIn("header", message);
    }

    public static Message header(String key, Object... arguments) {
        return wrapIn("header", Message.of(key, arguments));
    }

    public static Message listItem(Message message) {
        return wrapIn("list-item", message);
    }

    public static Message listItem(String key, Object... arguments) {
        return wrapIn("list-item", Message.of(key, arguments));
    }

    public static Message bool(boolean input) {
        return Message.of(input ? "core!yes" : "core!no");
    }

    public static Message rank(long num) {
        if (num >= 11L && num <= 13L) {
            return Message.of("core!number.other", num); //eleventh, twelfth, thirteenth
        }
        switch ((int) (num % 10L)) {
            case 1:
                return Message.of("core!number.one", num);
            case 2:
                return Message.of("core!number.two", num);
            case 3:
                return Message.of("core!number.three", num);
            default:
                return Message.of("core!number.other", num);
        }
    }
}
