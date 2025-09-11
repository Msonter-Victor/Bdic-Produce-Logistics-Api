/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.exception.ResourceNotFoundException
 */
package dev.gagnon.exception;

import dev.gagnon.exception.OrderBaseException;

public class ResourceNotFoundException
extends OrderBaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

