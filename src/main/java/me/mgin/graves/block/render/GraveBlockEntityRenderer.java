package me.mgin.graves.block.render;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.utility.Skulls;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class GraveBlockEntityRenderer implements BlockEntityRenderer<GraveBlockEntity> {

    private final TextRenderer textRenderer;
    private final EntityModelLoader modelLoader;

    public GraveBlockEntityRenderer(Context context) {
        super();
        this.modelLoader = context.getLayerRenderDispatcher();
        this.textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(GraveBlockEntity graveEntity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState state = graveEntity.getCachedState();
        int blockDecayOrdinal = ((GraveBlockBase) state.getBlock()).getDecayStage().ordinal();
        Direction direction = state.get(Properties.HORIZONTAL_FACING);

        matrices.push();
        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.translate(0, 0.08f, 0);

        switch (direction) {
            case NORTH:
                // 180 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                matrices.translate(-1.2, 0.25 - (blockDecayOrdinal * 0.03), -0.99);
                break;
            case SOUTH:
                matrices.translate(0.15, 0.25 - (blockDecayOrdinal * 0.03), 0.34);
                break;
            case EAST:
                // 90 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                matrices.translate(-1.2, 0.25 - (blockDecayOrdinal * 0.03), 0.34);
                break;
            case WEST:
                // 270 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                matrices.translate(0.15, 0.25 - (blockDecayOrdinal * 0.03), -0.99);
                break;
            case UP:
            case DOWN:
                break;
        }

        // 50 deg (X)
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(0.872665f));

        Skulls.renderSkull(graveEntity, modelLoader, blockDecayOrdinal, matrices, light, vertexConsumers);

        matrices.pop();

        // Outline
        if (graveEntity.getGraveOwner() != null
            || (graveEntity.getCustomName() != null && !graveEntity.getCustomName().isEmpty())) {
            String text;

            if (graveEntity.getGraveOwner() != null) {
                text = graveEntity.getGraveOwner().getName();
            } else {
                text = graveEntity.getCustomName();

                // Handle stringified NBT
                if (text.contains("\"text\":")) {
                    try {
                        text = StringNbtReader.parse(text).getString("text");
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Handle normal text
                if (text.startsWith("\"") && text.endsWith(("\""))) {
                    text = text.substring(1, text.length() - 1);
                }
            }

            // Main Text
            matrices.push();

            int width = this.textRenderer.getWidth(text);

            float scale = (text.length() > 5 ? 0.7F : 0.44F) / width;

            switch (direction) {
                case NORTH:
                    // 180 deg (Y)
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                    matrices.translate(-1, 0, -1);
                    break;
                case SOUTH, UP, DOWN:
                    break;
                case EAST:
                    // 90 deg (Y)
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                    matrices.translate(-1, 0, 0);
                    break;
                case WEST:
                    // 270 deg (Y)
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                    matrices.translate(0, 0, -1);
                    break;
            }

            matrices.translate(0.5, 0, 0.5);
            matrices.translate(0, 0.6, 0.435);
            matrices.scale(-1, -1, 0);
            matrices.scale(scale, scale, scale);
            matrices.translate(-width / 2.0, -4.5, 0);

            this.textRenderer.draw(text, 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);
            
            matrices.pop();
        }
    }
}
