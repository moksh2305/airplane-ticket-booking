import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailSender {
    private static final String FROM_EMAIL = "moksh230305@gmail.com";
    private static final String PASSWORD = "muug pqkm tnhg ozay";

    // For sending OTP email
    public static boolean sendOTPEmail(String toEmail, int otp) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your OTP for Airline Booking");
            message.setText("Your OTP for booking verification is: " + otp);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // For sending booking confirmation with QR code
    public static boolean sendBookingConfirmationWithQR(String toEmail, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            // Generate QR code image
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(text, 300, 300);

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            baos.flush();
            byte[] imageInBytes = baos.toByteArray();
            baos.close();

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Booking Confirmation with QR Code");

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text);
            multipart.addBodyPart(textPart);

            // Image part
            MimeBodyPart imagePart = new MimeBodyPart();
            ByteArrayDataSource ds = new ByteArrayDataSource(imageInBytes, "image/png");
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setFileName("booking_qr.png");
            multipart.addBodyPart(imagePart);

            // Set content
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
