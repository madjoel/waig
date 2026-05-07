package one.laqua.waig.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import one.laqua.waig.client.config.WaigConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class WaigClient implements ClientModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "waig";
    public static final String MOD_NAME = "Where Am I Going";

    public static final Identifier hudIdentifier = Identifier.parse(MOD_ID + ":hud");

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");

        // read config file located in ".minecraft/config/waig.config"
        WaigConfig.readConfigFile();

        // register render callback
        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, hudIdentifier, new CompassHud());

        // add key binding to toggle visibility of the hud
        KeyMapping binding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.waig.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KeyMapping.Category.MISC
        ));

        // add response to the keybinding
        ClientTickEvents.END_CLIENT_TICK.register(_ -> {
            while (binding.consumeClick()) {
                CompassHud.toggleVisibility();
            }
        });
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}
