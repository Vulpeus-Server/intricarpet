package me.lntricate.intricarpet.mixins.interactions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.lntricate.intricarpet.interactions.Interaction;
import me.lntricate.intricarpet.interfaces.IServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;

//#if MC < 11800
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.lntricate.intricarpet.interfaces.IChunkMap;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import net.minecraft.world.level.chunk.LevelChunk;
//#endif

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin
{
  @Redirect(method = "Lnet/minecraft/world/level/NaturalSpawner;spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(DDDDZ)Lnet/minecraft/world/entity/player/Player;"))
  private static Player getNearestPlayer(ServerLevel self, double a, double b, double c, double d, boolean e)
  {
    return self.getNearestPlayer(a, b, c, d, entity -> !(entity instanceof IServerPlayer player && !player.getInteraction(Interaction.MOBSPAWNING)));
  }

  //#if MC < 11800
  // The only vanilla caller, ServerChunkCache#tickChunks, invokes this from a synthetic lambda that
  // has no Mojang name to target, so the condition is applied on this side instead.
  @Inject(method = "spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)V", at = @At("HEAD"), cancellable = true)
  private static void shouldSpawnMobs(ServerLevel level, LevelChunk levelChunk, SpawnState a, boolean b, boolean c, boolean d, CallbackInfo ci)
  {
    if(!((IChunkMap)level.getChunkSource().chunkMap).anyPlayerCloseWithInteraction(levelChunk.getPos(), Interaction.MOBSPAWNING))
      ci.cancel();
  }
  //#endif
}
