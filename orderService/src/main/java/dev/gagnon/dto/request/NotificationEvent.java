/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.dto.request.NotificationEvent
 *  lombok.Generated
 */
package dev.gagnon.dto.request;

import lombok.Generated;

public class NotificationEvent {
    private String title;
    private String message;
    private String email;
    private String type;

    @Generated
    public void setTitle(String title) {
        this.title = title;
    }

    @Generated
    public void setMessage(String message) {
        this.message = message;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setType(String type) {
        this.type = type;
    }

    @Generated
    public String getTitle() {
        return this.title;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public NotificationEvent(String title, String message, String email, String type) {
        this.title = title;
        this.message = message;
        this.email = email;
        this.type = type;
    }

    @Generated
    public NotificationEvent() {
    }
}

