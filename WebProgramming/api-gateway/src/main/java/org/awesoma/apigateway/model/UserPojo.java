package org.awesoma.apigateway.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserPojo {
    private Long id;
    private String username;
    private String accessToken;

    public static UserPojo fromBigUser(BigUser bigUser) {
        return new UserPojo(
                bigUser.getUser().getId(),
                bigUser.getJwtUser().getUsername(),
                bigUser.getJwtUser().getAccessToken()
        );
    }

    public static String jsonFromBigUser(BigUser bigUser) throws JsonProcessingException {
        var pojo = fromBigUser(bigUser);
        return new ObjectMapper().writeValueAsString(pojo);
    }
}
