package shareit.view;

import static shareit.utils.ScreenUtils.clear;

import java.io.IOException;

import shareit.utils.ScreenUtils;

public class Menu {

    private String[] options;
    private int selectedIndex;

    public Menu(String[] options)
    {
        this.options = options;
    }
    
    private void displayOptions()
    {
        System.out.println();

        for (int i = 0; i < (options.length); i++)
        {
            String currentOption = options[i];
            
            if (i == selectedIndex)
            {
                System.out.print(ScreenUtils.BLACK);
                System.out.print(ScreenUtils.WHITE);
            }
            else
            {
                System.out.print(ScreenUtils.WHITE);
                System.out.print(ScreenUtils.BLACK);
            }

            System.out.println("<< " + currentOption + " >>");
        }

        ScreenUtils.resetColors();
    }

    public int run() throws IOException
    {
        int keyPressed;

        do
        {
            clear();
            displayOptions();

            keyPressed = System.in.read();

            if (keyPressed == 38)
            {
                selectedIndex --;

                if (selectedIndex == -1)    
                {
                    selectedIndex = options.length - 1;
                }
            }
            else if (keyPressed == 40)
            {
                selectedIndex ++;

                if (selectedIndex == options.length)
                {
                    selectedIndex = 0;
                }
            }

        } while(keyPressed != 13);

        return selectedIndex;

    }


}
