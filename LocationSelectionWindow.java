import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocationSelectionWindow extends JFrame {
    private static String selectedLocation;

    public LocationSelectionWindow(JFrame parent) {
        setTitle("选择会议室");
        setSize(400, 400);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] rooms = {"会议室1", "会议室2", "会议室3", "会议室4", "会议室5", "会议室6", "会议室7", "会议室8", "会议室9", "会议室10"};

        for (String room : rooms) {
            JButton button = new JButton(room);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedLocation = room;
                    dispose();
                }
            });
            add(button);
        }
    }

    public static String getSelectedLocation() {
        return selectedLocation;
    }
}
