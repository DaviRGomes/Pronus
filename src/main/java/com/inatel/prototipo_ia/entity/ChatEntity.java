package com.inatel.prototipo_ia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Chat")
@Getter @Setter
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer duracao;

    @Column(columnDefinition = "TEXT")
    private String conversa;

    @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL)
    private RelatorioEntity relatorio;

    @ManyToOne @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    // --- ALTERADO DE PROFISSIONAL PARA ESPECIALISTA ---
    @ManyToOne @JoinColumn(name = "especialista_id", nullable = false)
    private EspecialistaEntity especialista;
}
