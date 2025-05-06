package com.itj.blockcert.DTO;

public class LoginResponse {
	private String msg;
    private String role;

    public LoginResponse(String msg, String role) {
        this.msg = msg;
        this.role = role;
    }

    public String getMsg() {
        return msg;
    }

    public String getRole() {
        return role;
    }
}
