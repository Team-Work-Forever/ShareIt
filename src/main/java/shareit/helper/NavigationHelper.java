package shareit.helper;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import shareit.controllers.ControllerBase;

public class NavigationHelper {
    
    @Autowired
    private ApplicationContext applicationContext;

    private Deque<Class<?>> navigationStack;

    private Class<?> currentController;

    public NavigationHelper() {
        navigationStack = new LinkedList<>();
    }

    public void setFirstEntry(Class<?> controller) throws IOException {
        iniController(controller);
    }

    public void navigateTo(Class<?> controller) throws IOException {

        navigationStack.add(currentController);

        iniController(controller);

    }

    public void navigateBack() throws IOException {

        if (navigationStack.isEmpty()) return;

        var controller = navigationStack.pollLast();

        iniController(controller);

    }

    private void iniController(Class<?> controller) throws IOException {
        this.currentController = controller;
        ((ControllerBase)applicationContext.getBean(controller)).display();
    }

}
