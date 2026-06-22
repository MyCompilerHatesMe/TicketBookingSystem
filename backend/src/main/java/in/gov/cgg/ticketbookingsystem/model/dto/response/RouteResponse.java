package in.gov.cgg.ticketbookingsystem.model.dto.response;

public record RouteResponse (
        int routeId,
        String sourceCity,
        String destinationCity,
        int distanceKm
)
{}
