package main.tasks;

import main.form.Form;

public interface Task {
    Form[] parent = new Form[0];
    void run();
}
