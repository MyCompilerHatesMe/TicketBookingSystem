package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.TooManySeatsException;
import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import in.gov.cgg.ticketbookingsystem.repository.BusRepo;
import in.gov.cgg.ticketbookingsystem.repository.SeatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final BusRepo busRepo;
    private final SeatRepo seatRepo;
    private final DtoMapper dtoMapper;

    public BusResponse addBus (BusRequest busRequest) {

        if (busRequest.totalSeats() > 52)
            throw new TooManySeatsException(String.valueOf(busRequest.totalSeats()));

        Bus bus = dtoMapper.toEntity(busRequest);

        List<Seat> seats = new ArrayList<>();

        int seatsPerRow = 2;
        for (int i = 0; i < bus.getTotalSeats(); i++) {
            char rowLetter = (char) ('A' + i/seatsPerRow);
            int seatNum = (i % seatsPerRow) + 1;

            String seatNumber = "" + rowLetter + seatNum;

            Seat seat = new Seat();
            seat.setBus(bus);
            seat.setSeatNumber(seatNumber);

            Seat savedSeat = seatRepo.save(seat);
            savedSeat.setSeatId(null);
            seats.add(savedSeat);

        }

        return dtoMapper.toResponse(busRepo.save(bus), seats);
    }
}
