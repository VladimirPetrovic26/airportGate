package vladimir.petrovic.daon.airportGate.service.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AirportGateResponse extends AbstractResponse {
    private String gateCode;
    private String flightNumber;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
}
