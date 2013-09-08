package com.mojang.minecraft.render;

import com.mojang.minecraft.player.Player;
import java.util.Comparator;

public class ChunkDirtyDistanceComparator implements Comparator<Chunk> {
    private Player player;

    public ChunkDirtyDistanceComparator(Player player) {
	this.player = player;
    }

    @Override
    public int compare(Chunk chunk, Chunk other) {
	if (chunk.visible || !other.visible) {
	    if (other.visible) {
		float sqDist = chunk.distanceSquared(player);
		float otherSqDist = other.distanceSquared(player);

		if (sqDist == otherSqDist) {
		    return 0;
		} else if (sqDist > otherSqDist) {
		    return -1;
		} else {
		    return 1;
		}
	    } else {
		return 1;
	    }
	} else {
	    return -1;
	}
    }
}
