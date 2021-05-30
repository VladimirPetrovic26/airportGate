package vladimir.petrovic.daon.airportGate.service.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UpdateGateRequest {
    private String gateCode;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
}
