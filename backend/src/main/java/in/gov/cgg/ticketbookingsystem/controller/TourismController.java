package in.gov.cgg.ticketbookingsystem.controller;

import in.gov.cgg.ticketbookingsystem.model.operations.CityTourismData;
import in.gov.cgg.ticketbookingsystem.service.TourismService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tourism")
@RequiredArgsConstructor
public class TourismController {

    private final TourismService tourismService;

    @GetMapping
    public ResponseEntity<CityTourismData> getTourismData(@RequestParam String city) {
        CityTourismData data = tourismService.getTourismData(city);
        return ResponseEntity.ok(data);
    }
}
