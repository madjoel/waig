package one.laqua.waig.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class WaigConfig {

    private static final String CONFIG_FILE = "config/waig.config";

    private static final String KEY_ONLY_SHOW_HUD_WHEN_COMPASS_IN_HAND = "only-show-hud-when-compass-in-hand";

    private static boolean onlyShowHudWhenCompassInHand = false;

    public static boolean onlyShowHudWhenCompassInHand() {
        return onlyShowHudWhenCompassInHand;
    }

    public static void setOnlyShowHudWhenCompassInHand(boolean onlyShowHudWhenCompassInHand) {
        WaigConfig.onlyShowHudWhenCompassInHand = onlyShowHudWhenCompassInHand;
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

                if (key.equals(KEY_ONLY_SHOW_HUD_WHEN_COMPASS_IN_HAND)) {
                    String value = pieces[1].strip().toLowerCase();
                    setOnlyShowHudWhenCompassInHand(Boolean.parseBoolean(value));
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
                    KEY_ONLY_SHOW_HUD_WHEN_COMPASS_IN_HAND + " = false"
            }));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
