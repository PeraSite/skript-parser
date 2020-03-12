package io.github.syst3ms.skriptparser.premade.types;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.util.DummyPlayer;
import io.github.syst3ms.skriptparser.util.Priority;
import io.github.syst3ms.skriptparser.util.RegisterPriority;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RegisterPriority(priority = Priority.LOWEST)
public class TypePlayer extends Type<Player> {

    static {
        Main.getMainRegistration().addType(new TypePlayer());
    }

    public TypePlayer() {
        super(Player.class, "player", "player@s");
    }

    @Override
    public @Nullable Function<String, ? extends Player> getLiteralParser() {
        return name -> {
            ArrayList<Player> onlinePlayers = new ArrayList<>(getOnlinePlayers());
            Optional<Player> any = onlinePlayers.stream().filter(player -> player.getName().equals(name)).findAny();
            return any.orElse(null);
        };
    }

    @Override
    public Function<Player, String> getToStringFunction() {
        return Player::getName;
    }


    public static List<Player> getOnlinePlayers() {
        return Arrays.asList(
                new DummyPlayer("PeraSite"),
                new DummyPlayer("HeartPattern"),
                new DummyPlayer("EntryPoint")
        );
    }
}