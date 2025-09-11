/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.exception.BusinessException
 *  dev.gagnon.exception.OrderBaseException
 */
package dev.gagnon.exception;

import dev.gagnon.exception.OrderBaseException;

public class BusinessException
extends OrderBaseException {
    public BusinessException(String message) {
        super(message);
    }
}

