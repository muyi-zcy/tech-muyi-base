package tech.muyi.sso.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MySsoInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ssoId;

    private String ssoName;

    private String token;

    private Long loginTime;

    private Long expirationTime;

    private String cacheValue;

    private List<String> includeRoute;

    private List<String> excludeRoute;
}
