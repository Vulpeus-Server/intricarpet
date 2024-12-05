package me.lntricate.intricarpet.mixins.interactions;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import me.lntricate.intricarpet.interactions.Interaction;
import me.lntricate.intricarpet.interfaces.IChunkMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin
{
  @Final
  @Shadow
  public ChunkMap chunkMap;

  @Unique
  private static final String targetMethod =
  //#if MC >= 11800
  //$$ "tickChunks()V";
  //#else
    "method_20801";
  //#endif

  @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)V"))
  private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, boolean c, boolean d, boolean e)
  {
    return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.MOBSPAWNING);
  }

  @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V"))
  private boolean shouldRandomTick(ServerLevel instance, LevelChunk levelChunk, int i)
  {
    return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.RANDOMTICKS);
  }
}
