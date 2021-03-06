package one.laqua.waig.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import one.laqua.waig.client.config.WaigConfig;
import one.laqua.waig.mixin.BossBarHudAccessor;

@Environment(EnvType.CLIENT)
public class CompassHud {

    private static final String compass_text_simple = "S . . . . . . . SW . . . . . . . W . . . . . . . NW . . . . . . . N . . . . . . . NE . . . . . . . E . . . . . . . SE . . . . . . . ";
    private static final String compass_text_triple = compass_text_simple + compass_text_simple + compass_text_simple;
    private static final int oneSideLength = 20;

    private static final ItemStack compass_stack = Items.COMPASS.getDefaultStack();

    private static boolean visible = true;

    public static void onHudRender(MatrixStack matrices, float v) {
        if (!visible) {
            return;
        }

        // get the player
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity p = client.player;
        if (p == null) {
            return;
        }

        // check the configuration
        switch (WaigConfig.getHudShowMode()) {
            case ALWAYS -> {
                // nothing to check in this case, just continue
            }
            case INVENTORY -> {
                if (!p.getInventory().contains(compass_stack)) {
                    return;
                }
            }
            case HAND -> {
                if (!p.isHolding(Items.COMPASS)) {
                    return;
                }
            }
        }

        float modYaw = (p.headYaw % 360.0f + 360.0f) % 360.0f;

        String renderText = displayedText(modYaw);

        int screenWidth = client.getWindow().getScaledWidth();
        int textWidthInPixels = client.textRenderer.getWidth(renderText);

        int posX = screenWidth / 2 - textWidthInPixels / 2 - 2; // center on the screen

        int bossBarCount = ((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().size();
        int posY = 3 + bossBarCount * 19;

        client.textRenderer.drawWithShadow(matrices, renderText, posX, posY, 0xFFFFFF);
    }

    private static String displayedText(float yaw) {
        int textAnchor = Math.round((yaw / 360.0f) * compass_text_simple.length()) + compass_text_simple.length();
        return compass_text_triple.substring(textAnchor - oneSideLength, textAnchor + oneSideLength);
    }

    public static void setVisible(boolean visible) {
        CompassHud.visible = visible;
    }

    public static void toggleVisibility() {
        setVisible(!visible);
    }
}
