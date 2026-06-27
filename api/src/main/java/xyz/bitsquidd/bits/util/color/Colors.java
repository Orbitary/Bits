/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.color;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Range;

import java.util.Collection;


/**
 * A utility class for performing common color operations and transformations.
 *
 * @since 0.0.10
 */
public final class Colors {
    private Colors() {}

    // CSS Hex color names
    private static final ImmutableMap<String, Integer> NAMED_COLORS = ImmutableMap.<String, Integer>builder()
      .put("red", 0xFF0000)
      .put("darkred", 0x8B0000)
      .put("firebrick", 0xB22222)
      .put("crimson", 0xDC143C)
      .put("indianred", 0xCD5C5C)
      .put("lightcoral", 0xF08080)
      .put("salmon", 0xFA8072)
      .put("darksalmon", 0xE9967A)
      .put("lightsalmon", 0xFFA07A)
      .put("orange", 0xFFA500)
      .put("darkorange", 0xFF8C00)
      .put("coral", 0xFF7F50)
      .put("tomato", 0xFF6347)
      .put("orangered", 0xFF4500)
      .put("yellow", 0xFFFF00)
      .put("gold", 0xFFD700)
      .put("lightyellow", 0xFFFFE0)
      .put("lemonchiffon", 0xFFFACD)
      .put("lightgoldenrodyellow", 0xFAFAD2)
      .put("papayawhip", 0xFFEFD5)
      .put("moccasin", 0xFFE4B5)
      .put("peachpuff", 0xFFDAB9)
      .put("palegoldenrod", 0xEEE8AA)
      .put("khaki", 0xF0E68C)
      .put("darkkhaki", 0xBDB76B)
      .put("green", 0x008000)
      .put("lime", 0x00FF00)
      .put("darkgreen", 0x006400)
      .put("forestgreen", 0x228B22)
      .put("seagreen", 0x2E8B57)
      .put("darkseagreen", 0x8FBC8F)
      .put("mediumseagreen", 0x3CB371)
      .put("lightgreen", 0x90EE90)
      .put("palegreen", 0x98FB98)
      .put("springgreen", 0x00FF7F)
      .put("mediumspringgreen", 0x00FA9A)
      .put("lawngreen", 0x7CFC00)
      .put("chartreuse", 0x7FFF00)
      .put("greenyellow", 0xADFF2F)
      .put("yellowgreen", 0x9ACD32)
      .put("olivedrab", 0x6B8E23)
      .put("olive", 0x808000)
      .put("darkolivegreen", 0x556B2F)
      .put("mediumaquamarine", 0x66CDAA)
      .put("aquamarine", 0x7FFFD4)
      .put("cyan", 0x00FFFF)
      .put("aqua", 0x00FFFF)
      .put("darkcyan", 0x008B8B)
      .put("teal", 0x008080)
      .put("lightcyan", 0xE0FFFF)
      .put("paleturquoise", 0xAFEEEE)
      .put("turquoise", 0x40E0D0)
      .put("mediumturquoise", 0x48D1CC)
      .put("darkturquoise", 0x00CED1)
      .put("blue", 0x0000FF)
      .put("darkblue", 0x00008B)
      .put("navy", 0x000080)
      .put("mediumblue", 0x0000CD)
      .put("royalblue", 0x4169E1)
      .put("cornflowerblue", 0x6495ED)
      .put("lightblue", 0xADD8E6)
      .put("lightskyblue", 0x87CEFA)
      .put("skyblue", 0x87CEEB)
      .put("deepskyblue", 0x00BFFF)
      .put("dodgerblue", 0x1E90FF)
      .put("steelblue", 0x4682B4)
      .put("cadetblue", 0x5F9EA0)
      .put("powderblue", 0xB0E0E6)
      .put("lightsteelblue", 0xB0C4DE)
      .put("slateblue", 0x6A5ACD)
      .put("darkslateblue", 0x483D8B)
      .put("mediumslateblue", 0x7B68EE)
      .put("purple", 0x800080)
      .put("darkmagenta", 0x8B008B)
      .put("darkviolet", 0x9400D3)
      .put("darkorchid", 0x9932CC)
      .put("mediumorchid", 0xBA55D3)
      .put("orchid", 0xDA70D6)
      .put("violet", 0xEE82EE)
      .put("plum", 0xDDA0DD)
      .put("thistle", 0xD8BFD8)
      .put("lavender", 0xE6E6FA)
      .put("rebeccapurple", 0x663399)
      .put("indigo", 0x4B0082)
      .put("blueviolet", 0x8A2BE2)
      .put("mediumpurple", 0x9370DB)
      .put("pink", 0xFFC0CB)
      .put("lightpink", 0xFFB6C1)
      .put("hotpink", 0xFF69B4)
      .put("deeppink", 0xFF1493)
      .put("palevioletred", 0xDB7093)
      .put("mediumvioletred", 0xC71585)
      .put("magenta", 0xFF00FF)
      .put("fuchsia", 0xFF00FF)
      .put("brown", 0xA52A2A)
      .put("maroon", 0x800000)
      .put("saddlebrown", 0x8B4513)
      .put("sienna", 0xA0522D)
      .put("chocolate", 0xD2691E)
      .put("peru", 0xCD853F)
      .put("tan", 0xD2B48C)
      .put("rosybrown", 0xBC8F8F)
      .put("sandybrown", 0xF4A460)
      .put("goldenrod", 0xDAA520)
      .put("darkgoldenrod", 0xB8860B)
      .put("burlywood", 0xDEB887)
      .put("wheat", 0xF5DEB3)
      .put("navajowhite", 0xFFDEAD)
      .put("bisque", 0xFFE4C4)
      .put("blanchedalmond", 0xFFEBCD)
      .put("cornsilk", 0xFFF8DC)
      .put("white", 0xFFFFFF)
      .put("snow", 0xFFFAFA)
      .put("honeydew", 0xF0FFF0)
      .put("mintcream", 0xF5FFFA)
      .put("azure", 0xF0FFFF)
      .put("aliceblue", 0xF0F8FF)
      .put("ghostwhite", 0xF8F8FF)
      .put("whitesmoke", 0xF5F5F5)
      .put("seashell", 0xFFF5EE)
      .put("beige", 0xF5F5DC)
      .put("oldlace", 0xFDF5E6)
      .put("floralwhite", 0xFFFAF0)
      .put("ivory", 0xFFFFF0)
      .put("antiquewhite", 0xFAEBD7)
      .put("linen", 0xFAF0E6)
      .put("lavenderblush", 0xFFF0F5)
      .put("mistyrose", 0xFFE4E1)
      .put("gray", 0x808080)
      .put("grey", 0x808080)
      .put("darkgray", 0xA9A9A9)
      .put("darkgrey", 0xA9A9A9)
      .put("lightgray", 0xD3D3D3)
      .put("lightgrey", 0xD3D3D3)
      .put("silver", 0xC0C0C0)
      .put("dimgray", 0x696969)
      .put("dimgrey", 0x696969)
      .put("slategray", 0x708090)
      .put("slategrey", 0x708090)
      .put("darkslategray", 0x2F4F4F)
      .put("darkslategrey", 0x2F4F4F)
      .put("lightslategray", 0x778899)
      .put("lightslategrey", 0x778899)
      .put("gainsboro", 0xDCDCDC)
      .put("black", 0x000000)
      .build();

