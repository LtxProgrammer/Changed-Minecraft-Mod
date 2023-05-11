package net.ltxprogrammer.changed.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class LatexHumanoidSplitArmorLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>,
        A extends LatexHumanoidArmorModel<T, ?>, B extends LatexHumanoidArmorModel<T, ?>> extends LatexHumanoidArmorLayer<T, M, A> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    final B innerModelOther;
    final B outerModelOther;
    final Predicate<EquipmentSlot> useOther;
    final Predicate<EquipmentSlot> useInner;

    public LatexHumanoidSplitArmorLayer(RenderLayerParent<T, M> parentModel, A innerModel, A outerModel, B innerModelOther, B outerModelOther,
                                        Predicate<EquipmentSlot> useOther, Predicate<EquipmentSlot> useInner) {
        super(parentModel, innerModel, outerModel);
        this.innerModelOther = innerModelOther;
        this.outerModelOther = outerModelOther;
        this.useOther = useOther;
        this.useInner = useInner;
    }

    public void render(PoseStack pose, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limgSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.innerModelOther.prepareMobModel(entity, limbSwing, limgSwingAmount, partialTicks);
        this.innerModelOther.setupAnim(entity, limbSwing, limgSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.outerModelOther.prepareMobModel(entity, limbSwing, limgSwingAmount, partialTicks);
        this.outerModelOther.setupAnim(entity, limbSwing, limgSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.render(pose, buffers, packedLight, entity, limbSwing, limgSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    LatexHumanoidArmorModel<T, ?> getArmorModel(EquipmentSlot slot) {
        return useOther.test(slot) ? (this.useInner.test(slot) ? this.innerModelOther : this.outerModelOther) : super.getArmorModel(slot);
    }
}