package vn.com.humanresourcesmanagement.common.utils;

import com.auth0.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.com.humanresourcesmanagement.common.exception.AuthenticationException;
import vn.com.humanresourcesmanagement.common.model.payload.response.RolesUserResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JWTUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtils.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    public static final String PREFERRED_USERNAME = "preferred_username";

    public static String getUsername() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            var authorization = request.getHeader(AUTHORIZATION);
            var token = StringUtils.isNotNullOrEmpty(authorization) ? authorization.replace(BEARER, "") : "";
            var decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(PREFERRED_USERNAME).asString();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> getRoles(String tokenCurrent) {
        try {
            var token = tokenCurrent.startsWith(BEARER) ? tokenCurrent.replace(BEARER, "") : tokenCurrent;
            var decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("realm_access").as(RolesUserResponse.class).getRoles().stream().filter(role -> role.startsWith("ROLE")).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static String getCurrentToken() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                var request = servletRequestAttributes.getRequest();
                var authorization = request.getHeader(AUTHORIZATION);
                return StringUtils.isNotNullOrEmpty(authorization) ? authorization.replace(BEARER, "") : "";
            }
        } catch (Exception e) {
            LOGGER.error("[SECURE] Get Current Token Exception: {}", e.getMessage());
            throw new AuthenticationException();
        }
        return "";
    }

    public static Date getExpiredTime(String tokenCurrent) {
        try {
            var token = tokenCurrent.startsWith(BEARER) ? tokenCurrent.replace(BEARER, "") : tokenCurrent;
            var dJWT = JWT.decode(token);
            return dJWT.getClaim("exp").asDate();
        } catch (Exception e) {
            return new Date();
        }
    }

}
