//Name: David DeCosta
/*Description:
        Implements a version of the Feistel/DES encryption algorithm to encrypt blocks of bits.
 */

 public class Main {
    public static void main(String[] args) {

        //Question 1:
        System.out.println("Question 1: ");
        String bitPattern1 = "1010";
        String currentKey1 = "0011";
        int currentRoundEven = 2; //even round
        String result1 = roundFunction(bitPattern1, currentKey1, currentRoundEven);
        System.out.println("Result Even: " + result1);
        String bitPattern2 = "1010";
        String currentKey2 = "0011";
        int currentRoundOdd = 3; //odd round
        String result2 = roundFunction(bitPattern2, currentKey2, currentRoundOdd);
        System.out.println("Result Odd : " + result2);

        //Question 2:
        System.out.println("\nQuestion 2: ");
        String anotherKey = "1011010011010010";
        String returnKey = keyShift(anotherKey);
        System.out.println("Orig   Key: " + anotherKey);
        System.out.println("Return Key: " + returnKey);

        //Question 3/4:
        String finalCipherText, finalKey;
        String arr[];
        System.out.println("\nQuestion 3/4: ");
        String plainText = "1011010011010010";
        String key =       "1101010110100110";
        arr = sixteenRoundEncryption(plainText,key,true);
        finalCipherText = arr[0];
        finalKey = arr[1];
        System.out.println("Orig Text :   " + plainText);
        System.out.println("Final Cipher: " + finalCipherText);
        System.out.println("Final Key   : " + finalKey);

        //Question 5:
        System.out.println("\nQuestion 5: ");
        reverseTheProcess(finalCipherText, finalKey);

        //Question 6:
        System.out.println("\nQuestion 6: ");
        String word32 = "10001000010000000010000000100001";
        String word64 = "0000000000000000000000000000000000000000000000000000000000000000";
        String keyfor32 = "11010101101001101101010110100110";
        String keyfor64 = "1101010110100110110101011010011011010101101001101101010110100110";
        testEffectivness(word32, keyfor32);
        testEffectivness(word64, keyfor64);
    }


    /*
            FOR EVEN**
        even(0) index key  0 = NO
        odd (1) index key  0 = flip 
        even(0) index key  1 = flip
        odd (1) index key  1 = NO

            FOR ODD**
        even(0) index key  0 = flip
        odd (1) index key  0 = no
        even(0) index key  1 = no
        odd (1) index key  1 = flip
     */
    public static String roundFunction(String bitPattern, String currentKey, int currentRound) {
        boolean isIndexEven;
        char keyNum;
        char[] bitPatternArray = bitPattern.toCharArray();
        char[] keyArray = currentKey.toCharArray();
    
        for (int i = 0; i < bitPatternArray.length; i++) { 
            isIndexEven = (i % 2) == 0;  //if i mod 2 = 0, its even, so bool will be true
            keyNum = keyArray[i];
            
            //is the current round even
            if (currentRound % 2 == 0) {
                if ((isIndexEven && keyNum == '1') || (!isIndexEven && keyNum == '0')) {
                    bitPatternArray[i] = changeChar(bitPatternArray[i]);
                }
            }
            //or is it odd
            else {
                if ((isIndexEven && keyNum == '0') || (!isIndexEven && keyNum == '1')) {
                    bitPatternArray[i] = changeChar(bitPatternArray[i]);
                }
            }
        }
    
        return new String(bitPatternArray);
    }
    

    public static char changeChar(char c) {
        if (c == '0') {
            return '1';
        } else {
            return '0';
        }
    }

    public static String keyShift(String key){
        String shiftedKey = new String();
        String leftSide;            //hold left side string
        String rightSide;           //hold right side string
        String leftSideBitsRemoved;  //hold left side bits removed
        String rightSideBitsRemoved;  //hold right side bits removed
        int bitsToTakeOff;            //how much to take off each side

        //make sure out keys are length atleast 16
        if(key.length() < 16){
            System.out.println("Key is not length 16");
            return key;
        }

        int halfKeyLength = key.length()/2;
        int result = (int)(Math.log(key.length()) / Math.log(2));  //gives me the n that the base is being raised to
        bitsToTakeOff = result - 4;                                  //just n -4
        bitsToTakeOff = (int)(Math.pow(2, bitsToTakeOff));

        //break into 2 halfs
        leftSide = key.substring(0, halfKeyLength);
        rightSide = key.substring(halfKeyLength);
        
        //remove the bits off the beggining of each side
        leftSideBitsRemoved = leftSide.substring(0, bitsToTakeOff);
        rightSideBitsRemoved = rightSide.substring(0,bitsToTakeOff);

        //add the left side back with the right side, and then put the bits that were removed back on
        shiftedKey = leftSide.substring(bitsToTakeOff) + rightSide.substring(bitsToTakeOff) + leftSideBitsRemoved + rightSideBitsRemoved;

        return shiftedKey;
    }
    
    public static String[] sixteenRoundEncryption(String bitPattern, String key, Boolean printOrNot){
        String myBitPattern = bitPattern;
        String myKey = key;
        String firstHalfCurrentKey;
        String l1;
        String leftSideBitPattern;
        String rightSideBitPattern;
        int numberOfRounds = 16;
        String l2;
        int halfKeySize = myKey.length()/2;
        int halfBitPatternSize = bitPattern.length()/2;

        for(int i = 0; i < numberOfRounds; i++){

            //split bitPattern into 2 parts
            leftSideBitPattern = myBitPattern.substring(0,halfBitPatternSize);
            rightSideBitPattern = myBitPattern.substring(halfBitPatternSize);

            //first half of current key 
            firstHalfCurrentKey = myKey.substring(0, halfKeySize);

            //throw in the round function left side with curr subkey to get L1
            l1= roundFunction(leftSideBitPattern, firstHalfCurrentKey, i);
            l2 = xor(l1, rightSideBitPattern);

            myBitPattern = rightSideBitPattern + l2;  //cipher text at this stage

            //added this so I can display rounds for question 3/4 but not number 6
            if(printOrNot){
                System.out.println("Round: " + (i+1) + " Bit Pat: " + myBitPattern);     //display cipher text at this stage
            }
            myKey = keyShift(myKey);
        }
        return new String[]{myBitPattern, myKey};
    }


    public static String xor(String l1, String rightSideKey){
        StringBuilder xorString = new StringBuilder();
        for(int i = 0; i < l1.length(); i++){
            if (l1.charAt(i) == rightSideKey.charAt(i)) {
                xorString.append('0');
            } else {
                xorString.append('1');
            }        }
        return xorString.toString();
    }

    public static String[] reverseTheProcess(String cipherText, String finalKey) {
        String myBitPattern = cipherText;
        String myKey = finalKey;
        int numberOfRounds = 16;

        for (int round = numberOfRounds - 1; round >= 0; round--) {
            myKey = reverseKeyShift(myKey);
            String leftSideBitPattern = myBitPattern.substring(0, myBitPattern.length() / 2);
            String rightSideBitPattern = myBitPattern.substring(myBitPattern.length() / 2);
            String firstHalfCurrentKey = myKey.substring(0, myKey.length() / 2);
    
            String l1 = xor(leftSideBitPattern, rightSideBitPattern);
    
            String decryptedLeftSide = roundFunction(l1, firstHalfCurrentKey, round);

            myBitPattern = decryptedLeftSide + leftSideBitPattern;
    
        }
        System.out.println("Decrypted text: " + myBitPattern);
        return new String[]{myBitPattern, myKey};
    }

    public static String reverseKeyShift(String key) {
        int halfKeyLength;
        int charToRemove;
        String bitsToTakeOff;
        String firstBitsRemoved;
        String secondBitsRemoved;
        String leftPart, rightPart;

        halfKeyLength = key.length() / 2;
        charToRemove = (int) Math.pow(2, (Math.log(key.length()) / Math.log(2)) - 4);
        
        // take chars to be  from the end of the key.
        bitsToTakeOff = key.substring(key.length() - charToRemove * 2);
        
        // split into equal parts
        firstBitsRemoved = bitsToTakeOff.substring(0, bitsToTakeOff.length() / 2);
        secondBitsRemoved = bitsToTakeOff.substring(bitsToTakeOff.length() / 2);
        
        // new length after removing
        halfKeyLength -= charToRemove;
        
        leftPart = key.substring(0, halfKeyLength);
        rightPart = key.substring(halfKeyLength, key.length() - charToRemove * 2);
        
        // put them back together
        return firstBitsRemoved + leftPart + secondBitsRemoved + rightPart;
    }

    public static void testEffectivness(String theBitPattern, String key) {
        System.out.println("Input String: " + theBitPattern);
        String myBitPattern = theBitPattern;
        String currentKey = key;
        for (int round = 0; round < 16; round++) {
            String[] encryptionResult = sixteenRoundEncryption(myBitPattern, currentKey,false);
            String encrypted = encryptionResult[0];  //bitpatrern
            currentKey = encryptionResult[1];        //key
            int changes = countTheChanges(myBitPattern, encrypted);
            System.out.println("Round " + (round + 1) + ": " + changes + " bits changed");
            myBitPattern = encrypted;
        }
    }

    public static int countTheChanges(String original, String newValue) {
        int changes = 0;
        //if different chars increase changes by 1
        for (int i = 0; i < original.length(); i++) {
            if (original.charAt(i) != newValue.charAt(i)) {
                changes++;
            }
        }
        return changes;
    }
    
}
