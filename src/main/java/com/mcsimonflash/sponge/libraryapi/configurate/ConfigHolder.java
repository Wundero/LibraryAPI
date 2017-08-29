package com.mcsimonflash.sponge.libraryapi.configurate;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

/**
 * Contains a {@link ConfigurationLoader} and it's root node.
 *
 * @param <T> The type of node (matches the loader's {@param <NodeType>})
 */
public class ConfigHolder<T extends ConfigurationNode> {

    private final ConfigurationLoader<T> loader;
    private final T node;

    /**
     * Creates a new instance from an existing {@link ConfigurationLoader<T>}.
     *
     * @param loader the configuration loader for the config file
     * @throws IOException if loader.load() causes an error
     */
    public ConfigHolder(ConfigurationLoader<T> loader) throws IOException {
        this.loader = loader;
        this.node = loader.load();
    }

    /**
     * Returns the node represented by the given path from
     * the root node.
     *
     * The type returned by this method is guaranteed to match
     * the type of {@link #node}.
     *
     * @param path the child nodes, if any
     * @return the node at the provided path
     */
    @SuppressWarnings("unchecked")
    public T getNode(Object... path) {
        return (T) node.getNode(path);
    }

    /**
     * Attempts to save the {@link #node} to the {@link #loader}.
     *
     * If {@link ConfigurationLoader#save(ConfigurationNode)}
     *
     * @return true if the save was successful, else false.
     */
    public boolean save() {
        try {
            loader.save(node);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}