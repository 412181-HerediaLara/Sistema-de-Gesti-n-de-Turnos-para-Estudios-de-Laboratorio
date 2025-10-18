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
        // TODO: Implementar la lógica para agendar un turno
        EstudioDTO estudioDTO = estudioService.getEstudioById(newTurnoDTO.getEstudioId());
        if (estudioDTO == null) {
            throw new BusinessException("No se encontraron estudios",HttpStatus.NOT_FOUND);
        }
        ExtractorDTO extractorDto = extractorService.getExtractorById(newTurnoDTO.getExtractorId());
        if (extractorDto == null) {
            throw new BusinessException("No se encontraron extractores",HttpStatus.NOT_FOUND);
        }
        PacienteDTO pacienteDto = pacienteService.getPacienteById(newTurnoDTO.getPacienteId());
        if (pacienteDto == null) {
            throw new BusinessException("No se encontrarion pacientes",HttpStatus.NOT_FOUND);
        }
        if(isValid(newTurnoDTO )){
            /*TurnoEntity turnoEntity = turnoRepository.findAll()
                    .stream().filter(t ->t.getFechaHora().equals(newTurnoDTO.getFechaHora())).findFirst()
                    .orElseThrow(() ->  new BusinessException("No se encontró el turno",HttpStatus.NOT_FOUND));*/
            TurnoEntity turnoEntity = turnoRepository.findTurnoEntitiesByFecha(newTurnoDTO.getFechaHora().toLocalDate()).stream().findFirst()
                    .orElseThrow(() -> new BusinessException("No se encontró el turno",HttpStatus.NOT_FOUND));
            turnoEntity.setEstudio(modelMapper.map(estudioDTO, EstudioEntity.class));
            turnoEntity.setExtractor(modelMapper.map(extractorDto, ExtractorEntity.class));
            turnoEntity.setPaciente(modelMapper.map(pacienteDto, PacienteEntity.class));
            turnoEntity.setStatus(TurnoStatus.DISPONIBLE);
            turnoEntity.setFechaHora(newTurnoDTO.getFechaHora());
            turnoEntity.setObservaciones(newTurnoDTO.getObservaciones());

            turnoRepository.save(turnoEntity);

            return modelMapper.map(turnoEntity, TurnoDTO.class);
        } else {
            throw new BusinessException("No se pudo crear el turno, intente nuevamente",HttpStatus.BAD_REQUEST);
        }

    }

    private boolean isValid( NewTurnoDTO newTurnoDto) {
        List<TurnoEntity> turnosPacientes = turnoRepository.findTurnoEntitiesByPacienteId(newTurnoDto.getPacienteId());
        turnosPacientes = turnosPacientes.stream().filter(t -> t.getFechaHora().getDayOfMonth() == newTurnoDto.getFechaHora().getDayOfMonth()).toList();
        if (!turnosPacientes.isEmpty()) {
            throw new BusinessException("El paciente ya tiene un turno agendado para el día indicado",HttpStatus.BAD_REQUEST);
        }

        List<TurnoEntity> turnosExtractores = turnoRepository.findTurnoEntitiesByFechaHoraAndExtractorId(newTurnoDto.getFechaHora(), newTurnoDto.getExtractorId());
        if(turnosExtractores.size() > 1){
            throw new BusinessException("Ya existe un turno para el extractor en esa fecha y hora",HttpStatus.BAD_REQUEST);
        }

        List<TurnoEntity> turnos =turnoRepository.findTurnoEntitiesByFechaHoraAndExtractorId(newTurnoDto.getFechaHora(), newTurnoDto.getExtractorId());
        turnos = turnos.stream().filter(t -> t.getStatus().equals(TurnoStatus.DISPONIBLE)).toList();
        if (turnos.isEmpty()) {
            throw new BusinessException("No hay turnos disponibles en ese horario para ese extractor", HttpStatus.BAD_REQUEST);
        }

        return true;
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
        // TODO: Implementar la lógica para programar turnos
        List<TurnoEntity> turnosEntity = turnoRepository.findTurnoEntitiesByFecha(fecha);
        if (!turnosEntity.isEmpty()) {
            throw new BusinessException("Los turnos ya están programados para la fecha indicada", HttpStatus.BAD_REQUEST);
        }

        List<DisponibilidadTurnosEntity> disponibilidades = disponibilidadTurnosRepository.findAll();
        List<ExtractorDTO> extractores = extractorService.getAllExtractores();
        List<TurnoDTO> turnos = new ArrayList<>();
        for (ExtractorDTO extractor : extractores) {
            for(DisponibilidadTurnosEntity disponibilidad : disponibilidades){
                LocalDateTime fechaHora = disponibilidad.getHoraInicio().atDate(fecha);
                TurnoEntity turnoEntity = new TurnoEntity();
                turnoEntity.setEstudio(null);
                turnoEntity.setPaciente(null);
                turnoEntity.setObservaciones(null);
                turnoEntity.setExtractor(modelMapper.map(extractor, ExtractorEntity.class));
                turnoEntity.setStatus(TurnoStatus.DISPONIBLE);
                turnoEntity.setFechaHora(fechaHora);

                turnoRepository.save(turnoEntity);

                turnos.add(modelMapper.map(turnoEntity, TurnoDTO.class));
            }
        }
        if (turnos.isEmpty()) {
           throw new BusinessException("No se pudieron programar los turnos", HttpStatus.BAD_REQUEST);
        }else {
            return turnos;
        }
    }

    @Override
    public List<TurnoDTO> obtenerTurnos(Optional<Long> estudioId, Optional<Long> extractorId, Optional<Long> pacienteId, Optional<LocalDate> fecha) {
        List<TurnoEntity> turnos;

        if(estudioId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByEstudioId(estudioId.get());
        } else if (extractorId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByExtractorId(extractorId.get());
        } else if (pacienteId.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByPacienteId(pacienteId.get());
        } else if (fecha.isPresent()) {
            turnos = turnoRepository.findTurnoEntitiesByFecha(fecha.get());
        } else {
            turnos = turnoRepository.findAll();
        }

        if(!turnos.isEmpty()){
            return turnos.stream().map(t -> modelMapper.map(t, TurnoDTO.class)).toList();
        } else {
            throw new BusinessException("No se pudieron obtener los turnos", HttpStatus.NOT_FOUND);
        }
    }
}
