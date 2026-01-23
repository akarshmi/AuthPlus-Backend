package com.auth.AuthPlus;

import com.auth.AuthPlus.configs.AppConstants;
import com.auth.AuthPlus.entities.Role;
import com.auth.AuthPlus.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;


@SpringBootApplication
public class AuthPlusApplication implements CommandLineRunner {

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthPlusApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		roleRepository.findByName("ROLE_"+AppConstants.ROLE_ADMIN).ifPresentOrElse(role -> {

		},()->{
			Role role = new Role();
			role.setName("ROLE_"+AppConstants.ROLE_ADMIN);
			role.setRoleId(UUID.randomUUID());
			roleRepository.save(role);
		});
		roleRepository.findByName("ROLE_"+AppConstants.ROLE_USER).ifPresentOrElse(role -> {

		},()->{
			Role role = new Role();
			role.setName("ROLE_"+AppConstants.ROLE_USER);
			role.setRoleId(UUID.randomUUID());
			roleRepository.save(role);

		});

	}
}
