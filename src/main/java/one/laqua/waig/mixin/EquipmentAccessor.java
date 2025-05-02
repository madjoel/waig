package one.laqua.waig.mixin;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInventory.class)
public interface EquipmentAccessor {

    @Accessor
    EntityEquipment getEquipment();

}
