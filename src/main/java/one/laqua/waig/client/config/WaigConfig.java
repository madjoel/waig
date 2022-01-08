package one.laqua.waig.client.config;

import one.laqua.waig.client.WaigClient;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WaigConfig {

    private static final String CONFIG_FILE = "config/waig.config";

    private static final String KEY_HUD_SHOW_MODE = "hud-show-mode";

    private static HudShowMode hudShowMode = HudShowMode.ALWAYS;

    public static HudShowMode getHudShowMode() {
        return hudShowMode;
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
                        WaigClient.log(Level.ERROR, "The value '" + value + "' for config key '"
                                + KEY_HUD_SHOW_MODE + "' is invalid. Possible values are: "
                                + Arrays.toString(HudShowMode.values()).toLowerCase() + ". Falling back to default " +
                                "value '" + HudShowMode.ALWAYS.name().toLowerCase() + "'.");
                        return false;
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
                    KEY_HUD_SHOW_MODE + " = " + HudShowMode.ALWAYS.name().toLowerCase()
            }));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
