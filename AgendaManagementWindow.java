import javax.swing.*;

public class AgendaManagementWindow extends JFrame {
    private int currentUserId;

    public AgendaManagementWindow(int userId) {
        this.currentUserId = userId;
        setTitle("议程管理");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Implement agenda management functionality here
    }
}
