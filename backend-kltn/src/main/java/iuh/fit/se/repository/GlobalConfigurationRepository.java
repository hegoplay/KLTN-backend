package iuh.fit.se.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.se.entity.GlobalConfiguration;

public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, String> {
	Optional<GlobalConfiguration> findByConfigKey(String key);

}
