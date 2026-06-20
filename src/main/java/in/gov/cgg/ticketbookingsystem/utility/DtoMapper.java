package in.gov.cgg.ticketbookingsystem.utility;

import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    @Mapping(target = "busId", ignore = true)
    Bus toEntity(BusRequest busRequest);

    BusResponse toResponse(Bus bus, List<Seat> seats);
}