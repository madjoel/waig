package one.laqua.waig.mixin;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Inventory.class)
public interface EquipmentAccessor {
    @Accessor
    EntityEquipment getEquipment();
}
