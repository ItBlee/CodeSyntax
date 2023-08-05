package Client;

import Client.gui.MainFrame;
import com.apple.eawt.Application;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //System.err.close();

        //Khởi tạo giao diện
        FlatIntelliJLaf.setup();
        FlatLightLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        FlatIntelliJLaf.installLafInfo();
        FlatDarculaLaf.installLafInfo();
        try {
            Image image = ImageIO.read(new File("image/icon.png"));
            Application.getApplication().setDockIconImage(image);
        } catch (Exception ignored) {}


        Client.frame = new MainFrame();
        java.awt.EventQueue.invokeLater(() -> Client.frame.setVisible(true));

        //Cấu trúc Client và kết nối tới Server
        try {
            Client.connect();
        } catch (IOException | NullPointerException ignored) { //Nếu kết nói thất bại
            Client.frame.appendProcess(Client.FAIL_CONNECT);
            Client.frame.appendProcess("Click RUN to reconnect !");
            Client.frame.setEnabled(true);
        }

        if (Client.checkConnection()) {
            Client.frame.appendProcess(Client.SUCCESS_CONNECT);
            Client.frame.setEnabled(true);
        }
    }
}
