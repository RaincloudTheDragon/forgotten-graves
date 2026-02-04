package me.mgin.graves.mixin;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBreakingKineticBlockEntity.class)
public class BlockBreakingKineticBlockEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        // TODO: Implement grave protection from Create mod
    }
} 