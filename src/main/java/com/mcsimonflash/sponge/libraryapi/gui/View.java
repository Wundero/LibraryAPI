package com.mcsimonflash.sponge.libraryapi.gui;

import com.google.common.collect.Maps;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Map;

/* TODO:
 * Develop a system to construct the necessary Map<Integer, Element> to encode
 * a view. This might be done through a class called ViewMap which holds a
 * builder. Some methods for the builder might include:
 *
 *  - .set(Element element, int position)
 *  - .set(Element element, int... positions)
 *  - .set(Element element, Collection<Integer> positions)
 *  - .addAll(Map<Integer, Element> elements)
 *
 * Additionally, it might be helpful to consider the following utility methods
 * (and possibly others). Notably, this would require knowing the shape/size of
 * the inventory either through an InventoryArchetype or InventoryDimension.
 *
 *  - .row(Element element, int row) //fills in an entire row
 *  - .column(Element element, int column) //fills in an entire column
 *  - .border(Element element) //creates a border around the inventory
 *  - .checker(Element even, Element odd) //creates a checker pattern
 *  - .fill(Element element) //fill all empty slots
 *
 * Finally, would we want to implement Sponge's ResettableBuilder?
 */
/* TODO:
 * What happens if the map has an index that's not in the given inventory? What
 * about cases where an entry in the map is never called?
 */
/**
 * A representation of a GUI view.
 *
 * The primary design focus of this class was to prevent rebuilding the entire
 * inventory each time the view opens for a player. This has the additional
 * benefit of having changes to the inventory update automatically.
 */
public class View {

    private final OrderedInventory inventory;
    private final Map<Slot, Element> slots = Maps.newHashMap();
    private final PluginContainer plugin;

    /**
     * Creates a new instance and builds an {@link OrderedInventory} from the
     * given {@link InventoryArchetype}. The {@link PluginContainer} is used
     * for building the inventory and as a {@link Cause#source(Object)} when
     * the view is opened.
     *
     * @param archetype the archetype describing this inventory
     * @param plugin the plugin creating this view
     */
    public View(InventoryArchetype archetype, PluginContainer plugin) {
        this.inventory = Inventory.builder()
                .of(archetype)
                .listener(ClickInventoryEvent.class, this::processClick)
                .build(plugin)
                .query(OrderedInventory.class);
        this.plugin = plugin;
    }

    /**
     * Defines this view as encoded by the given {@link Map<Integer, Element>},
     * where the key of the map is a {@link SlotIndex} number and the value is
     * the represented Element.
     *
     * This method updates every slot in the inventory and fills in missing
     * indices in the map with {@link Element#empty()}.
     *
     * @param elements the map of slot indices to elements
     */
    public void define(Map<Integer, Element> elements) {
        slots.clear();
        for (int i = 0; i < inventory.size(); i++) {
            updateIndex(i, elements.getOrDefault(i, Element.empty()));
        }
    }

    /**
     * Updates the existing view with the given {@link Map<Integer, Element>}.
     *
     * This method will only update indices that are present in the provided
     * map. All other slots will be unchanged.
     *
     * @see #define(Map) for comparison
     *
     * @param elements
     */
    public void update(Map<Integer, Element> elements) {
        for (Map.Entry<Integer, Element> entry : elements.entrySet()) {
            updateIndex(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Attaches the given element to the slot located at the given index, if
     * present. Used in both #define and #update.
     *
     * @param index the index of the slot
     * @param element the element for the slot
     */
    private void updateIndex(int index, Element element) {
        Slot slot = inventory.getSlot(SlotIndex.of(index)).orElse(null);
        if (slot != null) {
            slot.set(element.getItem());
            slots.put(slot, element);
        }
    }

    /**
     * Opens this view for the given player, caused by {@link #plugin}.
     *
     * @param player the player to open the inventory for
     */
    public void open(Player player) {
        player.openInventory(inventory, Cause.source(plugin).build());
    }

    /**
     * Process a {@link ClickInventoryEvent} for the inventory of this view. If
     * one of the slots in the transaction is found in {@link #slots}, the event
     * is canceled and the associated element is processed for the player.
     *
     * @param event the event
     */
    private void processClick(ClickInventoryEvent event) {
        event.setCancelled(true);
        Player player = event.getCause().first(Player.class).orElse(null);
        if (player != null) {
            for (SlotTransaction transaction : event.getTransactions()) {
                Element element = slots.get(transaction.getSlot());
                if (element != null) {
                    event.setCancelled(true);
                    element.process(player);
                }
            }
        }
    }

}