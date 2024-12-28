package org.se.outl.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    private int teamId;
    private String status;
    private String description;
    private Date startTime;
    private Date endTime;
    private int limitCnt;
    private int currentNum;
    private String teamName;
}
