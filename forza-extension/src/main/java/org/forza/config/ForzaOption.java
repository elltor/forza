package org.forza.config;


import java.util.Objects;

public class ForzaOption<T> {
    private final String name;
    private T defaultValue;
    private Class<? extends ForzaOption> type;

    protected ForzaOption(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = getClass();
    }

    public String name() {
        return this.name;
    }

    public Class<? extends ForzaOption> type() {
        return this.type;
    }

    protected void setType(Class<? extends ForzaOption> type) {
        this.type = type;
    }

    public T defaultValue() {
        return this.defaultValue;
    }

    public static <T> ForzaOption<T> valueOf(String name) {
        return new ForzaOption<T>(name, null);
    }

    public static <T> ForzaOption<T> valueOf(String name, T defaultValue) {
        return new ForzaOption<T>(name, defaultValue);
    }

    public static <T> ForzaOption<T> valueOf(Class<? extends ForzaOption> type, String name) {
        ForzaOption<T> option = new ForzaOption<>(name, null);
        option.setType(type);
        return option;
    }

    public static <T> ForzaOption<T> valueOf(Class<? extends ForzaOption> type, String name, T defaultValue) {
        ForzaOption<T> option = new ForzaOption<>(name, defaultValue);
        option.setType(type);
        return option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ForzaOption<?> that = (ForzaOption<?>) o;
        return name == null ? Objects.equals(name, that.name) : name == null;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
}
