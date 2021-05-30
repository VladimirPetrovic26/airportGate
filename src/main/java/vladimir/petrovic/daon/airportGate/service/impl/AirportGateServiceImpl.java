package vladimir.petrovic.daon.airportGate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vladimir.petrovic.daon.airportGate.repository.AirportGateRepository;
import vladimir.petrovic.daon.airportGate.repository.FlightRepository;
import vladimir.petrovic.daon.airportGate.repository.entity.AirportGateEntity;
import vladimir.petrovic.daon.airportGate.repository.entity.FlightEntity;
import vladimir.petrovic.daon.airportGate.service.AirportGateService;
import vladimir.petrovic.daon.airportGate.service.mapper.AirportGateMapper;
import vladimir.petrovic.daon.airportGate.service.model.request.UpdateGateRequest;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;
import vladimir.petrovic.daon.airportGate.util.ErrorType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirportGateServiceImpl implements AirportGateService {

    private final AirportGateRepository airportGateRepository;
    private final FlightRepository flightRepository;
    private final AirportGateMapper mapper;

    /**
     * {@inheritDoc}
     *
     * @param flightNumber
     * @return AirportGateResponse
     */
    @Transactional
    @Override
    public AirportGateResponse assignGate(String flightNumber) {
        if (StringUtils.isBlank(flightNumber)) {
            log.error("Flight number cannot be blank!");
            return mapper.mapErrorResponse(
                    ErrorType.FLIGHT_NUMBER_BLANK.toString(),
                    ErrorType.FLIGHT_NUMBER_BLANK.getMessage());
        }

        log.info("Searching for available gate!");

        LocalDateTime now = LocalDateTime.now();
        Optional<AirportGateEntity> airportGate =
                airportGateRepository.findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(now, now);

        if (!airportGate.isPresent()) {
            return mapper.mapErrorResponse(
                    ErrorType.NO_AVAILABLE_GATE.toString(),
                    ErrorType.NO_AVAILABLE_GATE.getMessage());
        }

        return processAirportGate(airportGate.get(), flightNumber);
    }

    /**
     * Assigns gate to a flight and maps response
     *
     * @param airportGate
     * @param flightNumber
     * @return AirportGateResponse
     */
    private AirportGateResponse processAirportGate(AirportGateEntity airportGate, String flightNumber) {

        log.info("Searching for flight with number {}", flightNumber);
        FlightEntity flight = flightRepository.findByFlightNumber(flightNumber)
                .orElse(new FlightEntity(flightNumber));

        if (flight.getAirportGate() != null) {
            log.error("Flight already assigned to the gate {}.", flight);
            return mapper.mapErrorResponse(ErrorType.FLIGHT_ALREADY_ASSIGNED.toString(), ErrorType.FLIGHT_ALREADY_ASSIGNED.getMessage());
        }

        log.info("Assigning flight {} to gate {}", flightNumber, airportGate);
        airportGate.setFlight(flight);
        flight.setAirportGate(airportGate);

        return mapper.mapSuccessfulResponse(airportGate);
    }

    /**
     * {@inheritDoc}
     *
     * @param gateCode
     * @return ValidationResponse
     */
    @Transactional
    @Override
    public ValidationResponse freeGate(String gateCode) {
        if (StringUtils.isBlank(gateCode)) {
            log.error("Gate code can not be null");
            return mapper.mapValidationErrorResponse(
                    gateCode,
                    Boolean.FALSE,
                    ErrorType.GATE_CODE_BLANK.toString(),
                    ErrorType.GATE_CODE_BLANK.getMessage());
        }

        log.info("Searching for a gate with the code {}.", gateCode);

        Optional<AirportGateEntity> airportGateOptional = airportGateRepository.findByGateCode(gateCode);
        if (!airportGateOptional.isPresent()) {
            log.error("No gate found for code {}.", gateCode);
            return mapper.mapValidationErrorResponse(
                    gateCode,
                    Boolean.FALSE,
                    ErrorType.NO_GATES_FOR_CODE.toString(),
                    ErrorType.NO_GATES_FOR_CODE.getMessage());
        }

        AirportGateEntity airportGate = airportGateOptional.get();
        log.info("Updating gate free {}.", airportGate);
        airportGate.getFlight().setAirportGate(null);
        airportGate.setFlight(null);

        return mapper.mapValidationResponse(gateCode, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     *
     * @param request
     * @return ValidationResponse
     */
    @Transactional
    @Override
    public ValidationResponse updateGateAvailabilityTime(UpdateGateRequest request) {
        Optional<ValidationResponse> validationError = validateUpdateGateRequest(request);
        if (validationError.isPresent()) {
            return validationError.get();
        }

        log.info("Searching for gate with code {}", request.getGateCode());
        Optional<AirportGateEntity> airportGateUpdateCandidate = airportGateRepository.findByGateCode(request.getGateCode());
        if (airportGateUpdateCandidate.isPresent()) {

            AirportGateEntity airportGate = airportGateUpdateCandidate.get();
            log.info("Updating airport gate {} with request data {}", airportGate, request);
            airportGate.setAvailableFrom(request.getAvailableFrom());
            airportGate.setAvailableTo(request.getAvailableTo());

            return mapper.mapValidationResponse(request.getGateCode(), Boolean.TRUE);
        }

        log.warn("No gate found for request {}", request);
        return mapper.mapValidationErrorResponse(
                request.getGateCode(),
                Boolean.FALSE,
                ErrorType.NO_AVAILABLE_GATE.toString(),
                ErrorType.NO_GATES_FOR_CODE.getMessage());
    }

    /**
     * Validates if update gate request has all valid fields
     *
     * @param request
     * @return ValidationResponse
     */
    private Optional<ValidationResponse> validateUpdateGateRequest(UpdateGateRequest request) {
        log.info("Validating update gate request {}", request);
        if (request == null) {
            log.error("Missing update gate request!");
            return Optional.of(
                    mapper.mapValidationErrorResponse(
                            StringUtils.EMPTY,
                            Boolean.FALSE,
                            ErrorType.UPDATE_REQUEST_NULL.toString(),
                            ErrorType.UPDATE_REQUEST_NULL.getMessage()));
        }

        if (StringUtils.isBlank(request.getGateCode())) {
            log.error("Missing gateCode data in update gate request {}", request);
            return Optional.of(
                    mapper.mapValidationErrorResponse(
                            StringUtils.EMPTY,
                            Boolean.FALSE,
                            ErrorType.UPDATE_REQUEST_GATE_CODE.toString(),
                            ErrorType.UPDATE_REQUEST_GATE_CODE.getMessage()));
        }

        if (request.getAvailableFrom() == null) {
            log.error("Missing lockedFrom data in update gate request {}", request);
            return Optional.of(
                    mapper.mapValidationErrorResponse(
                            request.getGateCode(),
                            Boolean.FALSE,
                            ErrorType.UPDATE_REQUEST_AVAILABLE_FROM.toString(),
                            ErrorType.UPDATE_REQUEST_AVAILABLE_FROM.getMessage()));
        }

        if (request.getAvailableTo() == null) {
            log.error("Missing lockedTo data in update gate request {}", request);
            return Optional.of(
                    mapper.mapValidationErrorResponse(
                            request.getGateCode(),
                            Boolean.FALSE,
                            ErrorType.UPDATE_REQUEST_AVAILABLE_TO.toString(),
                            ErrorType.UPDATE_REQUEST_AVAILABLE_TO.getMessage()));
        }

        if (request.getAvailableFrom().isAfter(request.getAvailableTo()) ||
                request.getAvailableFrom().isEqual(request.getAvailableTo())) {
            log.error("Available from date must be lesser than availableTo {}", request);
            return Optional.of(
                    mapper.mapValidationErrorResponse(
                            request.getGateCode(),
                            Boolean.FALSE,
                            ErrorType.AVAILABLE_FROM_GREATER_THAN_AVAILABLE_TO.toString(),
                            ErrorType.AVAILABLE_FROM_GREATER_THAN_AVAILABLE_TO.getMessage()));
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     * @return List<AirportGateResponse>
     */
    @Override
    public List<AirportGateResponse> findAll() {
        List<AirportGateEntity> gates = airportGateRepository.findAll();

        if (CollectionUtils.isEmpty(gates)) {
            return Arrays.asList(
                    mapper.mapErrorResponse(
                            ErrorType.NO_AVAILABLE_GATE.toString(),
                            ErrorType.NO_AVAILABLE_GATE.getMessage()));
        }

        return mapper.mapGates(gates);
    }
}
