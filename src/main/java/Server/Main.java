package Server;

import Server.gui.ServerManagerGUI;
import com.apple.eawt.Application;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        try {
            Image image = ImageIO.read(new File("image/server_icon.png"));
            Application.getApplication().setDockIconImage(image);
        } catch (Exception ignored) {}

        ServerManagerGUI.AuthenticationFrame();
        Server.managerFrame = new ServerManagerGUI();
    }
}
