package com.zuehlke.securesoftwaredevelopment.config;

import com.zuehlke.securesoftwaredevelopment.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class AuditLogger {
    public static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");
    private final Logger log;

    public static AuditLogger getAuditLogger(Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        return new AuditLogger(logger);
    }

    public void auditWho(String description) {
        log.info(AUDIT, "userId={} - {}", getUserId(), description);
    }

    public void auditWho(int userId, String description) {
        log.info(AUDIT, "userId={} - {}", userId, description);
    }

    public void auditChange(EntityChanged entityChanged) {
        auditWho("Change " + entityChanged.toString());
    }


    private AuditLogger(Logger log) {
        this.log = log;
    }

    private Integer getUserId() {
        User user = SecurityUtil.getCurrentUser();
        Integer retId = null;
        if (user != null) {
            retId = user.getId();
        } else {
            log.info("Current user is null");
        }
        return retId;
    }

}
