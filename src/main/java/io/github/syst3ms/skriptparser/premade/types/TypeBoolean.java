package io.github.syst3ms.skriptparser.premade.types;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.util.Priority;
import io.github.syst3ms.skriptparser.util.RegisterPriority;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@RegisterPriority(priority = Priority.LOWEST)
public class TypeBoolean extends Type<Boolean> {

    static {
        Main.getMainRegistration().addType(new TypeBoolean());
    }


    public TypeBoolean() {
        super(Boolean.class, "boolean", "boolean@s");
    }

    @Override
    public @Nullable Function<String, ? extends Boolean> getLiteralParser() {
        return s -> {
            if (s.equalsIgnoreCase("true")) {
                return true;
            } else if (s.equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        };
    }

    @Override
    public Function<Boolean, String> getToStringFunction() {
        return String::valueOf;
    }
}
