package fi.alavesa.keycards;

import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired for every keycard swipe at a reader, BEFORE any feedback or door
 * movement. Other plugins (the Doors plugin, notably) listen to open their
 * own doors on granted swipes - and may CANCEL the event to take over the
 * interaction entirely (e.g. a locked door: the listener shows "Door is
 * locked" and nothing else happens, not even the grant chirp).
 */
public final class KeycardSwipeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Interaction reader;
    private final int requiredLevel;
    private final int cardLevel;
    private final boolean granted;
    private boolean cancelled;

    public KeycardSwipeEvent(Player player, Interaction reader,
                             int requiredLevel, int cardLevel, boolean granted) {
        this.player = player;
        this.reader = reader;
        this.requiredLevel = requiredLevel;
        this.cardLevel = cardLevel;
        this.granted = granted;
    }

    public Player getPlayer() { return player; }
    public Interaction getReader() { return reader; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getCardLevel() { return cardLevel; }
    /** True when the card's level suffices for this reader. */
    public boolean isGranted() { return granted; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
