package vladimir.petrovic.daon.airportGate.util;

import lombok.Getter;

public enum ErrorType {
    FLIGHT_NUMBER_BLANK("Flight number can not be blank"),
    NO_AVAILABLE_GATE("There are no available gates"),
    GATE_CODE_BLANK("Gate code cannot be blank"),
    NO_GATES_FOR_CODE("No gates found for requested code"),
    FLIGHT_ALREADY_ASSIGNED("Flight is already assigned to the gate"),
    UPDATE_REQUEST_NULL("No request object received"),
    UPDATE_REQUEST_GATE_CODE("Missing gate code in request"),
    UPDATE_REQUEST_AVAILABLE_FROM("Missing gate available from time request"),
    UPDATE_REQUEST_AVAILABLE_TO("Missing gate available to time request"),
    AVAILABLE_FROM_GREATER_THAN_AVAILABLE_TO("availableFrom time must be less then availableTo time");

    @Getter
    private String message;

    ErrorType(String message) {
        this.message = message;
    }
}
