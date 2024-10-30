package com.icet.controllers;
import com.icet.dto.AuthenticationRequest;
import com.icet.dto.AuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.icet.utils.JwtUtil;

import java.io.IOException;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public AuthenticationResponse createAuthenticatiionToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws IOException {
     try {
         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
     } catch (BadCredentialsException e){
         throw new BadCredentialsException("Incorrect Username or password");
     }catch (DisabledException disabledException){
         response.sendError(HttpServletResponse.SC_NOT_FOUND,"User is not created");
         return null;
     }
     final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
     final String jwt = jwtUtil.generateToken(userDetails.getUsername());
     return new AuthenticationResponse(jwt);
    }
}
