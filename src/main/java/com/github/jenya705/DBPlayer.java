package com.github.jenya705;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class DBPlayer {

    private UUID uuid;
    private String name;

    public DBPlayer(UUID uuid, String name){
        setUuid(uuid);
        setName(name);
    }

    public DBPlayer(Player player){
        this(player.getUniqueId(), player.getName());
    }

}
