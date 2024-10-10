package github.kasuminova.novaeng.common.util;

import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class NonnullListHandle<E> extends NonNullList<E> {
    
    private Runnable onChanged = null;

    public NonnullListHandle() {
    }

    public NonnullListHandle(final List<E> delegateIn, @Nullable final E listType) {
        super(delegateIn, listType);
    }

    public NonnullListHandle<E> setOnChanged(final Runnable onChanged) {
        this.onChanged = onChanged;
        return this;
    }

    @Override
    public boolean add(final E element) {
        if (onChanged != null) {
            onChanged.run();
        }
        return super.add(element);
    }

    @Nonnull
    @Override
    public E set(final int index, @Nonnull final E element) {
        if (onChanged != null) {
            onChanged.run();
        }
        return super.set(index, element);
    }

    @Override
    public void add(final int index, @Nonnull final E element) {
        if (onChanged != null) {
            onChanged.run();
        }
        super.add(index, element);
    }

}
