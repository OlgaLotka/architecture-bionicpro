package org.example.domain;

import lombok.Data;

@Data
public class Credentials {

    private String login;
    private String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
