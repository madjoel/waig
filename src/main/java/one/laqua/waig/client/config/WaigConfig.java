package one.laqua.waig.client.config;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import one.laqua.waig.client.WaigClient;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaigConfig {

    private static final String CONFIG_FILE = "config/waig.config";

    private static final String KEY_HUD_SHOW_MODE = "hud-show-mode";
    private static final String KEY_COMPASS_ITEMS = "compass-items";
    private static final String DEFAULT_COMPASS_ID = "minecraft:compass";

    private static HudShowMode hudShowMode = HudShowMode.ALWAYS;
    private static Set<Integer> compassItems = Set.of(Item.getRawId(Items.COMPASS));

    public static HudShowMode getHudShowMode() {
        return hudShowMode;
    }

    public static Set<Integer> getCompassItems() {
        return compassItems;
    }

    public static boolean readConfigFile() {
        Path filePath = Path.of(CONFIG_FILE);
        if (!Files.exists(filePath)) {
            if (!generateDefaultConfigFile()) {
                return false;
            }
        }

        try {
            Scanner scanner = new Scanner(filePath);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.strip().startsWith("#")) {
                    continue;
                }

                String[] pieces = line.split("=", 2);
                if (pieces.length != 2) {
                    continue;
                }

                String key = pieces[0].strip().toLowerCase();

                if (key.equals(KEY_HUD_SHOW_MODE)) {
                    String value = pieces[1].strip().toUpperCase();
                    try {
                        WaigConfig.hudShowMode = HudShowMode.valueOf(value);
                    } catch (IllegalArgumentException e) {
                        WaigClient.log(Level.ERROR, "The value '" + pieces[1].strip() + "' for config key '"
                                + KEY_HUD_SHOW_MODE + "' is invalid. Possible values are: "
                                + Arrays.toString(HudShowMode.values()).toLowerCase() + ". Falling back to default " +
                                "value '" + HudShowMode.ALWAYS.name().toLowerCase() + "'.");
                        return false;
                    }
                }

                if (key.equals(KEY_COMPASS_ITEMS)) {
                    String[] potentialItems = pieces[1].strip().toLowerCase().split(",");
                    Set<Integer> configItems = Arrays.stream(potentialItems)
                            .filter(potentialItemId -> potentialItemId.contains(":"))
                            .map(potentialItemId -> {
                                String[] idPieces = potentialItemId.split(":");

                                boolean containsInvalidChars = !Stream.of(idPieces)
                                        .map(String::strip)
                                        .allMatch(s -> s.matches("[a-z0-9/._-]*"));

                                if (containsInvalidChars) {
                                    WaigClient.log(Level.ERROR, "The config value '" + potentialItemId + "' " +
                                            "contains illegal characters and cannot be parsed into an item. Please " +
                                            "check the config file for errors. Ignoring this value.");
                                    return Item.getRawId(Items.AIR);
                                }

                                Identifier itemIdentifier = new Identifier(idPieces[0].strip(), idPieces[1].strip());

                                // this will need updating on Minecraft versions >=1.19.3, see https://fabricmc.net/wiki/tutorial:registry
                                return Item.getRawId(Registry.ITEM.get(itemIdentifier));
                            })
                            .filter(item -> !item.equals(Item.getRawId(Items.AIR)))
                            .collect(Collectors.toSet());
                    if (!configItems.isEmpty()) {
                        WaigConfig.compassItems = configItems;
                    } else {
                        WaigClient.log(Level.WARN, "The config key '" + KEY_COMPASS_ITEMS +  "' is present, " +
                                "but no values were configured. Defaulting to " + DEFAULT_COMPASS_ID + ".");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean generateDefaultConfigFile() {
        Path filePath = Path.of(CONFIG_FILE);
        try {
            Files.write(filePath, List.of(new String[]{
                    "# WAIG config file",
                    "",
                    "# hud show mode, defaults to " + HudShowMode.ALWAYS.name().toLowerCase(),
                    "# possible values are: " + Arrays.toString(HudShowMode.values()).toLowerCase(),
                    KEY_HUD_SHOW_MODE + " = " + HudShowMode.ALWAYS.name().toLowerCase(),
                    "",
                    "# list of valid compass items, defaults to " + DEFAULT_COMPASS_ID,
                    "# use the namespaced identifiers of items and use a colon as a separator",
                    "# separate list items with commas",
                    KEY_COMPASS_ITEMS + " = " + DEFAULT_COMPASS_ID
            }));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
