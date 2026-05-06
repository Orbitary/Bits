/*
 * This file is part of the BiomeBattle project.
 *
 * Copyright (c) 2023-2026 BitSquidd - All Rights Reserved
 *
 * Unauthorized copying, distribution, or disclosure of this file, via any medium, is strictly prohibited.
 * Proprietary and confidential - for internal use only.
 */

package org.slf4j.impl;

import org.slf4j.ILoggerFactory;

public class StaticLoggerBinder {
    public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private final ILoggerFactory loggerFactory = new CustomLoggerFactory();

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public String getLoggerFactoryClassStr() {
        return CustomLoggerFactory.class.getName();
    }

}
