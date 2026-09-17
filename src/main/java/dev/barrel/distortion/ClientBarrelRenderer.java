package dev.barrel.distortion;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.opengl.GL30;

/** Copies the finished level target, then replaces it with one distorted full-screen pass. */
public final class ClientBarrelRenderer {
    private static final ResourceLocation SHADER = ResourceLocation.fromNamespaceAndPath(BarrelDistortion.MOD_ID, "barrel_distortion");
    private static ShaderInstance shader;
    private static RenderTarget copyTarget;

    private ClientBarrelRenderer() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientBarrelRenderer::registerShader);
        NeoForge.EVENT_BUS.register(ClientBarrelRenderer.class);
    }

    private static void registerShader(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), SHADER, DefaultVertexFormat.POSITION_TEX), loaded -> shader = loaded);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load Barrel Distortion shader", exception);
        }
    }

    @SubscribeEvent
    public static void afterLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || !BarrelConfig.ENABLED.get() || shader == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        if (main.width <= 0 || main.height <= 0) return;
        ensureCopyTarget(main.width, main.height);

        // Sampling from a texture while rendering into it is undefined, so copy first.
        main.bindRead();
        copyTarget.bindWrite(true);
        GL30.glBlitFramebuffer(0, 0, main.width, main.height, 0, 0, copyTarget.width, copyTarget.height,
                GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);

        main.bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        shader.getUniform("DistortionStrength").set(BarrelConfig.STRENGTH.get().floatValue());
        RenderSystem.setShaderTexture(0, copyTarget.getColorTextureId());
        shader.apply();
        drawFullscreenQuad();
        shader.clear();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void ensureCopyTarget(int width, int height) {
        if (copyTarget == null) {
            copyTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            return;
        }
        if (copyTarget.width != width || copyTarget.height != height) {
            copyTarget.resize(width, height, false);
        }
    }

    private static void drawFullscreenQuad() {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
        buffer.addVertex(1.0F, -1.0F, 0.0F).setUv(1.0F, 0.0F);
        buffer.addVertex(1.0F, 1.0F, 0.0F).setUv(1.0F, 1.0F);
        buffer.addVertex(-1.0F, 1.0F, 0.0F).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
}
