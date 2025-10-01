package com.inatel.prototipo_ia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Profissional")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "usuario_id")
public class ProfissionalEntity extends UsuarioEntity {

    private String certificados;

    private Integer experiencia;

    // Relacionamento com Chat
    @OneToMany(mappedBy = "profissional")
    private List<ChatEntity> chats;

    // Relacionamento com Tratamento
    @OneToMany(mappedBy = "profissional")
    private List<TratamentoEntity> tratamentos;
}
