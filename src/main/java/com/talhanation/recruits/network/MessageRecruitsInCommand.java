package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageRecruitsInCommand implements Message<MessageRecruitsInCommand> {

    private int count;
    private UUID uuid;

    public MessageRecruitsInCommand(){
    }

    public MessageRecruitsInCommand(UUID uuid) {
        this.count = 0;
        this.uuid = uuid;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context){
        List<AbstractRecruitEntity> list = Objects.requireNonNull(context.getSender()).level.getEntitiesOfClass(AbstractRecruitEntity.class, context.getSender().getBoundingBox().inflate(64.0D));
        for (AbstractRecruitEntity recruits : list){
            if (recruits.getOwnerUUID() != null && recruits.getOwnerUUID().equals(this.uuid)) {
                this.count++;
                CommandEvents.setRecruitsInCommand(recruits, this.count);
                //recruits.getOwner().sendSystemMessage(Component.literal("MESSAGE int: " + count));
            }

        }

    }
    public MessageRecruitsInCommand fromBytes(FriendlyByteBuf buf) {
        this.count = buf.readInt();
        this.uuid = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(count);
        buf.writeUUID(uuid);
    }

}