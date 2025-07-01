package com.sarinah.tenantsalesomzet;

import com.sarinah.tenantsalesomzet.apisecurity.tokensecurity.annotation.EnableTokenSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableTokenSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class TenantsalesomzetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantsalesomzetApplication.class, args);
	}

}
