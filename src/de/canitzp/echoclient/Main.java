package de.canitzp.echoclient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * This is a "Echo Client" application which takes three arguments:
 * - host: can be a hostname or a ip address
 * - port
 * - data
 * and sends a UDP DatagramPacket with the given data to the specified echo server.
 * Afterwards it's receiving the answer and prints it to the screen.
 * <p>
 * Made by canitzp on 24 of September 2020 as a class project
 */
public class Main {
    
    public static void main(String[] args){
        // define "bright green" as default console color
        System.out.print("\033[92m");
        
        // test if enough arguments are given
        if(args.length >= 3){
            // unwrap the three arguments needed
            String host = args[0];
            String portString = args[1];
            String data = args[2];
            
            // test if the given port is parsable to Integer
            if(!isInteger(portString)){
                System.err.printf("The given port is not a valid number! '%s'\n", portString);
                return;
            }
            
            int port = Integer.parseInt(portString);
            // test if the given port isn't in the valid port range (0...65535)
            if(port < 0 || port > 65535){
                System.err.printf("The given port is out of range! '%d'\n", port);
                return;
            }
            
            // create a InetAddress object from the hostname and throw an error if something doesn't work, like hostname to ip resolution
            InetAddress address;
            try{
                address = InetAddress.getByName(host);
            } catch(UnknownHostException e){
                System.err.printf("The given hostname/ip is not valid! '%s'\n", host);
                return;
            }
            
            System.out.printf("===== Starting to send UDP Echo Packet to '%s:%d' with data '%s' =====\n", host, port, data);
            
            try{
                // create the datagram socket object to send and receive packets on
                DatagramSocket datagramSocket = new DatagramSocket();
                // convert the data string to a byte array with UTF-8 encoding chosen
                byte[] dataToSend = data.getBytes(StandardCharsets.UTF_8);
                // send the data byte array over the created socket
                createAndSendUDPPacket(datagramSocket, address, port, dataToSend);
                
                System.out.println("Data was send.");
                
                // receive a data array over the socket
                byte[] bytes = receiveUDPPacket(datagramSocket, dataToSend.length);
                // convert the received byte array to a readable string with UTF-8 encoding
                String stringReceived = new String(bytes, StandardCharsets.UTF_8);
                
                System.out.println("Data was received.");
                System.out.printf("===== Received Data: '%s' =====\n", stringReceived);
            } catch(SocketException e){
                System.err.println("Could not open socket!");
            } catch(IOException e){
                System.err.println("Packet error occurred!");
            }
        } else{
            System.err.println("No arguments given! Should be <host> <port> <data>. The host can be a hostname or an ip address.");
        }
    }
    
    /**
     * @param socket  DatagramSocket to send the packet
     * @param address InetAddress the packet should be send to
     * @param port    Port as integer
     * @param data    The actual data to send as byte[]
     * @throws IOException If any error occurs while sending the packet
     */
    private static void createAndSendUDPPacket(DatagramSocket socket, InetAddress address, int port, byte[] data) throws IOException{
        DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length, address, port);
        socket.send(datagramPacket);
    }
    
    /**
     * @param socket     The DatagramSocket used to send the packet
     * @param dataLength The length of data we should receive
     * @return A byte array containing the data of the received DatagramPacket
     *
     * @throws IOException If any error occurs while receiving the packet
     */
    private static byte[] receiveUDPPacket(DatagramSocket socket, int dataLength) throws IOException{
        DatagramPacket datagramPacket = new DatagramPacket(new byte[dataLength], dataLength);
        socket.receive(datagramPacket);
        return datagramPacket.getData();
    }
    
    /**
     * @param string Input string to test if it is a integer.
     * @return True if the inputted string is a valid and parsable integer.
     */
    private static boolean isInteger(String string){
        try{
            Integer.parseInt(string);
            return true;
        } catch(NumberFormatException ignored){
            return false;
        }
    }
    
}
