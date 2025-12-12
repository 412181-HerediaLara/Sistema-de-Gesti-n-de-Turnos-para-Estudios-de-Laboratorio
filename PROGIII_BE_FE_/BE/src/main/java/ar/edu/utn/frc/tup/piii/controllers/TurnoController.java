package ar.edu.utn.frc.tup.piii.controllers;

import ar.edu.utn.frc.tup.piii.dtos.NewTurnoDTO;
import ar.edu.utn.frc.tup.piii.dtos.TurnoDTO;
import ar.edu.utn.frc.tup.piii.services.TurnoService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping("/turnos")
    public ResponseEntity<List<TurnoDTO>> getTurnos(
            @RequestParam(name = "estudio_id") Optional<Long> estudioId,
            @RequestParam(name = "extractor_id") Optional<Long> extractorId,
            @RequestParam(name = "paciente_id") Optional<Long> pacienteId,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @RequestParam(name = "fecha_hora") Optional<LocalDateTime> fechaHora
    ) {
        return ResponseEntity.ok(
                turnoService.obtenerTurnos(estudioId, extractorId, pacienteId, fechaHora)
        );
    }


    @PutMapping("/turnos")
    public ResponseEntity<TurnoDTO> createNewTurno(@RequestBody NewTurnoDTO newTurnoDTO) {
        return ResponseEntity.ok(turnoService.createTurno(newTurnoDTO));
    }

    @PostMapping("/turnos/programar")
    public ResponseEntity<List<TurnoDTO>> programarTurnos(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(turnoService.programarTurnos(fecha));
    }
}
