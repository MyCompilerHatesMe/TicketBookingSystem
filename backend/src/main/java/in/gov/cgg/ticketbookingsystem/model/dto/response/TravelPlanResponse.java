package in.gov.cgg.ticketbookingsystem.model.dto.response;

public record TravelPlanResponse(
    ItineraryOption cheapestRoute,
    ItineraryOption shortestDistanceRoute
) {}
