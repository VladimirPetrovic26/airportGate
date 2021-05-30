package vladimir.petrovic.daon.airportGate.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import vladimir.petrovic.daon.airportGate.repository.entity.AirportGateEntity;
import vladimir.petrovic.daon.airportGate.repository.entity.FlightEntity;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;
import vladimir.petrovic.daon.airportGate.util.AirportGateTestConstants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AirportGateMapperTest {

    private AirportGateMapper mapper;

    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private FlightEntity flightEntity;
    private AirportGateEntity airportGateEntity;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AirportGateMapper.class);

        availableFrom = LocalDateTime.of(2021, 1, 1, 1,1,1);
        availableTo = LocalDateTime.of(2021, 2, 2, 2,2,2);

        buildFlightEntity();
        buildAirportGateEntity();
    }

    @Test
    void map_gates() {
        List<AirportGateEntity> airportGates = Arrays.asList(airportGateEntity);

        List<AirportGateResponse> response = mapper.mapGates(airportGates);

        assertEquals(response.size(), airportGates.size());
        testAirportGateResponse(response.get(0));
    }

    @Test
    void map_successful_response() {
        AirportGateResponse response = mapper.mapSuccessfulResponse(airportGateEntity);

        testAirportGateResponse(response);
    }

    private void testAirportGateResponse(AirportGateResponse response) {
        assertNotNull(response);
        assertEquals(response.getGateCode(), AirportGateTestConstants.GATE_CODE);
        assertEquals(response.getFlightNumber(), AirportGateTestConstants.FLIGHT_NUMBER);
        assertEquals(response.getAvailableFrom(), availableFrom);
        assertEquals(response.getAvailableTo(), availableTo);
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void map_error_response() {
        AirportGateResponse response = mapper.mapErrorResponse(AirportGateTestConstants.ERROR_TYPE, AirportGateTestConstants.ERROR_MESSAGE);

        assertNotNull(response);
        assertEquals(response.getError(), AirportGateTestConstants.ERROR_TYPE);
        assertEquals(response.getMessage(), AirportGateTestConstants.ERROR_MESSAGE);
        assertNull(response.getGateCode());
        assertNull(response.getFlightNumber());
        assertNull(response.getAvailableFrom());
        assertNull(response.getAvailableTo());
    }

    @Test
    void map_validation_response() {
        boolean successful = Boolean.TRUE;
        ValidationResponse response = mapper.mapValidationResponse(AirportGateTestConstants.GATE_CODE, successful);

        assertNotNull(response);
        assertEquals(response.getGateCode(), AirportGateTestConstants.GATE_CODE);
        assertEquals(response.isSuccessful(), successful);
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void map_validation_error_response() {
        boolean successful = Boolean.FALSE;
        ValidationResponse response = mapper.mapValidationErrorResponse(
                AirportGateTestConstants.GATE_CODE,
                successful,
                AirportGateTestConstants.ERROR_TYPE,
                AirportGateTestConstants.ERROR_MESSAGE);

        assertNotNull(response);
        assertEquals(response.getGateCode(), AirportGateTestConstants.GATE_CODE);
        assertEquals(response.isSuccessful(), successful);
        assertEquals(response.getError(), AirportGateTestConstants.ERROR_TYPE);
        assertEquals(response.getMessage(), AirportGateTestConstants.ERROR_MESSAGE);
    }

    private void buildFlightEntity() {
        flightEntity = FlightEntity
                .builder()
                .flightNumber(AirportGateTestConstants.FLIGHT_NUMBER)
                .build();
    }

    private void buildAirportGateEntity() {
        airportGateEntity = AirportGateEntity
                .builder()
                .gateCode(AirportGateTestConstants.GATE_CODE)
                .availableFrom(availableFrom)
                .availableTo(availableTo)
                .flight(flightEntity)
                .build();
    }
}