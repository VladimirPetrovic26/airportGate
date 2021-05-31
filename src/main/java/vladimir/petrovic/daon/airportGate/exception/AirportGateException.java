package vladimir.petrovic.daon.airportGate.exception;

import lombok.Getter;
import lombok.Setter;
import vladimir.petrovic.daon.airportGate.util.ErrorType;

public class AirportGateException extends RuntimeException {

    @Getter
    @Setter
    private ErrorType errorType;

    public AirportGateException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public AirportGateException(String message) {
        super(message);
    }

    public AirportGateException(String message, Throwable cause) {
        super(message, cause);
    }
}
