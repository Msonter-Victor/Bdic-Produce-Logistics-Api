/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.exception.UploadMediaFailedException
 */
package dev.gagnon.exception;

import dev.gagnon.exception.OrderBaseException;

public class UploadMediaFailedException
extends OrderBaseException {
    public UploadMediaFailedException(String message) {
        super(message);
    }
}

