package me.lntricate.intricarpet.mixins.interactions;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.lntricate.intricarpet.interactions.Interaction;
import me.lntricate.intricarpet.interfaces.IChunkMap;
import me.lntricate.intricarpet.interfaces.IServerPlayer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

//#if MC >= 12105
//$$ import net.minecraft.world.phys.Vec3;
//#endif

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements IChunkMap
{
  //#if MC >= 12105
  //$$ @Shadow private static double euclideanDistanceSquared(ChunkPos chunkPos, Vec3 vec){return 0.0;}
  //#else
  @Shadow private static double euclideanDistanceSquared(ChunkPos chunkPos, Entity entity){return 0.0;}
  //#endif
  @Shadow @Final private PlayerMap playerMap;

  private boolean playerValid(ServerPlayer player, ChunkPos chunkPos, Interaction interaction)
  {
    //#if MC >= 12105
    //$$ return ((IServerPlayer)player).getInteraction(interaction) && euclideanDistanceSquared(chunkPos, player.position()) < 16384d;
    //#else
    return ((IServerPlayer)player).getInteraction(interaction) && euclideanDistanceSquared(chunkPos, player) < 16384d;
    //#endif
  }

  @Override
  public boolean anyPlayerCloseWithInteraction(ChunkPos chunkPos, Interaction interaction)
  {
    //#if MC >= 11800
      //#if MC >= 12002
      //$$ for(ServerPlayer player : playerMap.getAllPlayers())
      //#else
      //$$ for(ServerPlayer player : playerMap.getPlayers(chunkPos.toLong()))
      //#endif
    //$$   if(playerValid(player, chunkPos, interaction))
    //$$     return true;
    //$$ return false;
    //#else
    return playerMap.getPlayers(chunkPos.toLong()).anyMatch(player -> playerValid(player, chunkPos, interaction));
    //#endif
  }

  @Inject(method = "skipPlayer", at = @At("HEAD"), cancellable = true)
  private void skipPlayer(ServerPlayer player, CallbackInfoReturnable<Boolean> cir)
  {
    if(!((IServerPlayer)player).getInteraction(Interaction.CHUNKLOADING))
      cir.setReturnValue(true);
  }
}
