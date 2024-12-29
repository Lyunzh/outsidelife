package org.se.outl.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.se.outl.Entity.Team;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto extends Team {
    private int isJoined;
}
