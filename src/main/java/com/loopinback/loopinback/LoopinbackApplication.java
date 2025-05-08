package com.loopinback.loopinback;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.loopinback.loopinback.model.User;
import com.loopinback.loopinback.repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class LoopinbackApplication {

	public static final String DB_URL = "DB_URL";
	public static final String DB_USER = "DB_USER";

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		if (dotenv.get(DB_URL) == null || dotenv.get(DB_USER) == null) {
			System.err.println("⚠️ Variables de entorno esenciales no están definidas correctamente.");
		}

		System.setProperty(DB_URL, dotenv.get(DB_URL, ""));
		System.setProperty(DB_USER, dotenv.get(DB_USER, ""));
		System.setProperty("DB_PASS", dotenv.get("DB_PASS", ""));
		System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY", ""));
		System.setProperty("spring.application.name", dotenv.get("SPRING_APPLICATION_NAME", "loopinback"));

		// Activar perfil si está definido
		String activeProfile = dotenv.get("SPRING_PROFILES_ACTIVE");
		if (activeProfile != null && !activeProfile.isBlank()) {
			System.setProperty("spring.profiles.active", activeProfile);
		}

		SpringApplication.run(LoopinbackApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initDatabase(UserRepository userRepository, BCryptPasswordEncoder encoder) {
		return args -> {
			Dotenv dotenv = Dotenv.load();

			if (userRepository.findByUsername(dotenv.get("ADMIN_USERNAME")).isEmpty()) {
				User admin = User.builder()
						.username(dotenv.get("ADMIN_USERNAME"))
						.email(dotenv.get("ADMIN_EMAIL"))
						.password(encoder.encode(dotenv.get("ADMIN_PASSWORD")))
						.build();
				userRepository.save(admin);
			}

			if (userRepository.findByUsername(dotenv.get("USER_USERNAME")).isEmpty()) {
				User user = User.builder()
						.username(dotenv.get("USER_USERNAME"))
						.email(dotenv.get("USER_EMAIL"))
						.password(encoder.encode(dotenv.get("USER_PASSWORD")))
						.build();
				userRepository.save(user);
			}
		};
	}
}
