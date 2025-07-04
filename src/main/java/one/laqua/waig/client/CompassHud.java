package one.laqua.waig.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import one.laqua.waig.client.config.WaigConfig;
import one.laqua.waig.mixin.BossBarHudAccessor;
import one.laqua.waig.mixin.EquipmentAccessor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class CompassHud implements HudElement {

    private static final String compass_text_simple = "S . . . . . . . SW . . . . . . . W . . . . . . . NW . . . . . . . N . . . . . . . NE . . . . . . . E . . . . . . . SE . . . . . . . ";
    private static final String compass_text_triple = compass_text_simple + compass_text_simple + compass_text_simple;
    private static final int oneSideLength = 20;

    private static final Set<Integer> compass_stacks = WaigConfig.getCompassItems();

    private static boolean visible = true;

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!visible) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) {
            return;
        }

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
                Set<Integer> compassIds = new HashSet<>(compass_stacks);
                Set<Integer> inventory = Stream.concat(
                                p.getInventory().getMainStacks().stream(),
                                PlayerInventory.EQUIPMENT_SLOTS.values().stream()
                                        .map(slot -> ((EquipmentAccessor) p.getInventory()).getEquipment().get(slot))
                        )
                        .map(ItemStack::getItem)
                        .map(Item::getRawId)
                        .collect(Collectors.toSet());

                compassIds.retainAll(inventory);
                boolean containsCompass = !compassIds.isEmpty();

                if (!containsCompass) {
                    return;
                }
            }
            case HAND -> {
                boolean holdsCompass = WaigConfig.getCompassItems().contains(Item.getRawId(p.getOffHandStack().getItem()))
                                    || WaigConfig.getCompassItems().contains(Item.getRawId(p.getMainHandStack().getItem()));

                if (!holdsCompass) {
                    return;
                }
            }
        }

        float modYaw = (p.headYaw % 360.0f + 360.0f) % 360.0f;

        String renderText = displayedText(modYaw);

        int screenWidth = client.getWindow().getScaledWidth();

        TextRenderer textRenderer = client.inGameHud.getTextRenderer();
        int textWidthInPixels = textRenderer.getWidth(renderText);

        int posX = screenWidth / 2 - textWidthInPixels / 2 - 2; // center on the screen

        int bossBarCount = ((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().size();
        int posY = 3 + bossBarCount * 19;

        drawContext.drawTextWithShadow(textRenderer, renderText, posX, posY, 0xFFFFFFFF);
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
