package core;

public interface SpaceLoader <T>{
    SpaceComponent<T> load() throws Exception;
}