package ar.edu.utn.frc.tup.piii.services;

import ar.edu.utn.frc.tup.piii.dtos.NewTurnoDTO;
import ar.edu.utn.frc.tup.piii.dtos.TurnoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface TurnoService {

    TurnoDTO createTurno(NewTurnoDTO newTurnoDTO);
    List<TurnoDTO> getAllTurnosByExtractor(Long extractorId);
    List<TurnoDTO> getAllTurnosByPaciente(Long pacienteId);
    List<TurnoDTO> getAllTurnosByEstudio(Long estudioId);
    List<TurnoDTO> getAllTurnosByFecha(LocalDate fecha);
    List<TurnoDTO> getAllTurnos();
    List<TurnoDTO> programarTurnos(LocalDate fecha);
    List<TurnoDTO> obtenerTurnos(Optional<Long> estudioId, Optional<Long>extractorId, Optional<Long>pacienteId, Optional<LocalDate> fecha);
}
