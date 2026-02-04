package me.mgin.graves.mixin;

import net.minecraft.server.command.SetBlockCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetBlockCommand.class)
public class SetBlockCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void onExecute(CallbackInfoReturnable<Integer> cir) {
        // TODO: Implement grave protection logic
    }
} 