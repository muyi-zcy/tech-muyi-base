package tech.muyi.sso.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class MySsoInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ssoId;

    private String ssoName;

    private String token;

    private Long loginTime;

    private Long expirationTime;
}
