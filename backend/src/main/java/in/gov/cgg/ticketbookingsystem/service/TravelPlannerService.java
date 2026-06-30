package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.model.dto.request.DestinationRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TravelPlanRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.ItineraryOption;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TravelPlanResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelPlannerService {

    private final TripService tripService;

    public TravelPlanResponse planTravel(TravelPlanRequest request) {
        List<List<DestinationRequest>> permutations = new ArrayList<>();
        if (request.flexibleOrder()) {
            generatePermutations(request.destinations(), 0, permutations);
        } else {
            permutations.add(request.destinations());
        }

        ItineraryOption cheapest = null;
        ItineraryOption shortest = null;

        for (List<DestinationRequest> perm : permutations) {
            List<List<TripResponse>> allValidRoutes = findValidRoutesForSequence(request.startCity(), request.startDate(), perm);
            for (List<TripResponse> route : allValidRoutes) {
                BigDecimal totalFare = route.stream()
                        .map(TripResponse::fare)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int totalDistance = route.stream()
                        .mapToInt(TripResponse::distanceKm)
                        .sum();

                ItineraryOption option = new ItineraryOption(route, totalFare, totalDistance);

                if (cheapest == null || option.totalFare().compareTo(cheapest.totalFare()) < 0) {
                    cheapest = option;
                }
                if (shortest == null || option.totalDistance() < shortest.totalDistance()) {
                    shortest = option;
                }
            }
        }

        return new TravelPlanResponse(cheapest, shortest);
    }

    private void generatePermutations(List<DestinationRequest> list, int index, List<List<DestinationRequest>> result) {
        if (index == list.size() - 1) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            swap(list, index, i);
            generatePermutations(list, index + 1, result);
            swap(list, index, i); // backtrack
        }
    }

    private void swap(List<DestinationRequest> list, int i, int j) {
        DestinationRequest temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private List<List<TripResponse>> findValidRoutesForSequence(String startCity, LocalDate startDate, List<DestinationRequest> destinations) {
        List<List<TripResponse>> validRoutes = new ArrayList<>();
        findRoutesBacktrack(startCity, startDate != null ? startDate.atStartOfDay() : null, destinations, 0, new ArrayList<>(), validRoutes);
        return validRoutes;
    }

    private void findRoutesBacktrack(String currentCity, LocalDateTime minStartTime, List<DestinationRequest> destinations, int index, List<TripResponse> currentRoute, List<List<TripResponse>> validRoutes) {
        if (index == destinations.size()) {
            validRoutes.add(new ArrayList<>(currentRoute));
            return;
        }

        DestinationRequest nextDest = destinations.get(index);
        // We only search for trips that arrive on the requested date if provided
        List<TripResponse> availableTrips = tripService.searchTrips(currentCity, nextDest.cityName(), null);

        for (TripResponse trip : availableTrips) {
            // Check time constraints
            if (minStartTime != null && trip.startTime().isBefore(minStartTime)) {
                continue;
            }
            if (nextDest.arrivalDate() != null && !trip.arrivalTime().toLocalDate().equals(nextDest.arrivalDate())) {
                continue;
            }

            // Valid trip for this hop, add and recurse
            currentRoute.add(trip);
            // Minimum 2 hours buffer for next transfer
            LocalDateTime nextMinStartTime = trip.arrivalTime().plusHours(2);
            findRoutesBacktrack(nextDest.cityName(), nextMinStartTime, destinations, index + 1, currentRoute, validRoutes);
            currentRoute.remove(currentRoute.size() - 1);
        }
    }
}
