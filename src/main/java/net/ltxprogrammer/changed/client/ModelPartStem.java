package net.ltxprogrammer.changed.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

public class ModelPartStem {
    public final ImmutableList<ModelPart> stem;

    private static @Nullable ModelPart findParentOfLeaf(ModelPart root, ModelPart leaf) {
        if (root.children.containsValue(leaf))
            return root;

        for (var child : root.children.values()) {
            var attempt = findParentOfLeaf(child, leaf);
            if (attempt != null)
                return attempt;
        }

        return null;
    }

    public ModelPartStem(ImmutableList<ModelPart> stem) {
        this.stem = stem;
    }

    public ModelPartStem(ModelPart single) {
        this.stem = ImmutableList.<ModelPart>builder().add(single).build();
    }

    public ModelPartStem(ModelPartStem stem, ModelPart immediateChild) {
        ImmutableList.Builder<ModelPart> builder = ImmutableList.<ModelPart>builder().addAll(stem.stem);
        builder.add(immediateChild);
        this.stem = builder.build();
    }

    public ModelPartStem(ModelPart immediateParent, ModelPartStem stem) {
        ImmutableList.Builder<ModelPart> builder = ImmutableList.<ModelPart>builder().add(immediateParent);
        builder.addAll(stem.stem);
        this.stem = builder.build();
    }

    public ModelPartStem(ModelPart root, ModelPart leaf) {
        ImmutableList.Builder<ModelPart> builder = ImmutableList.<ModelPart>builder().add(root);
        ArrayList<ModelPart> parentList = new ArrayList<>();
        while (leaf != root) {
            parentList.add(leaf);
            leaf = findParentOfLeaf(root, leaf);
            if (leaf == null)
                throw new IllegalStateException("Stem part does not exist within root part");
        }

        Collections.reverse(parentList);
        builder.addAll(parentList);

        this.stem = builder.build();
    }

    public void translateAndRotate(PoseStack poseStack) {
        stem.forEach(part -> {
            part.translateAndRotate(poseStack);
        });
    }

    public ModelPartStem withNext(ModelPart child) {
        return new ModelPartStem(this, child);
    }

    public ModelPartStem withParent(ModelPart parent) {
        return new ModelPartStem(parent, this);
    }

    public ModelPart getRoot() {
        return stem.get(0);
    }

    public ModelPart getLeaf() {
        return stem.get(stem.size() - 1);
    }
}
