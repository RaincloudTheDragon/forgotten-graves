package me.mgin.graves.mixin;

import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow
    @Final
    private PlayerInventory inventory;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"))
    private void dropAll(PlayerInventory inventory) {
        PlayerEntity player = this.inventory.player;
        boolean forgottenGravesEnabled = GravesConfig.resolve("graves", player.getGameProfile());

        // Do not drop the inventory or place a grave if the player is still alive.
        // This is needed for possession mods like RAT's Mischief, Requiem (Origins), etc.
        if (player.isAlive()) return;

        if (!forgottenGravesEnabled) {
            this.inventory.dropAll();
            return;
        }

        PlaceGrave.place(this.world, this.getPos(), player);
    }
}
