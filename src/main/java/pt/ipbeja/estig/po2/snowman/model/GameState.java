package pt.ipbeja.estig.po2.snowman.model;

import java.util.List;


public record GameState(
        List<List<PositionContent>> board,
        List<Snowball> snowballs,
        Monster monster
) {}