package me.lntricate.intricarpet.mixins.interactions;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.level.ServerChunkCache;

//#if MC >= 11800
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import com.llamalad7.mixinextras.injector.WrapWithCondition;
//$$ import me.lntricate.intricarpet.interactions.Interaction;
//$$ import me.lntricate.intricarpet.interfaces.IChunkMap;
//$$ import net.minecraft.server.level.ChunkMap;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.NaturalSpawner.SpawnState;
//$$ import net.minecraft.world.level.chunk.LevelChunk;
//#endif

//#if MC >= 12100
//$$ import java.util.List;
//#endif

//#if MC >= 12105
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import java.util.function.Consumer;
//#endif

// On MC < 1.18 the mob spawning and random tick calls live inside a synthetic lambda in
// ServerChunkCache#tickChunks, which has no Mojang name to target. Those two conditions are
// applied on the callee side instead, see NaturalSpawnerMixin and ServerLevelMixin.
@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin
{
  //#if MC >= 11800
  //$$ @Final
  //$$ @Shadow
  //$$ public ChunkMap chunkMap;
  //$$ @Unique
  //$$ private static final String targetMethod =
    //#if MC >= 12105
    //$$ "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V";
    //#elseif MC >= 12102
    //$$ "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;JLjava/util/List;)V";
    //#else
    //$$ "tickChunks()V";
    //#endif
    //#if MC >= 12105
    //$$ @WrapWithCondition(method = "tickSpawningChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;Ljava/util/List;)V"))
    //$$ private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, List c)
    //#elseif MC >= 12102
    //$$ @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;Ljava/util/List;)V"))
    //$$ private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, List c)
    //#else
    //$$ @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)V"))
    //$$ private boolean shouldSpawnMobs(ServerLevel a, LevelChunk levelChunk, SpawnState b, boolean c, boolean d, boolean e)
    //#endif
  //$$ {
  //$$   return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.MOBSPAWNING);
  //$$ }
    //#if MC >= 12105
    //$$ @Redirect(
    //$$         method = targetMethod,
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/server/level/ChunkMap;forEachBlockTickingChunk(Ljava/util/function/Consumer;)V"
    //$$         )
    //$$ )
    //$$ private void redirectForEachBlockTickingChunk(ChunkMap chunkMapInstance, Consumer<LevelChunk> originalConsumer) {
    //$$   Consumer<LevelChunk> wrapper = (levelChunk) -> {
    //$$     try {
    //$$       boolean should = ((IChunkMap)chunkMapInstance)
    //$$               .anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.RANDOMTICKS);
    //$$       if (should) {
    //$$         originalConsumer.accept(levelChunk);
    //$$       }
    //$$     } catch (Throwable t) {
    //$$       t.printStackTrace();
    //$$     }
    //$$   };
    //$$   chunkMapInstance.forEachBlockTickingChunk(wrapper);
    //$$ }
    //#else
    //$$ @WrapWithCondition(method = targetMethod, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V"))
    //$$ private boolean shouldRandomTick(ServerLevel instance, LevelChunk levelChunk, int i)
    //$$ {
    //$$   return ((IChunkMap)chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.RANDOMTICKS);
    //$$ }
    //#endif
  //#endif
}
