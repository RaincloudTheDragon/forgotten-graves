package me.mgin.graves.mixin;

import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"))
    private void dropAll(PlayerInventory inventory) {
        // Cast 'this' to PlayerEntity since we're in a mixin
        PlayerEntity player = (PlayerEntity)(Object)this;
        
        boolean forgottenGravesEnabled = GravesConfig.resolve("graves", player.getGameProfile());
        boolean playerCanPlaceBlocks = player.canModifyAt(player.getWorld(), player.getBlockPos());

        // Do not drop the inventory or place a grave if the player is still alive.
        // This is needed for possession mods like RAT's Mischief, Requiem (Origins), etc.
        if (player.isAlive()) return;

        // Do not place graves if its disabled or the player can't place/break blocks in the area
        if (!forgottenGravesEnabled || !playerCanPlaceBlocks) {
            inventory.dropAll();
            return;
        }

        PlaceGrave.place(this.getWorld(), this.getPos(), player);
    }
}
