package org.se.outl.Controller;


import io.jsonwebtoken.Claims;
import org.se.outl.util.JwtUtils;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    @GetMapping
    public Result getIdentity(@RequestHeader("Authorization") String token)
    {
        token = token.replace("Bearer ", "");
        Claims claims = JwtUtils.parseJwt(token);
        String identity = claims.get("identity", String.class);
        return Result.success(identity);

    }
}
