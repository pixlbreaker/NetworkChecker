package networkchecker;
import java.net.URL;
import java.io.*;
import java.net.MalformedURLException;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkChecker {

    public static void main(String[] args) throws IOException {
        // Gets the IP addresses
        String ExternalIP = getIPAddress();
        String CachedIP = getCachedIP();
        
        if (!ExternalIP.equals(CachedIP)){
            System.out.println("The IP addresses are different...");
            updateCache(ExternalIP);
            sendEmail(ExternalIP);
        } else {
            System.out.println("The IP addresses are the same");
        }
    }
    
    /**
     * Gets the IP Address
     * @return
     * @throws IOException 
     */
    public static String getIPAddress()throws IOException{
        String ip =  null;
        try {
            // Goes to checkIP service
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            
            // Gets the Ip
            ip = in.readLine(); //you get the IP as a String
            System.out.println("The external IP address was returned as " + ip);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ip;
    }
    
    /***
     * Gets the Cached IP address
     * @return
     * @throws IOException 
     */
    public static String getCachedIP() throws IOException{
        
        // Variables
        String ip = null;
        String filename = "ipaddress.txt";
        try{
            FileReader file = new FileReader(filename);
            
            // If the file doesn't exist
            if(!file.ready()){
                System.out.println("Null File. Writing to a new file");
                writeCache(ip);
            } else {
                // Gets the IP from the file
                BufferedReader in = new BufferedReader(file);
                ip = in.readLine();
                System.out.println("The file read IP address was returned as " + ip);
                in.close();
            }
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return ip;
    }
    
    /***
     * Updates the Cached Value in the txt file
     * @param ip 
     */
    public static void updateCache(String ip){
        String filename = "ipaddress.txt";
        
        try{
            
            // Writes to the file
            FileWriter writer = new FileWriter(filename);
            writer.write(ip);
            System.out.println("The file has been updated with the ip " + ip);
            writer.close();
            
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }
    
    /***
     * Writes a new file named ipaddress.txt
     * @param ip 
     */
    public static void writeCache(String ip){
        String filename = "ipaddress.txt";
        
        try{
            
            // Creates a new file and writes to it
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.write(ip);
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkChecker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /***
     * Sends an email from the raspberry pi development account
     * @param ip 
     */
    public static void sendEmail(String ip){
        
        // Variables
        String username = "******";
        String recipname = "********";
        String password = "******";
        
        // Properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        // Session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try{
            // New Message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipname));
            message.setSubject("IP Address Change");
            message.setText(ip);
            
            // Send Message
            Transport.send(message);
            System.out.println("Email sent from " + username);
        }catch(MessagingException e){
            System.out.println(e.toString());
        }
    }
}
