package pw.prok.imagine.object.nbt;

import net.minecraft.nbt.NBTBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterHandler {
    Class<?> from();

    Class<? extends NBTBase> to();
}
