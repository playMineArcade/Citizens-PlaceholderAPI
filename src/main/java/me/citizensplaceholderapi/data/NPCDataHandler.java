/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi.data;

import java.util.UUID;

public class NPCDataHandler {

    private UUID uniqueId;
    private String skinPlaceholder;
    private String namePlaceholder;
    private int updateID;

    public NPCDataHandler() {
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSkinPlaceholder() {
        return this.skinPlaceholder;
    }

    public void setSkinPlaceholder(final String skinPlaceholder) {
        this.skinPlaceholder = skinPlaceholder;
    }

    public String getNamePlaceholder() {
        return this.namePlaceholder;
    }

    public void setNamePlaceholder(final String namePlaceholder) {
        this.namePlaceholder = namePlaceholder;
    }

    public int getUpdateID() {
        return this.updateID;
    }

    public void setUpdateID(final int updateID) {
        this.updateID = updateID;
    }

}
