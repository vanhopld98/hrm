package vn.com.humanresourcesmanagement.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.com.humanresourcesmanagement.common.exception.AuthenticationException;
import vn.com.humanresourcesmanagement.common.redis.TokenService;
import vn.com.humanresourcesmanagement.common.utils.JWTUtils;
import vn.com.humanresourcesmanagement.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecuredAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredAspect.class);
    public static final String AUTHORIZATION = "Authorization";

    private final TokenService tokenService;

    @Before(value = "@annotation(vn.com.humanresourcesmanagement.aop.Secured)")
    public void before(JoinPoint joinPoint) {
        try {
            /* Lấy ra danh sách roles được chỉ định ở API */
            var roles = getCurrentRoles(joinPoint);

            /* Lấy token từ redis. Token này được lưu khi khác hàng đăng nhập thành công */
            var tokenRedis = tokenService.get(JWTUtils.getUsername()).trim();

            /* Lấy token từ header */
            var tokenHeaders = JWTUtils.getCurrentToken();

            /* Lấy danh sách role từ token */
            var rolesCurrent = JWTUtils.getRoles(tokenHeaders);

            /* Nếu không có role nào thì chỉ cần kiểm tra xem có trùng token được lưu ở Redis hay không */
            if (CollectionUtils.isEmpty(roles)) {
                checkEqualsToken(tokenRedis, tokenHeaders);
            } else {
                /* Trường hợp có phân quyền role chỉ định thì cần xem user có đúng quyền hay không */
                for (String role : rolesCurrent) {
                    if (roles.contains(role)) {
                        checkEqualsToken(tokenRedis, tokenHeaders);
                        break;
                    } else {
                        throw new AuthenticationException("Bạn không có quyền truy cập vào chức năng này", 403);
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            LOGGER.error("Unauthorized. {}", e.getMessage());
            throw new AuthenticationException();
        }
    }

    private static void checkEqualsToken(String token, String tokenCurrent) {
        /* Nếu không có token được lưu ở redis hoặc token trên header thì sẽ throw */
        if (StringUtils.isNullOrEmpty(token) || StringUtils.isNullOrEmpty(tokenCurrent)) {
            throw new AuthenticationException();
        }

        /* Nếu token trên header và token trong redis không trùng nhau cũng throw */
        if (!tokenCurrent.equals(token)) {
            throw new AuthenticationException();
        }
    }

    private List<String> getCurrentRoles(JoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();
        var secured = method.getAnnotation(Secured.class);
        return secured.roles().getValueAsList();
    }

}
