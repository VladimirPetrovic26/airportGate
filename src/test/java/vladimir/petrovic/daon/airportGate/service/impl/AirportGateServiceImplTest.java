package vladimir.petrovic.daon.airportGate.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vladimir.petrovic.daon.airportGate.repository.AirportGateRepository;
import vladimir.petrovic.daon.airportGate.repository.FlightRepository;
import vladimir.petrovic.daon.airportGate.repository.entity.AirportGateEntity;
import vladimir.petrovic.daon.airportGate.repository.entity.FlightEntity;
import vladimir.petrovic.daon.airportGate.service.AirportGateService;
import vladimir.petrovic.daon.airportGate.service.mapper.AirportGateMapper;
import vladimir.petrovic.daon.airportGate.service.model.request.UpdateGateRequest;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;
import vladimir.petrovic.daon.airportGate.util.AirportGateTestConstants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AirportGateServiceImplTest {

    private AirportGateRepository airportGateRepository;
    private FlightRepository flightRepository;
    private AirportGateMapper mapper;
    private AirportGateService airportGateService;

    @BeforeEach
    void setUp() {
        airportGateRepository = mock(AirportGateRepository.class);
        flightRepository = mock(FlightRepository.class);
        mapper = mock(AirportGateMapper.class);
        airportGateService = new AirportGateServiceImpl(airportGateRepository, flightRepository, mapper);
    }

    @Test
    void assign_gate_flight_number_blank() {
        when(mapper.mapErrorResponse(anyString(), anyString())).thenReturn(getErrorResponse());

        AirportGateResponse response = airportGateService.assignGate(StringUtils.SPACE);

        testErrorResponse(response);
    }

    @Test
    void assign_gate_flight_entity_not_found() {
        when(
                airportGateRepository.findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(), any()))
                .thenReturn(Optional.empty());
        when(mapper.mapErrorResponse(anyString(), anyString())).thenReturn(getErrorResponse());

        AirportGateResponse response = airportGateService.assignGate(AirportGateTestConstants.FLIGHT_NUMBER);

        verify(airportGateRepository, times(1))
                .findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(), any());

        testErrorResponse(response);
    }

    @Test
    void assign_gate_flight_already_assigned() {
        when(airportGateRepository.findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(),any()))
                .thenReturn(
                        Optional.of(
                                AirportGateEntity
                                        .builder()
                                        .gateCode(AirportGateTestConstants.GATE_CODE)
                                        .availableFrom(LocalDateTime.now())
                                        .availableTo(LocalDateTime.now())
                                        .build()));

        when(flightRepository.findByFlightNumber(anyString()))
                .thenReturn(
                        Optional.of(
                                FlightEntity
                                        .builder()
                                        .airportGate(new AirportGateEntity())
                                        .build()
                        ));
        when(mapper.mapErrorResponse(anyString(), anyString())).thenReturn(getErrorResponse());

        AirportGateResponse response = airportGateService.assignGate(AirportGateTestConstants.FLIGHT_NUMBER);

        verify(airportGateRepository, times(1))
                .findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(), any());
        verify(flightRepository, times(1))
                .findByFlightNumber(anyString());

        testErrorResponse(response);
    }

    private void testErrorResponse(AirportGateResponse response) {
        verify(mapper, times(1))
                .mapErrorResponse(anyString(), anyString());

        assertEquals(response.getError(), AirportGateTestConstants.ERROR_TYPE);
        assertEquals(response.getMessage(), AirportGateTestConstants.ERROR_MESSAGE);
        assertEquals(response.getGateCode(), null);
        assertEquals(response.getFlightNumber(), null);
        assertEquals(response.getAvailableFrom(), null);
        assertEquals(response.getAvailableTo(), null);
    }

    @Test
    void assign_gate_flight() {
        when(airportGateRepository.findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(),any()))
                .thenReturn(
                        Optional.of(
                                AirportGateEntity
                                        .builder()
                                        .gateCode(AirportGateTestConstants.GATE_CODE)
                                        .availableFrom(LocalDateTime.now())
                                        .availableTo(LocalDateTime.now())
                                        .build()));

        when(flightRepository.findByFlightNumber(anyString()))
                .thenReturn(Optional.of(new FlightEntity()));

        when(airportGateRepository.save(any())).then(mock -> mock.getArguments()[0]);
        when(flightRepository.save(any())).then(mock -> mock.getArguments()[0]);

        when(mapper.mapSuccessfulResponse(any())).thenReturn(getErrorResponse());

        airportGateService.assignGate(AirportGateTestConstants.FLIGHT_NUMBER);

        verify(airportGateRepository, times(1))
                .findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(any(), any());

        verify(flightRepository, times(1)).findByFlightNumber(anyString());
        verify(mapper, times(1)).mapSuccessfulResponse(any());
    }

    @Test
    void free_gate_blank_gate_code() {
        when(mapper.mapErrorResponse(anyString(), anyString())).thenReturn(getErrorResponse());

        airportGateService.freeGate(StringUtils.SPACE);

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());
    }

    @Test
    void free_gate_entity_not_found() {
        when(airportGateRepository.findByGateCode(any())).thenReturn(Optional.empty());
        when(mapper.mapErrorResponse(anyString(), anyString())).thenReturn(getErrorResponse());

        airportGateService.freeGate(AirportGateTestConstants.GATE_CODE);

        verify(airportGateRepository, times(1))
                .findByGateCode(anyString());
        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());
    }

    @Test
    void free_gate() {
        AirportGateEntity airportGateEntity = new AirportGateEntity();
        FlightEntity flightEntity = FlightEntity
                .builder()
                .airportGate(airportGateEntity)
                .build();
        airportGateEntity.setFlight(flightEntity);

        when(airportGateRepository.findByGateCode(any())).thenReturn(Optional.of(airportGateEntity));
        when(mapper.mapValidationResponse(anyString(), anyBoolean())).thenReturn(new ValidationResponse());

        airportGateService.freeGate(AirportGateTestConstants.GATE_CODE);

        verify(airportGateRepository, times(1))
                .findByGateCode(anyString());
        verify(mapper, times(1))
                .mapValidationResponse(anyString(), anyBoolean());

        assertNull(airportGateEntity.getFlight());
        assertNull(flightEntity.getAirportGate());
    }

    @Test
    void update_gate_availability_time_null_request() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        airportGateService.updateGateAvailabilityTime(null);

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability_no_gate_code() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        airportGateService.updateGateAvailabilityTime(new UpdateGateRequest());

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability_no_available_from() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        airportGateService.updateGateAvailabilityTime(
                UpdateGateRequest
                        .builder()
                        .gateCode(AirportGateTestConstants.GATE_CODE)
                        .build());

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability_no_available_to() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        airportGateService.updateGateAvailabilityTime(
                UpdateGateRequest
                        .builder()
                        .gateCode(AirportGateTestConstants.GATE_CODE)
                        .availableFrom(LocalDateTime.now())
                        .build());

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability_available_from_greater_than_available_to() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        airportGateService.updateGateAvailabilityTime(
                UpdateGateRequest
                        .builder()
                        .gateCode(AirportGateTestConstants.GATE_CODE)
                        .availableFrom(LocalDateTime.of(2025, 6, 12, 12,0,0))
                        .availableTo(LocalDateTime.of(2021, 6, 12, 12,0,0))
                        .build());

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability_no_gate_found() {
        when(mapper.mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(new ValidationResponse());
        when(airportGateRepository.findByGateCode(any())).thenReturn(Optional.empty());

        airportGateService.updateGateAvailabilityTime(
                UpdateGateRequest
                        .builder()
                        .gateCode(AirportGateTestConstants.GATE_CODE)
                        .availableFrom(LocalDateTime.of(2020, 6, 12, 12,0,0))
                        .availableTo(LocalDateTime.of(2021, 6, 12, 12,0,0))
                        .build());

        verify(airportGateRepository, times(1))
                .findByGateCode(anyString());

        verify(mapper, times(1))
                .mapValidationErrorResponse(anyString(), anyBoolean(), anyString(), anyString());

        verify(mapper, times(0))
                .mapValidationResponse(anyString(), anyBoolean());
    }

    @Test
    void update_gate_availability() {
        AirportGateEntity airportGateEntity = AirportGateEntity
                .builder()
                .availableFrom(LocalDateTime.now())
                .availableTo(LocalDateTime.now())
                .build();

        when(mapper.mapValidationResponse(anyString(), anyBoolean())).thenReturn(new ValidationResponse());
        when(airportGateRepository.findByGateCode(any())).thenReturn(Optional.of(airportGateEntity));

        LocalDateTime updatedAvailableFrom = LocalDateTime.of(2021, 1, 1, 1,1,1);
        LocalDateTime updatedAvailableTo = LocalDateTime.of(2021, 2, 2, 2,2,2);

        airportGateService.updateGateAvailabilityTime(
                UpdateGateRequest
                        .builder()
                        .gateCode(AirportGateTestConstants.GATE_CODE)
                        .availableFrom(updatedAvailableFrom)
                        .availableTo(updatedAvailableTo)
                        .build());

        verify(airportGateRepository, times(1))
                .findByGateCode(anyString());

        verify(mapper, times(1))
                .mapValidationResponse(anyString(), anyBoolean());

        assertEquals(airportGateEntity.getAvailableFrom(), updatedAvailableFrom);
        assertEquals(airportGateEntity.getAvailableTo(), updatedAvailableTo);
    }

    @Test
    void find_all_no_gates_found() {
        when(airportGateRepository.findAll()).thenReturn(Collections.emptyList());

        airportGateService.findAll();

        verify(airportGateRepository, times(1)).findAll();
        verify(mapper, times(1)).mapErrorResponse(anyString(), anyString());
    }

    @Test
    void find_all() {
        when(airportGateRepository.findAll())
                .thenReturn(
                        Arrays.asList(
                                new AirportGateEntity()));

        when(mapper.mapGates(any()))
                .thenReturn(
                        Arrays.asList(
                                new AirportGateResponse()));

        airportGateService.findAll();

        verify(airportGateRepository, times(1)).findAll();
        verify(mapper, times(1)).mapGates(any());
    }

    private AirportGateResponse getErrorResponse() {
        AirportGateResponse response = new AirportGateResponse();
        response.setError(AirportGateTestConstants.ERROR_TYPE);
        response.setMessage(AirportGateTestConstants.ERROR_MESSAGE);
        return response;
    }
}