package jwzp.wp.VetApp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;


@EnableScheduling
@SpringBootApplication
public class VetAppApplication implements CommandLineRunner {

	private static final Logger LOGGER = LogManager.getLogger(VetAppApplication.class);

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		LOGGER.info("Starting app");
		LOGGER.warn("Starting app");
		LOGGER.trace("ELO");
		LOGGER.error("ELO");
		LOGGER.error("ELO");
		LOGGER.error("ELO");
		LOGGER.error("ELO");
		LOGGER.error("ELO");

		int i = 10;
		while (i-- > 0) {
			LOGGER.debug(i);
		}
//		return;
//		SpringApplication.run(VetAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
