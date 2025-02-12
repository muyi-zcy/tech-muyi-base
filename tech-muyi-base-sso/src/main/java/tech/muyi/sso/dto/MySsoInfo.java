package tech.muyi.sso.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MySsoInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ssoId;

    private String ssoName;

    private String token;

    private LocalDateTime loginTime;

    private LocalDateTime expirationTime;

    private String cacheValue;

    private String source;

    private String tenantId;

    private String currentTenantId;

    private List<String> includeRoute;

    private List<String> excludeRoute;
}
