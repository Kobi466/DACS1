package server;

import java.net.ServerSocket;

public class FindPort {
    public static void main(String[] args) {
        int startPort =1024;
        int endPort =10000;
        try{
            for(int i=startPort; i<=endPort; i++){
                if(checkPort(i)==true){
                    System.out.println(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPort(int i) {
        try{
            ServerSocket ss = new ServerSocket(i);
            ss.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
