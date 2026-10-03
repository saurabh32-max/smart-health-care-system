package com.healthcare.auth.controllers;

import com.healthcare.auth.payload.request.LoginRequest;
import com.healthcare.auth.payload.request.SignupRequest;
import com.healthcare.auth.payload.response.JwtResponse;
import com.healthcare.auth.payload.response.MessageResponse;
import com.healthcare.auth.services.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
		return ResponseEntity.ok(jwtResponse);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		MessageResponse response = authService.registerUser(signUpRequest);

		if (response.message().startsWith("Error:")) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}
}
