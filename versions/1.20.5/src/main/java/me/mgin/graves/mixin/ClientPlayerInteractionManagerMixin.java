package me.mgin.graves.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
    private void onBreakBlock(CallbackInfoReturnable<Boolean> cir) {
        // TODO: Implement grave breaking logic
    }
} 