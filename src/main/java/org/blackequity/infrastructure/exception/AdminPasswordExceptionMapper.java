package org.blackequity.infrastructure.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.blackequity.application.service.AdminPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Provider
public class AdminPasswordExceptionMapper implements ExceptionMapper<AdminPasswordException> {

    private static final Logger logger = LoggerFactory.getLogger(AdminPasswordExceptionMapper.class);

    @Override
    public Response toResponse(AdminPasswordException exception) {
        logger.warn("Acceso denegado: {}", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
            .entity(Map.of(
                "error", "ADMIN_PASSWORD_INVALID",
                "message", exception.getMessage()
            ))
            .build();
    }
}
