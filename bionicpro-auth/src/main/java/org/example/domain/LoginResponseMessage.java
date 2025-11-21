package org.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseMessage implements Serializable {

    @JsonProperty("sid")
    private String sessionId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("expiresIn")
    private Date expiresIn;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("refreshExpiresIn")
    private Date refreshExpiresIn;
}
