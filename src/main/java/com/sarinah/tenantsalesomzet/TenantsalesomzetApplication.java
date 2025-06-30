package com.sarinah.tenantsalesomzet;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.EnableTokenSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTokenSecurity
public class TenantsalesomzetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantsalesomzetApplication.class, args);
	}

}
