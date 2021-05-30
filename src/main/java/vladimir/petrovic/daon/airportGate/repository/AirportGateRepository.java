package vladimir.petrovic.daon.airportGate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vladimir.petrovic.daon.airportGate.repository.entity.AirportGateEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AirportGateRepository extends JpaRepository<AirportGateEntity, String> {
    Optional<AirportGateEntity> findFirstByFlightIsNullAndAvailableFromLessThanAndAvailableToGreaterThanOrFlightIsNullAndAvailableFromIsNullAndAvailableToIsNull(LocalDateTime lockedFrom,LocalDateTime lockeTo);
    Optional<AirportGateEntity> findByGateCode(String gateCode);
}
