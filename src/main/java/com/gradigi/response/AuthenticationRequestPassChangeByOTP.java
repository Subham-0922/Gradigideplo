package com.gradigi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestPassChangeByOTP {
	private String email;
	private int otp;
	
	private String newPassword;
}