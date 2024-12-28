package org.se.outl.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Route {
    private  int routeId;
    private  String time;
    private  String description;
    private String difficulty;
}
