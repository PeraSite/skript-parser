package io.github.syst3ms.skriptparser.premade.types;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.util.Priority;
import io.github.syst3ms.skriptparser.util.RegisterPriority;

@RegisterPriority(priority = Priority.LOWEST)
public class TypeString extends Type<String> {
    static {
        Main.getMainRegistration().addType(new TypeString());
    }

    public TypeString() {
        super(String.class, "string", "string@s");
    }


}
