package vladimir.petrovic.daon.airportGate.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vladimir.petrovic.daon.airportGate.repository.entity.AirportGateEntity;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirportGateMapper {

    List<AirportGateResponse> mapGates(List<AirportGateEntity> airportGates);

    @Mapping(target = "flightNumber", source = "airportGate.flight.flightNumber")
    AirportGateResponse mapSuccessfulResponse(AirportGateEntity airportGate);
    AirportGateResponse mapErrorResponse(String error, String message);

    ValidationResponse mapValidationResponse(String gateCode, boolean successful);
    ValidationResponse mapValidationErrorResponse(String gateCode, boolean successful, String error, String message);
}
