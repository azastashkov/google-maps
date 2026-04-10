package com.google.maps.maptile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TileInfo {
    private String tileId;
    private int x;
    private int y;
}
