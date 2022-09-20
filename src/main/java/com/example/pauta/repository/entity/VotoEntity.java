package com.example.pauta.repository.entity;

import com.example.pauta.repository.entity.enums.VotoOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Data
@EqualsAndHashCode
@Entity(name = "user")
public class VotoEntity {

    @EmbeddedId
    private PautaUserKey votoKey;

    @Column
    @Enumerated(EnumType.STRING)
    private VotoOption voto;

    @Data
    @EqualsAndHashCode
    @Embeddable
    public static class PautaUserKey implements Serializable {

        private Long pautaId;
        private Long userId;
    }
}
