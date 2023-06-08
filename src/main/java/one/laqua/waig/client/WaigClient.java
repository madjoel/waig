package one.laqua.waig.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");

        // read config file located in ".minecraft/config/waig.config"
        WaigConfig.readConfigFile();

        // register render callback
        HudRenderCallback.EVENT.register(new CompassHud());

        // add key binding to toggle visibility of the hud
        KeyBinding binding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.waig.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KeyBinding.UI_CATEGORY
        ));

        // add response to the keybinding
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (binding.wasPressed()) {
                CompassHud.toggleVisibility();
            }
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}
