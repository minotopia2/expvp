/*
 * This file is part of Expvp,
 * Copyright (c) 2016-2017.
 *
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt.
 */

package me.minotopia.expvp.spawn;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import me.minotopia.expvp.api.spawn.MapSpawn;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Represents a map spawn backed by a YAML data store.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-04-12
 */
public class YamlMapSpawn implements MapSpawn, ConfigurationSerializable {
    private static final String ID_PATH = "id";
    private static final String LOCATION_PATH = "loc";
    private static final String AUTHOR_PATH = "author";
    private final String id;
    private XyLocation location;
    private String author = "";

    public YamlMapSpawn(String id) {
        this.id = id;
    }

    public YamlMapSpawn(Map<String, Object> input) {
        MapConfig config = HashMapConfig.of(input);
        this.id = config.findString(ID_PATH).orElseThrow(IllegalArgumentException::new);
        this.location = config.findTyped(LOCATION_PATH, XyLocation.class).orElse(null);
        this.author = config.findString(AUTHOR_PATH).orElse("");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean hasLocation() {
        return location != null;
    }

    @Override
    public XyLocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(XyLocation location) {
        Preconditions.checkNotNull(location, "location");
        this.location = location;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        Preconditions.checkNotNull(author, "author");
        this.author = author;
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(ID_PATH, id)
                .put(LOCATION_PATH, location)
                .put(AUTHOR_PATH, author)
                .build();
    }

    @Override
    public String toString() {
        return "YamlMapSpawn " +
                "" + id +
                " at " + location +
                " by '" + author + '\'';
    }
}
