package gm.url.shortening.entidad;

import com.fasterxml.jackson.annotation.JsonView;
import gm.url.shortening.view.Views;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Url {
    @JsonView(Views.Public.class)
    Integer id;

    @Id
    @JsonView(Views.Public.class)
    String shortCode;

    @JsonView(Views.Public.class)
    String url;

    @JsonView(Views.Public.class)
    String createdAt;

    @JsonView(Views.Public.class)
    String updatedAt;

    @JsonView(Views.Private.class)
    Integer accessCount = 0;

}


