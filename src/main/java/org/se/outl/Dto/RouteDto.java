package org.se.outl.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.se.outl.Entity.Node;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {
    private  int routeId;
    private  String time;
    private  String description;
    private  String difficulty;
    private  List<NodeDto> nodes;
}
