package vladimir.petrovic.daon.airportGate.service.model.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGateRequest {
    private String gateCode;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
}
