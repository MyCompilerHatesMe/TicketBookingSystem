package in.gov.cgg.ticketbookingsystem;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled to avoid dependency on running PostgreSQL during test execution")
class TicketBookingSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
