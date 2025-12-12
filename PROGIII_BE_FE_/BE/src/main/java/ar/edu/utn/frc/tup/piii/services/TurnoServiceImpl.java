package ar.edu.utn.frc.tup.piii.services;

import ar.edu.utn.frc.tup.piii.dtos.*;
import ar.edu.utn.frc.tup.piii.entities.*;
import ar.edu.utn.frc.tup.piii.exceptions.BusinessException;
import ar.edu.utn.frc.tup.piii.repositories.DisponibilidadTurnosRepository;
import ar.edu.utn.frc.tup.piii.repositories.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;

    private final ModelMapper modelMapper;

    private final ExtractorService extractorService;

    private final PacienteService pacienteService;

    private final EstudioService estudioService;

    private final DisponibilidadTurnosRepository disponibilidadTurnosRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public TurnoDTO createTurno(NewTurnoDTO newTurnoDTO) {
        PacienteDTO paciente = pacienteService.getPacienteById(newTurnoDTO.getPacienteId());
        ExtractorDTO extractor = extractorService.getExtractorById(newTurnoDTO.getExtractorId());
        EstudioDTO estudio = estudioService.getEstudioById(newTurnoDTO.getEstudioId());

        if (paciente == null || extractor == null || estudio == null) {
            throw new BusinessException(
                    "Extractor, Paciente o Estudio no existen",
                    HttpStatus.BAD_REQUEST);
        }

        LocalDateTime fechaHora = newTurnoDTO.getFechaHora();
        LocalDate dia = fechaHora.toLocalDate();

        boolean pacienteYaTieneTurnoEseDia =
                turnoRepository.findTurnoEntitiesByPacienteId(paciente.getId())
                        .stream()
                        .filter(t -> t.getStatus() == TurnoStatus.OCUPADO) // Solo turnos ocupados
                        .anyMatch(t -> t.getFechaHora().toLocalDate().equals(dia));

        if (pacienteYaTieneTurnoEseDia) {
            throw new BusinessException(
                    "El paciente ya tiene un turno agendado para el día indicado",
                    HttpStatus.BAD_REQUEST);
        }

        boolean extractorOcupado =
                turnoRepository.findTurnoEntitiesByFechaHoraAndExtractorId(fechaHora, extractor.getId())
                        .stream()
                        .anyMatch(t -> t.getStatus() == TurnoStatus.OCUPADO);

        if (extractorOcupado) {
            throw new BusinessException(
                    "Ya existe un turno para el extractor en esa fecha y hora",
                    HttpStatus.BAD_REQUEST);
        }

        TurnoEntity turnoDisponible =
                turnoRepository.findTurnoEntitiesByFechaHoraAndExtractorId(fechaHora, extractor.getId())
                        .stream()
                        .filter(t -> t.getStatus() == TurnoStatus.DISPONIBLE)
                        .findFirst()
                        .orElseThrow(() ->
                                new BusinessException(
                                        "No hay turnos disponibles en ese horario para ese extractor",
                                        HttpStatus.BAD_REQUEST
                                )
                        );

        turnoDisponible.setPaciente(modelMapper.map(paciente, PacienteEntity.class));
        turnoDisponible.setEstudio(modelMapper.map(estudio, EstudioEntity.class));
        turnoDisponible.setObservaciones(newTurnoDTO.getObservaciones());
        turnoDisponible.setStatus(TurnoStatus.OCUPADO);

        turnoRepository.save(turnoDisponible);

        return modelMapper.map(turnoDisponible, TurnoDTO.class);
    }


    @Override
    public List<TurnoDTO> getAllTurnosByExtractor(Long extractorId) {
        List<TurnoEntity> turnoEntities = turnoRepository.findTurnoEntitiesByExtractorId(extractorId);
        return modelMapper.map(turnoEntities, new TypeToken<List<TurnoDTO>>() {}.getType());
    }

    @Override
    public List<TurnoDTO> getAllTurnosByPaciente(Long pacienteId) {
        List<TurnoEntity> turnoEntities = turnoRepository.findTurnoEntitiesByPacienteId(pacienteId);
        return modelMapper.map(turnoEntities, new TypeToken<List<TurnoDTO>>() {}.getType());
    }

    @Override
    public List<TurnoDTO> getAllTurnosByEstudio(Long estudioId) {
        List<TurnoEntity> turnoEntities = turnoRepository.findTurnoEntitiesByEstudioId(estudioId);
        return modelMapper.map(turnoEntities, new TypeToken<List<TurnoDTO>>() {}.getType());
    }

    @Override
    public List<TurnoDTO> getAllTurnosByFecha(LocalDate fecha) {
        List<TurnoEntity> turnoEntities = turnoRepository.findTurnoEntitiesByFecha(fecha);
        return modelMapper.map(turnoEntities, new TypeToken<List<TurnoDTO>>() {}.getType());
    }

    @Override
    public List<TurnoDTO> getAllTurnos() {
        List<TurnoEntity> turnoEntities = turnoRepository.findAll();
        return modelMapper.map(turnoEntities, new TypeToken<List<TurnoDTO>>() {}.getType());
    }

    /*
        HELP:
        * Para componer LocalDateTime a partir de LocalDate y LocalTime, se puede usar el metodo atDate() de LocalTime.
          Ejemplo: LocalDateTime fechaHora = disponibilidad.getHoraInicio().atDate(fecha);
        * Se puede obtener la lista de intervalos de turnos desde la base de datos usando el repositorio DisponibilidadTurnosRepository.
          Ejemplo: List<DisponibilidadTurnosEntity> disponibilidades = disponibilidadTurnosRepository.findAll();
     */
    @Override
    public List<TurnoDTO> programarTurnos(LocalDate fecha) {
        // 1️⃣ Verificar si ya hay turnos programados
        List<TurnoEntity> yaProgramados = turnoRepository.findTurnoEntitiesByFecha(fecha);
        if (!yaProgramados.isEmpty()) {
            throw new BusinessException(
                    "Los turnos ya están programados para la fecha indicada",
                    HttpStatus.BAD_REQUEST);
        }

        // 2️⃣ Obtener extractores
        List<ExtractorDTO> extractores = extractorService.getAllExtractores();
        if (extractores.isEmpty()) {
            throw new BusinessException(
                    "No existen extractores para programar turnos",
                    HttpStatus.BAD_REQUEST);
        }

        // 3️⃣ Obtener configuración de disponibilidad desde BD
        List<DisponibilidadTurnosEntity> disponibilidades =
                disponibilidadTurnosRepository.findAll();

        // Si no hay configuración, usar valores por defecto
        if (disponibilidades.isEmpty()) {
            disponibilidades = List.of(
                    DisponibilidadTurnosEntity.builder()
                            .horaInicio(LocalTime.of(7, 0))
                            .duracionMinutos(15)
                            .build()
            );
        }

        List<TurnoEntity> turnosAGuardar = new ArrayList<>();

        // 4️⃣ Crear turnos para cada extractor
        for (ExtractorDTO extractor : extractores) {
            LocalTime inicio = LocalTime.of(7, 0);
            LocalTime fin = LocalTime.of(10, 45);

            // Usar la duración desde la configuración
            int duracionMinutos = disponibilidades.get(0).getDuracionMinutos();

            LocalTime horaActual = inicio;
            while (!horaActual.isAfter(fin)) {
                TurnoEntity turno = new TurnoEntity();
                turno.setExtractor(modelMapper.map(extractor, ExtractorEntity.class));
                turno.setPaciente(null);
                turno.setEstudio(null);
                turno.setObservaciones(null);
                turno.setStatus(TurnoStatus.DISPONIBLE);
                turno.setFechaHora(LocalDateTime.of(fecha, horaActual));

                turnosAGuardar.add(turno);
                horaActual = horaActual.plusMinutes(duracionMinutos);
            }
        }

        turnoRepository.saveAll(turnosAGuardar);

        return turnosAGuardar.stream()
                .map(t -> modelMapper.map(t, TurnoDTO.class))
                .toList();
    }
    @Override
    public List<TurnoDTO> obtenerTurnos(
            Optional<Long> estudioId,
            Optional<Long> extractorId,
            Optional<Long> pacienteId,
            Optional<LocalDateTime> fechaHora ) {
        // 1️⃣ Contar cuántos filtros vienen
        int filtros = 0;
        if (estudioId.isPresent()) filtros++;
        if (extractorId.isPresent()) filtros++;
        if (pacienteId.isPresent()) filtros++;
        if (fechaHora.isPresent()) filtros++;

        if (filtros > 1) {
            throw new BusinessException(
                    "Solo se puede filtrar por un campo a la vez",
                    HttpStatus.BAD_REQUEST
            );
        }

        List<TurnoEntity> turnos;

        // 2️⃣ Buscar según el filtro enviado
        if (estudioId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByEstudioId(estudioId.get());
        } else if (extractorId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByExtractorId(extractorId.get());
        } else if (pacienteId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByPacienteId(pacienteId.get());
        } else if (fechaHora.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByFechaHora(fechaHora.get());
        } else {
            turnos = turnoRepository.findAll();
        }

        // 3️⃣ Validación
        if (turnos.isEmpty()) {
            throw new BusinessException(
                    "No se encontraron turnos",
                    HttpStatus.NOT_FOUND
            );
        }

        // 4️⃣ Mappeo final
        return turnos.stream()
                .map(t -> modelMapper.map(t, TurnoDTO.class))
                .toList();
    }

}
