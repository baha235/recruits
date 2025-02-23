package com.talhanation.recruits.network;

import com.talhanation.recruits.TeamEvents;
import com.talhanation.recruits.config.RecruitsModConfig;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

public class MessageCreateTeam implements Message<MessageCreateTeam> {

    private String teamName;
    private ItemStack banner;

    public MessageCreateTeam(){
    }

    public MessageCreateTeam(String name, ItemStack banner) {
        this.teamName = name;
        this.banner = banner;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel world = player.getLevel();
        TeamEvents.createTeam(context.getSender(), world, this.teamName, player.getName().getString(), this.banner);
    }

    public MessageCreateTeam fromBytes(FriendlyByteBuf buf) {
        this.teamName = buf.readUtf();
        this.banner = buf.readItem();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.teamName);
        buf.writeItemStack(this.banner, false);
    }
}
