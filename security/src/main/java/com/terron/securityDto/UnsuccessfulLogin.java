package com.terron.securityDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnsuccessfulLogin {

    private LocalDateTime date;

    private String message;

    private String status;

    private String path;
}
