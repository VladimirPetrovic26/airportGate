package vladimir.petrovic.daon.airportGate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vladimir.petrovic.daon.airportGate.service.AirportGateService;
import vladimir.petrovic.daon.airportGate.service.model.request.UpdateGateRequest;
import vladimir.petrovic.daon.airportGate.service.model.response.AirportGateResponse;
import vladimir.petrovic.daon.airportGate.service.model.response.ValidationResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gate")
@RequiredArgsConstructor
public class AirportGateController {

    private final AirportGateService airportGateService;

    @PostMapping("/assign/{flightNumber}")
    public AirportGateResponse assignGateToFlight(@PathVariable("flightNumber") String flightNumber) {
        log.info("Assigning flight with number [{}] to first available gate.", flightNumber);
        return airportGateService.assignGate(flightNumber);
    }

    @PatchMapping("/free/{gateCode}")
    public ValidationResponse freeGate(@PathVariable("gateCode") String gateCode) {
        log.info("Setting gate with code [{}] free.", gateCode);
        return airportGateService.freeGate(gateCode);
    }

    @PatchMapping("/update")
    public ValidationResponse updateGateAvailabilityTime(@RequestBody UpdateGateRequest request) {
        log.info("Updating gate lock time {}.", request);
        return airportGateService.updateGateAvailabilityTime(request);
    }

    @GetMapping("/findAll")
    public List<AirportGateResponse> freeGate() {
        log.info("Searching for all gates.");
        return airportGateService.findAll();
    }

}
