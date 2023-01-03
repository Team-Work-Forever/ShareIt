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

    public NavigationHelper() {
        navigationStack = new LinkedList<>();
    }

    public void navigateTo(Class<?> controller) throws IOException {

        navigationStack.add(controller);

        ((ControllerBase)applicationContext.getBean(controller)).display();

    }

    public void navigateBack() throws IOException {

        if (navigationStack.isEmpty()) return;

        var controller = navigationStack.pollFirst();

        ((ControllerBase)applicationContext.getBean(controller)).display();

    }

}
