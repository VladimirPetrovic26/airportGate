package vladimir.petrovic.daon.airportGate.service.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResponse extends AbstractResponse {
    private String gateCode;
    private boolean successful;
}
