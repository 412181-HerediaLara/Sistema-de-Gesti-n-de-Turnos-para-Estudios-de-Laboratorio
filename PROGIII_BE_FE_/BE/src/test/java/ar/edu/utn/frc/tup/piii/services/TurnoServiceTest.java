package ar.edu.utn.frc.tup.piii.services;

import ar.edu.utn.frc.tup.piii.dtos.NewTurnoDTO;
import ar.edu.utn.frc.tup.piii.dtos.TurnoDTO;
import ar.edu.utn.frc.tup.piii.repositories.DisponibilidadTurnosRepository;
import ar.edu.utn.frc.tup.piii.repositories.TurnoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class TurnoServiceTest {

    @MockitoSpyBean
    private TurnoService turnoService;

    @MockitoBean
    private TurnoRepository turnoRepository;

    @MockitoBean
    private ExtractorService extractorService;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private EstudioService estudioService;

    @MockitoBean
    private DisponibilidadTurnosRepository disponibilidadTurnosRepository;

    @Test
    void createTurnoHappyPathTest() {
        fail("No test implemented yet");
    }

    @Test
    void createTurnoFailPacienteOrExtractorOrEstudioTest() {
        fail("No test implemented yet");
    }

    @Test
    void createTurnoFailPacienteTieneTurnoMismoDiaTest() {
        fail("No test implemented yet");
    }

    @Test
    void createTurnoFailTurnoExtractorOcupadoTest() {
        fail("No test implemented yet");
    }

    @Test
    void programarTurnosHappyPathTest() {
        fail("No test implemented yet");
    }

    @Test
    void programarTurnosFailDiaYaProgramado() {
        fail("No test implemented yet");
    }

    @Test
    void getAllTurnosByExtractor() {

    }

    @Test
    void getAllTurnosByPaciente() {

    }

    @Test
    void getAllTurnosByEstudio() {

    }

    @Test
    void getAllTurnosByFechaHora() {

    }

    @Test
    void getAllTurnos() {

    }


}
