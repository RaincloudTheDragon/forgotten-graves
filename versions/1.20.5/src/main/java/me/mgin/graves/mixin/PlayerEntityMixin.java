package me.mgin.graves.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void onDropInventory(CallbackInfo ci) {
        // TODO: Implement grave creation logic
    }
} 