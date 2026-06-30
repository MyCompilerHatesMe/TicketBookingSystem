package in.gov.cgg.ticketbookingsystem;

import in.gov.cgg.ticketbookingsystem.model.core.Route;
import in.gov.cgg.ticketbookingsystem.model.core.RouteStop;
import in.gov.cgg.ticketbookingsystem.repository.RouteRepo;
import in.gov.cgg.ticketbookingsystem.repository.RouteStopRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class TicketBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBookingSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(RouteRepo routeRepo, RouteStopRepo routeStopRepo) {
        return args -> {
            Route route = routeRepo.findById(1L).orElse(null);
            if (route != null && routeStopRepo.findByRouteRouteId(1L).isEmpty()) {
                RouteStop stop1 = new RouteStop(null, route, "Ameerpet", "BOARDING", 0, 1);
                RouteStop stop2 = new RouteStop(null, route, "Gachibowli", "BOARDING", 30, 2);
                RouteStop stop3 = new RouteStop(null, route, "LB Nagar", "BOARDING", 60, 3);
                RouteStop stop4 = new RouteStop(null, route, "Hebbal", "DROPPING", 540, 4);
                RouteStop stop5 = new RouteStop(null, route, "Majestic", "DROPPING", 570, 5);
                RouteStop stop6 = new RouteStop(null, route, "Silk Board", "DROPPING", 600, 6);

                routeStopRepo.saveAll(List.of(stop1, stop2, stop3, stop4, stop5, stop6));
                System.out.println("Successfully seeded default boarding and dropping stops for Route 1 (Hyd -> Bang)");
            }
        };
    }

}
