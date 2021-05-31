package vladimir.petrovic.daon.airportGate.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import vladimir.petrovic.daon.airportGate.util.ErrorType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String error;
    private String message;

    public ErrorResponse(HttpStatus status, ErrorType errorType) {
        this.status = status;
        this.error = errorType.toString();
        this.message = errorType.getMessage();
    }
}
