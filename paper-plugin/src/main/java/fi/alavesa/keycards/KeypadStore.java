package fi.alavesa.keycards;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Persistent store of keypad-locked doors. One door = one bottom-block key
 * "world:x,y,z"; each entry holds a per-door salt and the SHA-256 of salt+code.
 * The plaintext code is never written, so a peek at keypads.yml leaks no codes.
 * Lives in the plugin data folder (server-only) and survives restarts like a
 * normal config - the reader/door bindings themselves are datapack entities, but
 * a keypad code has nowhere else to live, so it gets its own file here.
 */
final class KeypadStore {

    private final File file;
    private final YamlConfiguration data;
    private final SecureRandom random = new SecureRandom();

    KeypadStore(KeycardsPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "keypads.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    /** Bottom-block key: doors are addressed by their lower half so either half resolves alike. */
    private static String key(Block block) {
        Block bottom = Doors.bottomOf(block);
        Block b = bottom != null ? bottom : block;
        return b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ();
    }

    boolean isKeypad(Block block) {
        return data.contains(key(block) + ".hash");
    }

    /** Bind (or re-bind) a secret code to this door. Hashes with a fresh per-door salt. */
    void bind(Block block, String code) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String saltHex = HexFormat.of().formatHex(salt);
        String base = key(block);
        data.set(base + ".salt", saltHex);
        data.set(base + ".hash", hash(saltHex, code));
        save();
    }

    /** Remove any keypad binding on this door. Returns true if one was actually removed. */
    boolean unbind(Block block) {
        String base = key(block);
        if (!data.contains(base + ".hash")) return false;
        data.set(base, null);
        save();
        return true;
    }

    /** True if this door is a keypad and the entered code matches its stored hash. */
    boolean matches(Block block, String code) {
        String base = key(block);
        String saltHex = data.getString(base + ".salt");
        String stored = data.getString(base + ".hash");
        if (saltHex == null || stored == null) return false;
        return MessageDigest.isEqual(
            stored.getBytes(StandardCharsets.UTF_8),
            hash(saltHex, code).getBytes(StandardCharsets.UTF_8));
    }

    private static String hash(String saltHex, String code) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(HexFormat.of().parseHex(saltHex));
            byte[] digest = md.digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save keypads.yml", e);
        }
    }
}
