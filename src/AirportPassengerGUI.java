import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AirportPassengerGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbairport";
    private static final String USER = "root";
    private static final String PASSWORD = "cos87Tvor!k";

    private JButton viewDataButton, addPassengerButton, deletePassengerButton, addAircraftButton, deleteAircraftButton, addAirportButton, deleteAirportButton;

    public AirportPassengerGUI() {
        setTitle("Airport Management System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        viewDataButton = new JButton("VIEW ALL DATA");
        viewDataButton.addActionListener(e -> displayAllData());

        addPassengerButton = new JButton("ADD PASSENGER");
        addPassengerButton.addActionListener(e -> addRecord("passengers", new String[]{"Name", "Seat Number"}));

        deletePassengerButton = new JButton("DELETE PASSENGER");
        deletePassengerButton.addActionListener(e -> deleteRecord("passengers", "Passenger ID"));

        addAircraftButton = new JButton("ADD AIRCRAFT");
        addAircraftButton.addActionListener(e -> addRecord("aircrafts", new String[]{"Aircraft Name", "Capacity"}));

        deleteAircraftButton = new JButton("DELETE AIRCRAFT");
        deleteAircraftButton.addActionListener(e -> deleteRecord("aircrafts", "Aircraft ID"));

        addAirportButton = new JButton("ADD AIRPORT");
        addAirportButton.addActionListener(e -> addRecord("airports", new String[]{"Airport Name", "Location"}));

        deleteAirportButton = new JButton("DELETE AIRPORT");
        deleteAirportButton.addActionListener(e -> deleteRecord("airports", "Airport ID"));

        add(viewDataButton);
        add(addPassengerButton);
        add(deletePassengerButton);
        add(addAircraftButton);
        add(deleteAircraftButton);
        add(addAirportButton);
        add(deleteAirportButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayAllData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            StringBuilder data = new StringBuilder();


            ResultSet rs = stmt.executeQuery("SELECT * FROM dbairport.passengers");
            data.append("PASSENGERS:\n");
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Seat: ").append(rs.getInt("seat_number"))
                        .append("\n");
            }
            data.append("\n");


            rs = stmt.executeQuery("SELECT * FROM dbairport.aircrafts");
            data.append("AIRCRAFTS:\n");
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("aircraft_name"))
                        .append(", Capacity: ").append(rs.getInt("capacity"))
                        .append("\n");
            }
            data.append("\n");


            rs = stmt.executeQuery("SELECT * FROM dbairport.airports");
            data.append("AIRPORTS:\n");
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("airport_name"))
                        .append(", Location: ").append(rs.getString("location"))
                        .append("\n");
            }


            JTextArea textArea = new JTextArea(20, 50);
            textArea.setText(data.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JFrame dataFrame = new JFrame("All Data");
            dataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dataFrame.add(scrollPane);
            dataFrame.pack();
            dataFrame.setLocationRelativeTo(null);
            dataFrame.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    private void addRecord(String tableName, String[] fields) {
        JPanel panel = new JPanel(new GridLayout(fields.length, 2));
        JTextField[] textFields = new JTextField[fields.length];

        for (int i = 0; i < fields.length; i++) {
            panel.add(new JLabel(fields[i] + ":"));
            textFields[i] = new JTextField(10);
            panel.add(textFields[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Record", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {

                String sql = "";
                if (tableName.equals("passengers")) {
                    sql = "INSERT INTO dbairport.passengers (name, seat_number) VALUES (?, ?)";
                } else if (tableName.equals("aircrafts")) {
                    sql = "INSERT INTO dbairport.aircrafts (aircraft_name, capacity) VALUES (?, ?)";
                } else if (tableName.equals("airports")) {
                    sql = "INSERT INTO dbairport.airports (airport_name, location) VALUES (?, ?)";
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].toLowerCase().contains("capacity") || fields[i].toLowerCase().contains("seat")) {
                            pstmt.setInt(i + 1, Integer.parseInt(textFields[i].getText()));
                        } else {
                            pstmt.setString(i + 1, textFields[i].getText());
                        }
                    }
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record added successfully.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input format. Please enter correct values.");
            }
        }
    }

    private void deleteRecord(String tableName, String idLabel) {
        String id = JOptionPane.showInputDialog(null, "Enter " + idLabel + " to delete:");
        if (id != null && !id.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {

                pstmt.setInt(1, Integer.parseInt(id));
                int rowsDeleted = pstmt.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, idLabel + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "No record found with the provided " + idLabel + ".");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a number.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AirportPassengerGUI::new);
    }
}

