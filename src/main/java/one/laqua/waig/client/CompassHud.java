package one.laqua.waig.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import one.laqua.waig.client.config.WaigConfig;
import one.laqua.waig.mixin.BossBarHudAccessor;

import java.util.Set;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class CompassHud {

    private static final String compass_text_simple = "S . . . . . . . SW . . . . . . . W . . . . . . . NW . . . . . . . N . . . . . . . NE . . . . . . . E . . . . . . . SE . . . . . . . ";
    private static final String compass_text_triple = compass_text_simple + compass_text_simple + compass_text_simple;
    private static final int oneSideLength = 20;

    private static final Set<ItemStack> compass_stacks = WaigConfig.getCompassItems().stream()
            .map(Item::getDefaultStack)
            .collect(Collectors.toSet());

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
                boolean containsCompass = compass_stacks.stream()
                        .anyMatch(itemStack -> p.getInventory().contains(itemStack));

                if (!containsCompass) {
                    return;
                }
            }
            case HAND -> {
                boolean holdsCompass = WaigConfig.getCompassItems().contains(p.getOffHandStack().getItem())
                                    || WaigConfig.getCompassItems().contains(p.getMainHandStack().getItem());

                if (!holdsCompass) {
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
