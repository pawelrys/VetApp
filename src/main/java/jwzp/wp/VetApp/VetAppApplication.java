package jwzp.wp.VetApp;

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

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(VetAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
