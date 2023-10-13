package br.com.eduardomeurer.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.eduardomeurer.todolist.user.IUserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var serveltPath = request.getServletPath();

        if (serveltPath.startsWith("/tasks/")) {
            // Pegar a autenticação

            var auth = request.getHeader("Authorization");
            System.out.println(auth);

            var userPass = auth.substring("Basic".length()).trim();

            byte[] authDecoded = Base64.getDecoder().decode(userPass);

            var authString = new String(authDecoded);

            System.out.println(userPass);
            System.out.println(authString);
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println(username);
            System.out.println(password);
            // Validar usuário

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "Usuário sem autorização");
            } else {

                // Validar Senha

                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401, "Usuário sem autorização");
                }
                // Segue viagem
            }
        }else{
            filterChain.doFilter(request, response);
        }

    }

}
