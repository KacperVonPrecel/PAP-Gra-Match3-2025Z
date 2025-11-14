package pap.project.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pap.project.auth.model.RegisterResult;
import pap.project.auth.model.controller.login.LoginRequest;
import pap.project.auth.model.controller.login.LoginResponse;
import pap.project.auth.model.controller.register.RegisterError;
import pap.project.auth.model.controller.register.RegisterErrorResponse;
import pap.project.auth.model.controller.register.RegisterRequest;
import pap.project.auth.model.controller.register.RegisterResponse;

import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private static final String LOG_PREFIX = "authController{%d}";
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    private final @NonNull AuthenticationManager authenticationManager;
    private final @NonNull RegisterService registerService;
    private final @NonNull SecurityContextRepository securityContextRepository;

    public AuthController(@NonNull AuthenticationManager authenticationManager, @NonNull RegisterService registerService,
                          @NonNull SecurityContextRepository securityContextRepository)
    {
        this.authenticationManager = authenticationManager;
        this.registerService = registerService;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("login")
    public @NonNull ResponseEntity<?> login(@NonNull @Valid @RequestBody LoginRequest request,
                                   @NonNull HttpServletRequest httpRequest,
                                   @NonNull HttpServletResponse httpResponse)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = LOG_PREFIX.formatted(requestId);
        LOG.info("%s new login request for user: %s".formatted(logPrefix, request.username()));
        try
        {
            final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            createSession(authentication, httpRequest, httpResponse);
            LOG.info("%s new login successful".formatted(logPrefix));
            return ResponseEntity.ok(new LoginResponse());
        } catch (AuthenticationException wyj)
        {
            LOG.info("%s login request failed".formatted(logPrefix), wyj);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid login data");
        }
    }

    @PostMapping("register")
    public @NonNull ResponseEntity<?> register(@NonNull @Valid @RequestBody RegisterRequest registerRequest)
    {
        final int requestId = REQUEST_ID.getAndIncrement();
        final String logPrefix = LOG_PREFIX.formatted(requestId);
        LOG.info("%s new register request for user: %s %s".formatted(logPrefix, registerRequest.username(), registerRequest.email()));
        final RegisterResult result = registerService.registerUser(logPrefix, registerRequest);
        LOG.info("%s register ended result: %s".formatted(logPrefix, result.name()));

        return switch (result)
        {
            case REGISTERED -> ResponseEntity.ok(new RegisterResponse());
            case USERNAME_REPEATED -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterErrorResponse(RegisterError.USERNAME_TAKEN));
            case EMAIL_REPEATED -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterErrorResponse(RegisterError.EMAIL_TAKEN));
            case DATABASE_ERROR -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterErrorResponse(RegisterError.INTERNAL_SERVER_ERROR));
        };
    }

    private void createSession(@NonNull Authentication authentication,
            @NonNull HttpServletRequest httpRequest,
            @NonNull HttpServletResponse httpResponse)
    {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }
}
