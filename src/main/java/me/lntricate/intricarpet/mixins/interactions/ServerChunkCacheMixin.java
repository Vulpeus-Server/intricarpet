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

//#if MC >= 12100
//$$ import java.util.List;
//#endif

//#if MC >= 12105
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import java.util.function.Consumer;
//#endif

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin
{
  @Final
  @Shadow
  public ChunkMap chunkMap;

  @Unique
  private static final String targetMethod =
  //#if MC >= 12105
  //$$ "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V";
  //#elseif MC >= 12102
  //$$ "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;JLjava/util/List;)V";
  //#elseif MC >= 11800
  //$$ "tickChunks()V";
  //#else
    "method_20801";
  //#endif

  //#if MC >= 12105
  //$$ @WrapWithCondition(method = "tickSpawningChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;Ljava/util/List;)V"))
  //$$ private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, List c)
  //#elseif MC >= 12102
  //$$ @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;Ljava/util/List;)V"))
  //$$ private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, List c)
  //#else
  @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)V"))
  private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, boolean c, boolean d, boolean e)
  //#endif
  {
    return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.MOBSPAWNING);
  }

  //#if MC >= 12105
  //$$ @Redirect(
  //$$         method = targetMethod,
  //$$         at = @At(
  //$$                 value = "INVOKE",
  //$$                 target = "Lnet/minecraft/server/level/ChunkMap;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V"
  //$$         )
  //$$ )
  //$$ private void redirectForEachBlockTickingChunk(ChunkMap chunkMapInstance, Consumer<LevelChunk> originalConsumer) {
  //$$   IChunkMap chunkMap = (IChunkMap)chunkMapInstance;
  //$$   Consumer<LevelChunk> wrapper = (levelChunk) -> {
  //$$     try {
  //$$       boolean should = chunkMap
  //$$               .anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.RANDOMTICKS);
  //$$       if (should) {
  //$$         originalConsumer.accept(levelChunk);
  //$$       }
  //$$     } catch (Throwable t) {
  //$$       t.printStackTrace();
  //$$     }
  //$$   };
  //$$   chunkMap.forEachBlockTickingChunk(wrapper);
  //$$ }
  //#else
  @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V"))
  private boolean shouldRandomTick(ServerLevel instance, LevelChunk levelChunk, int i)
  {
    return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.RANDOMTICKS);
  }
  //#endif
}
