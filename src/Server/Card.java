package Server;


import java.util.ArrayList;
import java.util.Random;


public class Card {

    private static String[] currentPosition = {
                "H7", "H8", "H9", "HX", "HB", "HD", "HK", "HA",
                "C7", "C8", "C9", "CX", "CB", "CD", "CK", "CA",
                "P7", "P8", "P9", "PX", "PB", "PD", "PK", "PA",
                "K7", "K8", "K9", "KX", "KB", "KD", "KK", "KA"
        };


    private static ArrayList<String> kartenstapel = new ArrayList<String>();
    private static void test() {
        for (int i = 0; i > 32; i++) {
            kartenstapel.add("leer");
        }
    }

    static Random random = new Random();

    public static void main(String[] args) {
        test();
        mixCards();
        System.out.println(kartenstapel);
    }


    private static void mixCards(){
        for (int i = 0; i < 32; i++) {
            int rand;
            while (true) {
                rand = random.nextInt(32);
                if (kartenstapel.get(rand) == null) {
                    kartenstapel.set(rand, currentPosition[i]);
                    break;
                }
            }
        }
    }


    }


//    private String[] newPosition = new String[32];
//    Random random = new Random();
//
//
//    private void mixCards(){
//        for (int i = 0; i < 32; i++) {
//            int rand;
//            while (true) {
//                rand = random.nextInt(32);
//                if (newPosition[rand] == null) {
//                    newPosition[rand] = currentPosition[i];
//                    break;
//                }
//            }
//        }
//    }



