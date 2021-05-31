package vladimir.petrovic.daon.airportGate.exception.handlers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import vladimir.petrovic.daon.airportGate.exception.AirportGateException;
import vladimir.petrovic.daon.airportGate.exception.dto.ErrorResponse;

@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {AirportGateException.class})
    public ErrorResponse handleAirportGateException(AirportGateException exception) {
        if (exception.getErrorType() != null) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getErrorType());
        }

        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.EMPTY, exception.getMessage());
    }
}
