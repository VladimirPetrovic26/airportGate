package vladimir.petrovic.daon.airportGate.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vladimir.petrovic.daon.airportGate.service.AirportGateService;
import vladimir.petrovic.daon.airportGate.service.model.request.UpdateGateRequest;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;
import vladimir.petrovic.daon.airportGate.util.AirportGateTestConstants;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AirportGateControllerTest {

    private AirportGateService airportGateService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        airportGateService = mock(AirportGateService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AirportGateController(airportGateService))
                .build();
    }

    @Test
    void assign_gate_to_flight() throws Exception {
        when(airportGateService.assignGate(any())).thenReturn(new AirportGateResponse());

        mockMvc.perform(
                post(
                        ("/gate/assign/").concat(AirportGateTestConstants.FLIGHT_NUMBER)))
                .andExpect(status()
                        .isOk());

        verify(airportGateService, times(1))
                .assignGate(anyString());
    }

    @Test
    void free_gate() throws Exception {
        when(airportGateService.freeGate(any())).thenReturn(new ValidationResponse());

        mockMvc.perform(
                patch(
                        ("/gate/free/").concat(AirportGateTestConstants.GATE_CODE)))
                .andExpect(status()
                        .isOk());

        verify(airportGateService, times(1))
                .freeGate(anyString());
    }

    @Test
    void updateGateAvailabilityTime() throws Exception {
        when(airportGateService.updateGateAvailabilityTime(any())).thenReturn(new ValidationResponse());

        mockMvc.perform(
                patch("/gate/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertDtoToJson(new UpdateGateRequest())))
                .andExpect(status()
                        .isOk());

        verify(airportGateService, times(1))
                .updateGateAvailabilityTime(any());
    }

    @Test
    void testFreeGate() throws Exception {
        when(airportGateService.findAll()).thenReturn(new ArrayList<AirportGateResponse>());

        mockMvc.perform(
                get("/gate/findAll"))
                .andExpect(status()
                        .isOk());

        verify(airportGateService, times(1))
                .findAll();
    }

    private String convertDtoToJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }
}