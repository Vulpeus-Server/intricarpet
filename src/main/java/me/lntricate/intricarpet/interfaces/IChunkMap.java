package me.lntricate.intricarpet.interfaces;

import java.util.function.Consumer;

import me.lntricate.intricarpet.interactions.Interaction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public interface IChunkMap
{
  public boolean anyPlayerCloseWithInteraction(ChunkPos chunkPos, Interaction interaction);
}
