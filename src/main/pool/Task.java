package main.pool;

import main.model.Model;

public interface Task {
    Model[] parent = new Model[0];
    void run();
}
