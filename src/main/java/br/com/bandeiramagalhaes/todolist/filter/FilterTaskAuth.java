package br.com.bandeiramagalhaes.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.bandeiramagalhaes.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
      var servletPath = request.getServletPath();

      if (servletPath.startsWith("/tasks/")) {
        var authorization = request.getHeader("Authorization");

        var authEncoded = authorization.substring("Basic ".length()).trim();
        byte[] authDecoded = java.util.Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecoded);

        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        var userExists = this.userRepository.findByUsername(username);
        if (userExists == null) {
          response.sendError(401);
        } else {
          var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userExists.getPassword());
          if (passwordVerify.verified) {
            request.setAttribute("userId", userExists.getId());
            filterChain.doFilter(request, response);
          } else {
            response.sendError(401);
          }
        }
      } else {
        filterChain.doFilter(request, response);
      }
  }
}
