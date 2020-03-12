package io.github.syst3ms.skriptparser.premade.types;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.util.Priority;
import io.github.syst3ms.skriptparser.util.RegisterPriority;

@RegisterPriority(priority = Priority.LOWEST)
public class TypeObject extends Type<Object> {
    static {
        Main.getMainRegistration().addType(new TypeObject());
    }

    public TypeObject() {
        super(Object.class, "object", "object@s");
    }


}
