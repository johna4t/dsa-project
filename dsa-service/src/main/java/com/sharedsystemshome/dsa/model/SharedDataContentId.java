package com.sharedsystemshome.dsa.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class SharedDataContentId implements Serializable {

    // Getters and Setters
    private Long dfId;
    private Long dcdId;

    // Default constructor
    public SharedDataContentId() {}

    // Constructor with all fields
    public SharedDataContentId(Long dfId, Long dcdId) {
        this.dfId = dfId;
        this.dcdId = dcdId;
    }

    // Equals and HashCode (required for composite keys)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SharedDataContentId that = (SharedDataContentId) obj;
        return Objects.equals(dfId, that.dfId) && Objects.equals(dcdId, that.dcdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dfId, dcdId);
    }
}

