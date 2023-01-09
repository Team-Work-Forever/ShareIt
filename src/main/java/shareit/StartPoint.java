package shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class StartPoint {
    
    private static ConfigurableApplicationContext applicationContext;

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(TerminalSpringApplication.class, args);
    }

}
