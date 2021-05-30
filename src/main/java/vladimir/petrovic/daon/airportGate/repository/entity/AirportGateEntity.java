package vladimir.petrovic.daon.airportGate.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Airport gate entity
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AIRPORT_GATE")
public class AirportGateEntity {

    @Id
    private String id;

    @Column(name = "gate_code", unique = true)
    private String gateCode;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_to")
    private LocalDateTime availableTo;
}
