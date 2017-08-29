package com.mcsimonflash.sponge.libraryapi.gui;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.function.Consumer;

/* TODO:
 * Consider having static creation methods such as Element.of(ItemStack) and
 * Element.of(ItemStack, Consumer<Player>) as opposed to using new.
 */
/* TODO:
 * Is there a need to have simplified methods to handle common consumers, such
 * as process commands, open new views, etc.
 */
/* TODO:
 * Some GUI cases, such as a trade window, would need to allow players to edit
 * certain slots. Is this something feasible for this project, and if so how
 * should we go about determining who can edit a slot?
 *
 * Because this would need to be player specific, we'd likely have to have a
 * Function<Player, Boolean> to return whether the given Player can edit the
 * slot and cancel (or not) the event accordingly.
 *
 * An alternative is to consider having two independent views, and requiring a
 * simultaneous update of each.
 */
/**
 * A class representing a GUI element.
 */
public class Element {

    /**
     * An empty element, represented by {@link ItemStack#empty()} and an empty
     * consumer. This is required to 'reset' an inventory slot and also allows
     * the equality operator (==) to be used for comparison.
     */
    private static final Element EMPTY = new Element(ItemStack.empty(), player -> {});
    public static Element empty() {
        return EMPTY;
    }

    private final ItemStack item;
    private final Consumer<Player> action;

    /**
     * Creates a new instance with the specified {@link ItemStack} item and
     * {@link Consumer<Player>} action.
     *
     * @param item the item displayed in the slot
     * @param action the action processed when the slot is clicked
     */
    public Element(ItemStack item, Consumer<Player> action) {
        this.item = item;
        this.action = action;
    }

    /**
     * @return a copy of this element's item
     */
    public ItemStack getItem() {
        return item.copy();
    }

    /**
     * Accepts the action for a given player. This method is called when an
     * {@link org.spongepowered.api.event.item.inventory.ClickInventoryEvent}
     * is registered for a slot with this element by the player.
     *
     * @param player the player who clicked on the slot
     */
    public void process(Player player) {
        action.accept(player);
    }

}