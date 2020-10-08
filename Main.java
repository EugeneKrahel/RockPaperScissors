
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Main {

    private static final int INVALID_INPUT = -1;
    private static final int EXIT_MOVE = 0;

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> moves = Arrays.asList(args);
        if (isInputValid(moves)) {
            System.out.println("Error");
            System.exit(0);
        }
        String hmacKey = getHMACKey();
        int pcMove = new Random().nextInt(moves.size());

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = bytesToHex(sha256_HMAC.doFinal(moves.get(pcMove).getBytes()));
        System.out.println("HMAC: " + hash);



        int playerMove = getPlayerMove(moves);
        System.out.println(String.format("Your move: %s", moves.get(playerMove)));
        System.out.println(String.format("Computer move: %s", moves.get(pcMove)));
        if (playerMove == pcMove){
            System.out.println("Draw");
        } else if ((pcMove < playerMove && playerMove <= (pcMove + moves.size()/2)) || (pcMove > moves.size()/2 && 0 <= playerMove && playerMove < moves.size() - pcMove)){
            System.out.println("You win!");
        } else System.out.println("Computer win!");
        System.out.println("HMAC key: " + hmacKey);

    }

    private static boolean isInputValid(final List<String> moves) {
        HashSet<String> set = new HashSet<>(moves);
        return (moves.size() < 3 || moves.size() % 2 == 0 || set.size() < moves.size());
    }

    private static String getHMACKey() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] values = new byte[32];
        random.nextBytes(values);
        StringBuilder sb = new StringBuilder();
        for (byte b : values) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static int getPlayerMove(final List<String> moves) {
        int move;
        do {
            printMenu(moves);
            move = readPlayerMove();
            if (move == EXIT_MOVE) {
                System.exit(1);
            }
        } while (move == INVALID_INPUT || move >= moves.size());
        return move - 1;
    }

    private static int readPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            return INVALID_INPUT;
        }
        return scanner.nextInt();
    }

    private static void printMenu(final List<String> moves) {
        System.out.println("Available moves:");
        for (int i = 0; i < moves.size(); i++) {
            System.out.println(String.format("%d - %s", i + 1, moves.get(i)));
        }
        System.out.println(String.format("%d - %s", EXIT_MOVE, "Exit"));
        System.out.print("Enter your move: ");
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
