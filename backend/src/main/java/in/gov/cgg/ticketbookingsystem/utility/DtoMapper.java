package in.gov.cgg.ticketbookingsystem.utility;

import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Route;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TripRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RegisterResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteStopResponse;
import in.gov.cgg.ticketbookingsystem.model.core.RouteStop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    @Mapping(target = "busId", ignore = true)
    Bus toEntity(BusRequest busRequest);

    BusResponse toResponse(Bus bus, List<Seat> seats);

    @Mapping(target = "routeId", ignore = true)
    @Mapping(target = "stops", ignore = true)
    Route toEntity(RouteRequest routeRequest);

    RouteResponse toResponse(Route route);

    @Mapping(target = "busId", source = "tripSchedule.bus.busId")
    @Mapping(target = "busNumber", source = "tripSchedule.bus.busNumber")
    @Mapping(target = "busName", source = "tripSchedule.bus.busName")
    @Mapping(target = "busType", source = "tripSchedule.bus.busType")
    @Mapping(target = "routeId", source = "tripSchedule.route.routeId")
    @Mapping(target = "sourceCity", source = "tripSchedule.route.sourceCity")
    @Mapping(target = "destinationCity", source = "tripSchedule.route.destinationCity")
    @Mapping(target = "stops", expression = "java(mapStops(tripSchedule))")
    TripResponse toResponse(TripSchedule tripSchedule);

    RegisterResponse toResponse(AuthUser saved);

    default List<RouteStopResponse> mapStops(TripSchedule tripSchedule) {
        if (tripSchedule.getRoute() == null || tripSchedule.getRoute().getStops() == null) {
            return java.util.Collections.emptyList();
        }
        return tripSchedule.getRoute().getStops().stream()
                .map(stop -> new RouteStopResponse(
                        stop.getRouteStopId(),
                        stop.getStopName(),
                        stop.getStopType(),
                        stop.getMinutesOffset(),
                        stop.getSequence(),
                        tripSchedule.getStartTime().plusMinutes(stop.getMinutesOffset())
                ))
                .sorted(java.util.Comparator.comparing(RouteStopResponse::sequence))
                .toList();
    }
}