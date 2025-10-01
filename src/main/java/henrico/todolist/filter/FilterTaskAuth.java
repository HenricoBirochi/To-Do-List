package henrico.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import henrico.todolist.user.IUserRepository;
import henrico.todolist.user.UserModel;
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

        // Verifica o path da requisicao para evitar que a criacao de novos usuarios seja impedida
        String servletPath = request.getServletPath();
        if(servletPath.equals("/tasks/")){
            // Pegar a autenticacao (usuario e senha)
            String authorization = request.getHeader("Authorization");
            String authEncoded = authorization.substring(5).trim(); // O "trim()" tira todos os espacos(se tiver espaco(s)) no comeco da palavra
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            String authString = new String(authDecode);
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // Validar usuario
            UserModel user = this.userRepository.findByUsername(username);
            if (user == null)
                    response.sendError(401);
            else{
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if(!passwordVerify.verified)
                    response.sendError(401);
                else{ // Segue viagem
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                }
            }
        }
        else
            filterChain.doFilter(request, response);
    }

}
