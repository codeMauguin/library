package app.pojo;

import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Admin_user extends User {

  @Serial
  private static final long serialVersionUID = 1361978392181652585L;

  private Integer borrowed;
  private Long cardId;
  private Integer total;
}
