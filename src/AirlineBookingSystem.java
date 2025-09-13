import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class AirlineBookingSystem extends JFrame {
    private static final int ROWS = 6;
    private static final int COLS = 4;
    private Map<String, Seat[][]> flightSeats = new HashMap<>();
    private java.util.List<Booking> bookings = new ArrayList<>();
    private String currentFlight;

    // GUI components
    private JTextField nameField, passportField, mobileField, emailField;
    private JComboBox<String> flightBox;
    private JTextArea bookingArea;
    private JButton[][] seatButtons = new JButton[ROWS][COLS];
    private JLabel selectedSeatLabel;
    private JButton bookButton;

    private String selectedSeat = null;

    public AirlineBookingSystem() {
        setTitle("Airline Ticket Booking System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(null);

        // Initialize flights and seats
        String[] flights = {"AI101: Delhi → Mumbai", "AI202: Mumbai → Bangalore", "AI303: Bangalore → Kolkata"};
        for (String flight : flights) {
            Seat[][] seats = new Seat[ROWS][COLS];
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    seats[i][j] = new Seat((i + 1) + "" + (char) ('A' + j));
                }
            }
            flightSeats.put(flight, seats);
        }

        // Flight selection
        flightBox = new JComboBox<>(flights);
        currentFlight = (String) flightBox.getSelectedItem();
        flightBox.addActionListener(e -> updateSeatDisplay());

        // Passenger info
        nameField = new JTextField(15);
        passportField = new JTextField(10);
        mobileField = new JTextField(10);
        emailField = new JTextField(20);

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Passenger & Flight Details"));
        topPanel.add(new JLabel("Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Passport No:"));
        topPanel.add(passportField);
        topPanel.add(new JLabel("Mobile:"));
        topPanel.add(mobileField);
        topPanel.add(new JLabel("Email:"));
        topPanel.add(emailField);
        topPanel.add(new JLabel("Flight:"));
        topPanel.add(flightBox);

        // Seat grid
        JPanel seatPanel = new JPanel(new GridLayout(ROWS, COLS, 5, 5));
        seatPanel.setBorder(BorderFactory.createTitledBorder("Select Seat"));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.GREEN);
                final int row = i, col = j;
                btn.addActionListener(e -> selectSeat(row, col));
                seatButtons[i][j] = btn;
                seatPanel.add(btn);
            }
        }
        updateSeatDisplay(); // Initialize seat buttons

        // Booking area
        bookingArea = new JTextArea(10, 40);
        bookingArea.setEditable(false);

        // Action panel
        JPanel actionPanel = new JPanel();
        selectedSeatLabel = new JLabel("Selected Seat: None");
        bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(e -> bookTicket());
        actionPanel.add(selectedSeatLabel);
        actionPanel.add(bookButton);

        // Assemble UI
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(seatPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(bookingArea), BorderLayout.EAST);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void updateSeatDisplay() {
        currentFlight = (String) flightBox.getSelectedItem();
        Seat[][] seats = flightSeats.get(currentFlight);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                seatButtons[i][j].setText(seats[i][j].seatNo);
                seatButtons[i][j].setBackground(seats[i][j].isBooked ? Color.RED : Color.GREEN);
                seatButtons[i][j].setEnabled(!seats[i][j].isBooked);
            }
        }
    }

    private void selectSeat(int row, int col) {
        Seat[][] seats = flightSeats.get(currentFlight);
        if (seats[row][col].isBooked) {
            JOptionPane.showMessageDialog(this, "Seat already booked!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedSeat = seats[row][col].seatNo;
        selectedSeatLabel.setText("Selected Seat: " + selectedSeat);
    }

    private void bookTicket() {
        String name = nameField.getText().trim();
        String passport = passportField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || passport.isEmpty() || mobile.isEmpty() || email.isEmpty() || selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // OTP Verification (send real email)
        int otp = new Random().nextInt(900000) + 100000;
        boolean otpSent = EmailSender.sendOTPEmail(email, otp);
        if (!otpSent) {
            JOptionPane.showMessageDialog(this, "Failed to send OTP email. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String input = JOptionPane.showInputDialog("Enter the OTP sent to your email:");
        if (input == null || !input.equals(String.valueOf(otp))) {
            JOptionPane.showMessageDialog(this, "OTP verification failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Payment Processing (simulate)
        String payment = (String) JOptionPane.showInputDialog(
                this,
                "Select payment method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Credit Card", "Debit Card", "UPI", "Net Banking"},
                "Credit Card"
        );
        if (payment == null) {
            JOptionPane.showMessageDialog(this, "Payment cancelled", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Finalize booking
        Seat[][] seats = flightSeats.get(currentFlight);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (seats[i][j].seatNo.equals(selectedSeat)) {
                    seats[i][j].isBooked = true;
                    seatButtons[i][j].setBackground(Color.RED);
                    seatButtons[i][j].setEnabled(false);
                }
            }
        }

        Booking booking = new Booking(name, passport, mobile, email, currentFlight, selectedSeat, payment);
        bookings.add(booking);

        String bookingText = booking.toString();
        bookingArea.append("Booking Confirmed:\n" + bookingText + "\n\n");

        // Send email with QR code
        boolean emailSent = EmailSender.sendBookingConfirmationWithQR(email, bookingText);

        if (emailSent) {
            JOptionPane.showMessageDialog(this, "Booking confirmed and email sent with QR code!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Booking confirmed but failed to send email.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        // Reset form
        selectedSeat = null;
        selectedSeatLabel.setText("Selected Seat: None");
        nameField.setText("");
        passportField.setText("");
        mobileField.setText("");
        emailField.setText("");
    }

    static class Seat {
        String seatNo;
        boolean isBooked = false;
        Seat(String seatNo) { this.seatNo = seatNo; }
    }

    static class Booking {
        String name, passport, mobile, email, flight, seatNo, payment;
        Booking(String name, String passport, String mobile, String email, String flight, String seatNo, String payment) {
            this.name = name;
            this.passport = passport;
            this.mobile = mobile;
            this.email = email;
            this.flight = flight;
            this.seatNo = seatNo;
            this.payment = payment;
        }
        public String toString() {
            return String.format(
                    "Passenger: %s\nPassport: %s\nMobile: %s\nEmail: %s\nFlight: %s\nSeat: %s\nPayment: %s\n",
                    name, passport, mobile, email, flight, seatNo, payment
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AirlineBookingSystem().setVisible(true));
    }
}
