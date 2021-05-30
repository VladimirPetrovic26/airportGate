package vladimir.petrovic.daon.airportGate.service;

import vladimir.petrovic.daon.airportGate.service.model.request.UpdateGateRequest;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;

import java.util.List;

public interface AirportGateService {

    /**
     * Searches for free Gate and assign it to Flight with FlightNumber
     *
     * @param flightNumber
     * @return AirportGateResponse
     */
    AirportGateResponse assignGate(String flightNumber);

    /**
     * Frees gate with the code
     *
     * @param gateCode
     * @return ValidationResponse
     */
    ValidationResponse freeGate(String gateCode);

    /**
     * Search for gate and update its lock time
     *
     * @param request
     * @return ValidationResponse
     */
    ValidationResponse updateGateAvailabilityTime(UpdateGateRequest request);

    /**
     * Searches for all gates in DB
     *
     * @return List<AirportGateResponse>
     */
    List<AirportGateResponse> findAll();
}
