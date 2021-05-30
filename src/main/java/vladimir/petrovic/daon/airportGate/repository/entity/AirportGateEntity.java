package vladimir.petrovic.daon.airportGate.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Airport gate entity
 */
@Getter
@Setter
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
