package ar.edu.utn.frc.tup.piii.entities;

import ar.edu.utn.frc.tup.piii.dtos.TurnoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PacienteEntity paciente;

    @ManyToOne
    private ExtractorEntity extractor;

    @ManyToOne
    private EstudioEntity estudio;

    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    private TurnoStatus status;

    private String observaciones;
}
