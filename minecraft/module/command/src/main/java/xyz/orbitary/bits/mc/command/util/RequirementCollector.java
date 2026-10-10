/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.command.util;

import xyz.orbitary.bits.mc.command.BitsCommandManager;
import xyz.orbitary.bits.mc.command.annotation.Permission;
import xyz.orbitary.bits.mc.command.annotation.Requirement;
import xyz.orbitary.bits.mc.command.requirement.BitsCommandRequirement;
import xyz.orbitary.bits.mc.command.requirement.impl.PermissionRequirement;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;


/**
 * Gathers the requirements declared through {@link Permission} and {@link Requirement} annotations on a command class or method.
 *
 * @since 0.1.0
 */
final class RequirementCollector {
    private RequirementCollector() {}

    static List<BitsCommandRequirement> collect(AnnotatedElement element, xyz.orbitary.bits.mc.permission.Permission corePermission) {
        List<BitsCommandRequirement> requirements = new ArrayList<>();

        // Gather permission strings and convert them to requirements.
        Permission permissionAnnotation = element.getAnnotation(Permission.class);
        if (permissionAnnotation != null) {
            for (String appended : permissionAnnotation.value()) {
                requirements.add(PermissionRequirement.of(xyz.orbitary.bits.mc.permission.Permission.of(corePermission + "." + appended)));
            }
        }

        // Gather requirement instances
        Requirement requirementAnnotation = element.getAnnotation(Requirement.class);
        if (requirementAnnotation != null) {
            for (Class<? extends BitsCommandRequirement> clazz : requirementAnnotation.value()) {
                requirements.add(BitsCommandManager.get().getRequirementRegistry().getRequirement(clazz));
            }
        }

        return requirements;
    }

}
