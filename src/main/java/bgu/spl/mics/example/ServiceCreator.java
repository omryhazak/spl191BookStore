package bgu.spl.mics.example;

public interface ServiceCreator {
    MicroService create(String name, String[] args);
}
