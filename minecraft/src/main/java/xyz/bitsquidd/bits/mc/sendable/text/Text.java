/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.mc.sendable.text;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import xyz.bitsquidd.bits.config.BitsMinecraft;
import xyz.bitsquidd.bits.mc.sendable.Sendable;
import xyz.bitsquidd.bits.mc.sendable.text.decorator.ITextDecorator;
import xyz.bitsquidd.bits.mc.sendable.text.decorator.impl.BlankDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A highly customizable {@link Sendable} implementation for text-based messages.
 * <p>
 * This class wraps an Adventure {@link Component} and provides a mechanism for applying
 * multiple {@link ITextDecorator}s. Decorators are processed in a specific sequence:
 * <ol>
 *   <li>Internal pre-default decorators</li>
 *   <li>User-defined decorators (passed via constructor or {@link #decorate})</li>
 *   <li>Internal post-default decorators (e.g., {@link BlankDecorator} for tag cleanup)</li>
 * </ol>
 *
 * @since 0.0.10
 */
public final class Text implements Sendable {
    private final Component component;
    private final List<ITextDecorator> decorators = new ArrayList<>();
    private final List<Text> appendedText = new ArrayList<>();

    private static final List<ITextDecorator> PRE_DEFAULT_DECORATORS = List.of(
      // These will always be applied first.
    );
    private static final List<ITextDecorator> POST_DEFAULT_DECORATORS = List.of(
      // These will always be applied last.
      new BlankDecorator()
    );

    private Text(Component component, List<ITextDecorator> decorators) {
        this.component = component;
        this.decorators.addAll(decorators);
    }

    private Text(Component component, ITextDecorator decorator) {
        this(component, List.of(decorator));
    }

    private Text(Component component) {
        this(component, List.of());
    }


    public static Text of(Component component) {
        return new Text(component);
    }

    public static Text of(String plainText) {
        return new Text(Component.text(plainText));
    }


    /**
     * Creates a new text instance with the specified decorators added.
     *
     * @param decorators the list of decorators to apply
     *
     * @since 0.0.10
     */
    public Text decorate(List<ITextDecorator> decorators) {
        return new Text(component, decorators);
    }

    /**
     * Creates a new text instance with the specified decorators added.
     *
     * @param decorators the decorators to apply
     *
     * @since 0.0.10
     */
    public Text decorate(ITextDecorator... decorators) {
        return new Text(component, new ArrayList<>(List.of(decorators)));
    }


    /**
     * Appends another text instance to the end of this one.
     *
     * @param text the text to append
     *
     * @return this text instance for fluent chaining
     *
     * @since 0.0.10
     */
    public Text append(Text text) {
        this.appendedText.add(text);
        return this;
    }


    /**
     * Dispatches the fully decorated component to the target audience.
     *
     * @param audience the target audience
     *
     * @since 0.0.10
     */
    @Override
    public void send(Audience audience) {
        audience.forEachAudience(a -> a.sendMessage(getComponent(a)));
    }

    /**
     * Builds the final {@link Component} for a specific audience,
     * applying all decorators and locale-sensitive transformations.
     *
     * @param audience the audience to build for
     *
     * @since 0.0.10
     */
    public Component getComponent(Audience audience) {
        Component returnComponent = component;

        List<ITextDecorator> componentDecorators = new ArrayList<>(PRE_DEFAULT_DECORATORS);
        componentDecorators.addAll(decorators);
        componentDecorators.addAll(POST_DEFAULT_DECORATORS);

        Locale locale = BitsMinecraft.get().getLocale(audience);
        for (ITextDecorator decorator : componentDecorators) {
            returnComponent = decorator.format(returnComponent, locale);
        }

        for (Text appendedText : appendedText) {
            returnComponent = returnComponent.append(appendedText.getComponent(audience));
        }

        return returnComponent;
    }

    /**
     * Returns a representation of this text as a {@link Component} using a default locale.
     *
     * @since 0.0.10
     */
    public Component toComponent() {
        return getComponent(Audience.empty());
    }

}