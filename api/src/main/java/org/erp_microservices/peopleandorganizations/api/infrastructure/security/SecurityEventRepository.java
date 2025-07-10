package org.erp_microservices.peopleandorganizations.api.infrastructure.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, UUID> {

    Page<SecurityEvent> findAllByOrderByTimestampDesc(Pageable pageable);

    List<SecurityEvent> findByUsernameOrderByTimestampDesc(String username);

    List<SecurityEvent> findByEventTypeOrderByTimestampDesc(SecurityEventType eventType);

    List<SecurityEvent> findByIpAddressOrderByTimestampDesc(String ipAddress);

    List<SecurityEvent> findByTimestampBetweenOrderByTimestampDesc(Instant start, Instant end);

    long countByEventTypeAndTimestampAfter(SecurityEventType eventType, Instant after);

    long countByUsernameAndEventTypeAndTimestampAfter(String username, SecurityEventType eventType, Instant after);
}
