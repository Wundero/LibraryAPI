package com.mcsimonflash.sponge.libraryapi.cooldown;

import com.mcsimonflash.sponge.libraryapi.configurate.ConfigHolder;

/* TODO:
 * The original version of this service had a path signature of User, String as
 * opposed to the current Object... signature, which is less restrictive.
 * However, a more restrictive signature would be better for most use cases
 * provided that it doesn't interfere with most actions.
 */
/**
 * Contains utility methods for dealing with processing cooldowns.
 */
public class CooldownService {

    private final ConfigHolder config;

    /**
     * Creates a new instance for the given {@link ConfigHolder}.
     *
     * @param config the ConfigHolder for the config.
     */
    public CooldownService(ConfigHolder config) {
        this.config = config;
    }

    /**
     * Returns the cooldown associated with the given path, or 0.
     *
     * @param path the path to the node
     * @return the cooldown, in milliseconds
     */
    public long getCooldown(Object... path) {
        return config.getNode(path).getLong(0);
    }

    /**
     * Returns the time since the cooldown was set.
     *
     * @param path the path to the node
     * @return the completed time, in milliseconds
     */
    public long completedTime(Object... path) {
        return System.currentTimeMillis() - getCooldown(path);
    }

    /**
     * Returns the time until the cooldown is finished.
     *
     * @param cooldown the length of the cooldown, in milliseconds
     * @param path the path to the node
     * @return the remaining time, in milliseconds
     */
    public long remainingTime(long cooldown, Object... path) {
        return cooldown - completedTime(path);
    }

    /**
     * Returns true if the cooldown is finished, else false
     *
     * @param cooldown the length of the cooldown, in milliseconds
     * @param path the path to the node
     * @return whether the cooldown is finished
     */
    public boolean isFinished(long cooldown, Object... path) {
        return remainingTime(cooldown, path) <= 0;
    }

    /**
     * Resets the cooldown of the given path to the current time as set by
     * {@link System#currentTimeMillis()}.
     *
     * @param path the path to the node
     * @return whether the config saved successfully
     */
    public boolean resetCooldown(Object... path) {
        config.getNode(path).setValue(System.currentTimeMillis());
        return config.save();
    }

}