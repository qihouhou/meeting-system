import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardWindow extends JFrame {
    private int currentUserId;

    public DashboardWindow(int userId) {
        this.currentUserId = userId;
        setTitle("欢迎使用会议管理系统");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("欢迎使用会议管理系统", SwingConstants.CENTER);
        add(welcomeLabel);

        JButton meetingManagementButton = new JButton("会议管理");
        meetingManagementButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MainWindow(currentUserId).setVisible(true);
                dispose();
            }
        });
        add(meetingManagementButton);

        JButton personalInfoButton = new JButton("个人信息");
        personalInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PersonalInfoWindow(currentUserId).setVisible(true);
            }
        });
        add(personalInfoButton);

        JButton agendaManagementButton = new JButton("议程管理");
        agendaManagementButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AgendaManagementWindow(currentUserId).setVisible(true);
            }
        });
        add(agendaManagementButton);

        JButton meetingInfoButton = new JButton("会议信息");
        meetingInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MeetingInfoWindow(currentUserId).setVisible(true);
            }
        });
        add(meetingInfoButton);
    }
}
