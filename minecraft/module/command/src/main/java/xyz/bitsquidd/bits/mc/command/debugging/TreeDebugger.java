/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.command.debugging;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for debugging and visualising Brigadier command trees.
 * <p>
 * Provides methods to convert a list of {@link com.mojang.brigadier.tree.LiteralCommandNode}s
 * into a readable string representation, showing the hierarchy and executable status.
 *
 * @param <T> the command source type
 *
 * @since 0.0.10
 */
public class TreeDebugger<T> {
    /**
     * Converts a collection of command nodes into a human-readable visual tree representation.
     *
     * @param nodes the root literal nodes to visualise
     *
     * @return a formatted string illustrating the node hierarchy
     *
     * @since 0.0.10
     */
    public String visualizeCommandTree(List<LiteralCommandNode<T>> nodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Command Tree:\n");

        for (LiteralCommandNode<T> node : nodes) {
            sb.append("root\n");
            visualizeNode(node, "", true, sb);
        }

        return sb.toString();
    }

    private void visualizeNode(CommandNode<T> node, String prefix, boolean isLast, StringBuilder sb) {
        String nodeName = getNodeName(node);
        String connector = isLast ? "└── " : "├── ";
        sb.append(prefix).append(connector).append(nodeName);

        if (node.getCommand() != null) {
            sb.append(" [executable]");
        }

        sb.append("\n");

        Collection<CommandNode<T>> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }

        String childPrefix = prefix + (isLast ? "    " : "│   ");

        List<CommandNode<T>> childList = new ArrayList<>(children);
        for (int i = 0; i < childList.size(); i++) {
            boolean isLastChild = (i == childList.size() - 1);
            visualizeNode(childList.get(i), childPrefix, isLastChild, sb);
        }
    }

    @SuppressWarnings("rawtypes")
    private String getNodeName(CommandNode<T> node) {
        if (node instanceof LiteralCommandNode) {
            return ((LiteralCommandNode<T>)node).getLiteral();
        } else if (node instanceof com.mojang.brigadier.tree.ArgumentCommandNode argumentCommandNode) {
            return "<" + argumentCommandNode.getType().toString() + ">";
        } else {
            return node.getName();
        }
    }

}
