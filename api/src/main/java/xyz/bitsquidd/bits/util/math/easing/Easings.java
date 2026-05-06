/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.math.easing;

public final class Easings {
    private Easings() {}

    public static final Easing LINEAR = new Easing.Linear();

    public static final Easing IN = new Easing.In();
    public static final Easing OUT = new Easing.Out();
    public static final Easing IN_OUT = new Easing.InOut();

    public static final Easing IN_SIN = new Easing.InSin();
    public static final Easing OUT_SIN = new Easing.OutSin();
    public static final Easing IN_OUT_SIN = new Easing.InOutSin();

    public static final Easing IN_EXPO = new Easing.InExpo();
    public static final Easing OUT_EXPO = new Easing.OutExpo();
    public static final Easing IN_OUT_EXPO = new Easing.InOutExpo();

    public static final Easing IN_BACK = new Easing.InBack();
    public static final Easing OUT_BACK = new Easing.OutBack();
    public static final Easing IN_OUT_BACK = new Easing.InOutBack();

    public static final Easing IN_CIRC = new Easing.InCirc();
    public static final Easing OUT_CIRC = new Easing.OutCirc();
    public static final Easing IN_OUT_CIRC = new Easing.InOutCirc();

    public static final Easing IN_ELASTIC = new Easing.InElastic();
    public static final Easing OUT_ELASTIC = new Easing.OutElastic();
    public static final Easing IN_OUT_ELASTIC = new Easing.InOutElastic();

    public static final Easing IN_BOUNCE = new Easing.InBounce();
    public static final Easing OUT_BOUNCE = new Easing.OutBounce();
    public static final Easing IN_OUT_BOUNCE = new Easing.InOutBounce();


}
