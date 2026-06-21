package in.gov.cgg.ticketbookingsystem.utility;

import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Route;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TripRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    @Mapping(target = "busId", ignore = true)
    Bus toEntity(BusRequest busRequest);

    BusResponse toResponse(Bus bus, List<Seat> seats);

    @Mapping(target = "routeId", ignore = true)
    Route toEntity(RouteRequest routeRequest);

    RouteResponse toResponse(Route route);

    @Mapping(target = "tripId", ignore = true)
    TripSchedule toEntity(TripRequest tripRequest);

    TripResponse toResponse(TripSchedule tripSchedule);

}