package NAND;

public abstract class AbstractSsdOperator implements ReadWritable {
    protected NandDriver nandDriver;

    public abstract static class Builder<T extends Builder<T>> {
        protected NandDriver nandDriver;

        public T nandDriver(NandDriver nandDriver) {
            this.nandDriver = nandDriver;
            return self();
        }

        protected abstract T self();

        public abstract AbstractSsdOperator build();
    }

    protected AbstractSsdOperator(Builder<?> builder) {
        this.nandDriver = builder.nandDriver;
    }
}
