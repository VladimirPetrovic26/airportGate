package vladimir.petrovic.daon.airportGate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vladimir.petrovic.daon.airportGate.repository.entity.FlightEntity;

import java.util.Optional;

//@Transactional
public interface FlightRepository extends JpaRepository<FlightEntity, String> {
    Optional<FlightEntity> findByFlightNumber(String flightNumber);
}
