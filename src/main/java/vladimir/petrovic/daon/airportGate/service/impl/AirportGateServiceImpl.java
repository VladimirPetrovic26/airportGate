package vladimir.petrovic.daon.airportGate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vladimir.petrovic.daon.airportGate.exception.AirportGateException;
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
            throw new AirportGateException(ErrorType.FLIGHT_NUMBER_BLANK);
        }

        log.info("Searching for available gate!");

        LocalDateTime now = LocalDateTime.now();

        try {
            Optional<AirportGateEntity> airportGate =
                    airportGateRepository.findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(now, now);
            if (!airportGate.isPresent()) {
                return mapper.mapErrorResponse(
                        ErrorType.NO_AVAILABLE_GATE.toString(),
                        ErrorType.NO_AVAILABLE_GATE.getMessage());
            }

            return processAirportGate(airportGate.get(), flightNumber);
        }
        catch (RuntimeException e) {
            log.error("An exception occurred while assigning gate to a flight!", e);
            throw new AirportGateException("An exception occurred while assigning gate to a flight!");
        }
    }

    /**
     * Assigns gate to a flight and maps response
     *
     * @param airportGate
     * @param flightNumber
     * @return AirportGateResponse
     */
    private AirportGateResponse processAirportGate(AirportGateEntity airportGate, String flightNumber) {

        log.info("Searching for flight with number {}.", flightNumber);
        FlightEntity flight = flightRepository.findByFlightNumber(flightNumber)
                .orElse(new FlightEntity(flightNumber));

        if (flight.getAirportGate() != null) {
            log.error("Flight already assigned to the gate {}.", flight);
            return mapper.mapErrorResponse(ErrorType.FLIGHT_ALREADY_ASSIGNED.toString(), ErrorType.FLIGHT_ALREADY_ASSIGNED.getMessage());
        }

        log.info("Assigning flight {} to gate {}.", flightNumber, airportGate);
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
            throw new AirportGateException(ErrorType.GATE_CODE_BLANK);
        }

        log.info("Searching for a gate with the code {}.", gateCode);

        try {
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
        catch (RuntimeException e) {
            log.error("Something went wrong freeing gate {}!", gateCode, e);
            throw new AirportGateException("Something went wrong freeing gate " + gateCode);
        }
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

        validateUpdateGateRequest(request);

        log.info("Searching for gate with code {}.", request.getGateCode());

        try {
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
        catch (RuntimeException e) {
            log.error("Something went wrong updating gate with request {}", request, e);
            throw new AirportGateException("Something went wrong updating gate!");
        }
    }

    /**
     * Validates if update gate request has all valid fields
     *
     * @param request
     * @return ValidationResponse
     */
    private void validateUpdateGateRequest(UpdateGateRequest request) {
        log.info("Validating update gate request {}", request);
        if (request == null) {
            log.error("Missing update gate request!");
            throw new AirportGateException(ErrorType.UPDATE_REQUEST_NULL);
        }

        if (StringUtils.isBlank(request.getGateCode())) {
            log.error("Missing gateCode data in update gate request {}", request);
            throw new AirportGateException(ErrorType.UPDATE_REQUEST_GATE_CODE);
        }

        if (request.getAvailableFrom() == null) {
            log.error("Missing lockedFrom data in update gate request {}", request);
            throw new AirportGateException(ErrorType.UPDATE_REQUEST_AVAILABLE_FROM);
        }

        if (request.getAvailableTo() == null) {
            log.error("Missing lockedTo data in update gate request {}", request);
            throw new AirportGateException(ErrorType.UPDATE_REQUEST_AVAILABLE_TO);
        }

        if (request.getAvailableFrom().isAfter(request.getAvailableTo()) ||
                request.getAvailableFrom().isEqual(request.getAvailableTo())) {
            log.error("Available from date must be lesser than availableTo {}", request);
            throw new AirportGateException(ErrorType.AVAILABLE_FROM_GREATER_THAN_AVAILABLE_TO);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return List<AirportGateResponse>
     */
    @Override
    public List<AirportGateResponse> findAll() {
        log.info("Fetching all gates!");

        try {
            List<AirportGateEntity> gates = airportGateRepository.findAll();

            if (CollectionUtils.isEmpty(gates)) {
                return Arrays.asList(
                        mapper.mapErrorResponse(
                                ErrorType.NO_AVAILABLE_GATE.toString(),
                                ErrorType.NO_AVAILABLE_GATE.getMessage()));
            }

            return mapper.mapGates(gates);
        }
        catch (RuntimeException e) {
            log.error("Something went wrong fetching all gates!");
            throw new AirportGateException("Something went wrong fetching all gates!");
        }
    }
}
