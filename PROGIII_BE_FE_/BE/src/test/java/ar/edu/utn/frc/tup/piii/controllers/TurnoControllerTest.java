package ar.edu.utn.frc.tup.piii.controllers;

import ar.edu.utn.frc.tup.piii.dtos.NewTurnoDTO;
import ar.edu.utn.frc.tup.piii.dtos.TurnoDTO;
import ar.edu.utn.frc.tup.piii.services.TurnoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTurnosTest() throws Exception {
        //when(turnoService.getAllTurnos()).then(List.of(TurnoDTO));
       // fail("No test implemented yet");
    }

    @Test
    void getTurnosByEstudioTest() throws Exception {

        fail("No test implemented yet");
    }

    @Test
    void getTurnosByExtractorTest() throws Exception {
        fail("No test implemented yet");
    }

    @Test
    void getTurnosByFechaHoraTest() throws Exception {
        fail("No test implemented yet");
    }

    @Test
    void getTurnosByPacienteTest() throws Exception {
        fail("No test implemented yet");
    }

    @Test
    void createNewTurnoTest() throws Exception {
//
    }

    @Test
    void programarTurnosTest() throws Exception {

    }

}