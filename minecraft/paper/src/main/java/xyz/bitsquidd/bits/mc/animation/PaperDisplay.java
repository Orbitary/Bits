/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.animation;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;


public class PaperDisplay implements Animatable {
    private final Display display;

    private PaperDisplay(Display display) {
        this.display = display;
    }

    public static PaperDisplay of(Display display) {
        return new PaperDisplay(display);
    }


    @Override
    public void applyPose(AnimationPose pose) {
        display.setTransformation(
          new Transformation(
            pose.translation(),
            pose.rotation(),
            pose.scale(),
            new Quaternionf() // No right rotation, very few situations require this.
          )
        );
    }

}
