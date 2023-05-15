package com.gradigi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestPassChange {
	private String userName;
	private String oldPassword;
	private String newPassword;
}
