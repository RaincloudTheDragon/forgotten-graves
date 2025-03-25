package me.mgin.graves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity")
public abstract class BlockBreakingKineticBlockEntityMixin {
    @Inject(method = "isBreakable", at = @At("HEAD"), cancellable = true)
    private void stopDrillBreakingGraves(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}