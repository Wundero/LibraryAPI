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
    private T node;

    /**
     * Creates a new instance from an existing {@link ConfigurationLoader<T>}.
     *
     * @param loader the configuration loader for the config file
     * @throws IOException if loader.load() causes an error
     */
    public ConfigHolder(ConfigurationLoader<T> loader) throws IOException {
        this.loader = loader;
        load();
    }
    
    /**
     * Returns the node representing the root configuration.
     *
     * @return the root node.
     */
    public T getRootNode() {
        return node;
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
    
    /**
     * Attempts to load the {@link #node} from the {@link #loader}.
     *
     * @return whether the load was successful.
     */
    public boolean load() {
        try {
            this.node = this.loader.load();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