    /**
     * Softens or darkens a hexadecimal color code by a given ratio.
     *
     * @param color     the base RGB color as an integer
     * @param lightness the ratio to lighten (0.0 to 1.0) or darken (-1.0 to 0.0)
     *
     * @return the transformed RGB color integer
     *
     * @since 0.0.10
     */
    public static int lightenColour(int color, @Range(from = -1, to = 1) double lightness) {
        lightness = Math.clamp(lightness, -1, 1);

        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        if (lightness >= 0) {
            red = (int)(red + ((255 - red) * lightness));
            green = (int)(green + ((255 - green) * lightness));
            blue = (int)(blue + ((255 - blue) * lightness));
        } else {
            red = (int)(red * (1 + lightness));
            green = (int)(green * (1 + lightness));
            blue = (int)(blue * (1 + lightness));
        }

        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Returns the inverted value of the specified color.
     *
     * @param color the base RGB color as an integer
     *
     * @return the inverted RGB color integer
     *
     * @since 0.0.10
     */
    public static int invertColor(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Performs a linear interpolation (average) between any number of colors.
     *
     * @param colors the colors to blend
     *
     * @return the blended color integer
     *
     * @since 0.0.10
     */
    public static int mix(Collection<Integer> colors) {
        if (colors.isEmpty()) throw new IllegalArgumentException("At least one color must be provided");

        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        for (int color : colors) {
            redSum += (color >> 16) & 0xFF;
            greenSum += (color >> 8) & 0xFF;
            blueSum += color & 0xFF;
        }

        int red = redSum / colors.size();
        int green = greenSum / colors.size();
        int blue = blueSum / colors.size();

        return (red << 16) | (green << 8) | blue;
    }

    public static int fromName(String name) {
        return NAMED_COLORS.getOrDefault(name.toLowerCase(), 0xFFFFFF);
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }


}
