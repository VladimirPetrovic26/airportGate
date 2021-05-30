package vladimir.petrovic.daon.airportGate.repository.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FLIGHT")
public class FlightEntity {

    public FlightEntity(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "flight_number", unique = true)
    private String flightNumber;

    @OneToOne
    private AirportGateEntity airportGate;
}
