package one.laqua.waig.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import one.laqua.waig.client.config.WaigConfig;
import one.laqua.waig.mixin.BossHealthOverlayAccessor;
import one.laqua.waig.mixin.EquipmentAccessor;
import org.jspecify.annotations.NonNull;

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
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        if (!visible) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.options.hideGui) {
            return;
        }

        LocalPlayer p = client.player;
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
                                p.getInventory().getNonEquipmentItems().stream(),
                                Inventory.EQUIPMENT_SLOT_MAPPING.values().stream()
                                        .map(slot -> ((EquipmentAccessor) p.getInventory()).getEquipment().get(slot))
                        )
                        .map(ItemStack::getItem)
                        .map(Item::getId)
                        .collect(Collectors.toSet());

                compassIds.retainAll(inventory);
                boolean containsCompass = !compassIds.isEmpty();

                if (!containsCompass) {
                    return;
                }
            }
            case HAND -> {
                boolean holdsCompass = WaigConfig.getCompassItems().contains(Item.getId(p.getOffhandItem().getItem()))
                                    || WaigConfig.getCompassItems().contains(Item.getId(p.getMainHandItem().getItem()));

                if (!holdsCompass) {
                    return;
                }
            }
        }

        float modYaw = (p.yHeadRot % 360.0f + 360.0f) % 360.0f;

        String renderText = displayedText(modYaw);

        int screenWidth = client.getWindow().getGuiScaledWidth();

        Font textRenderer = client.gui.getFont();
        int textWidthInPixels = textRenderer.width(renderText);

        int posX = screenWidth / 2 - textWidthInPixels / 2 - 2; // center on the screen

        int bossBarCount = ((BossHealthOverlayAccessor) client.gui.getBossOverlay()).getEvents().size();
        int posY = 3 + bossBarCount * 19;

        graphics.text(textRenderer, renderText, posX, posY, 0xFFFFFFFF);
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
