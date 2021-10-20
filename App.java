package direelf;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        System.out.println("Welcome to Amazing Numbers!\n");
        printInstructions();
        requestInput();
    }

    private static void requestInput() {
        Scanner scanInput = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a request: ");
            String input = scanInput.nextLine();
            if (input.isEmpty()) {
                printInstructions();
                continue;
            }
            Scanner scanString = new Scanner(input);
            long number1 = scanString.nextLong();
            if (number1 == 0) {
                System.out.println("\nGoodbye!");
                scanString.close();
                return;
            } else if (number1 < 0) {
                System.out.println("\nThe first parameter should be a natural number or zero.\n");
                continue;
            } else if (!scanString.hasNext()) {
                oneNumber(number1);
                continue;
            }
            long number2 = scanString.nextLong();
            if (number2 < 1) {
                System.out.println("\nThe second parameter should be a natural number.\n");
                continue;
            } else if (!scanString.hasNext()) {
                System.out.println(someNumbers(number1, number2));
                continue;
            }
            StringBuilder requests = new StringBuilder();
            while (scanString.hasNext()) {
                requests.append(String.format("%s ", scanString.next().toUpperCase()));
            }
            String[] specifiedProperties = requests.toString().split(" ");
            String[] errors = checkErrors(specifiedProperties);
            if (errors[0].equals("true")) {
                String available = "[EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, JUMPING, HAPPY, SAD]";
                if (!errors[1].contains(",") && errors[2].equals("")) {
                    System.out.printf("\nThe property [%s] is wrong.\nAvailable properties: [%s]\n\n", errors[1], available);
                } else if (errors[1].contains(",") && errors[2].equals("")) {
                    System.out.printf("\nThe properties [%s] are wrong.\nAvailable properties: [%s]\n\n", errors[1], available);
                } else if (errors[1].equals("")){
                    System.out.printf("\nThe request contains mutually exclusive properties: [%s]\n" +
                            "There are no numbers with these properties.\n\n", errors[2]);
                }
            } else {
                long start = number1;
                while (number2 > 0) {
                    boolean flag = true;
                    for (String specifiedProperty : specifiedProperties) {
                        if (!getMethod(start, specifiedProperty)) {
                            start++;
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        System.out.print(someNumbers(start, 1).substring(0, someNumbers(start, 1).length() - 1));
                        start++;
                        number2--;
                    }
                }
                System.out.print("\n\n");
            }
        }
    }

    private static void printInstructions() {
        System.out.println("Supported requests:");
        System.out.println("- enter a natural number to know its properties;");
        System.out.println("- enter two natural numbers to obtain the properties of the list:");
        System.out.println("  * the first parameter represents a starting number;");
        System.out.println("  * the second parameters show how many consecutive numbers are to be printed;");
        System.out.println("- two natural numbers and a properties to search for;");
        System.out.println("- a property preceded by minus must not be present in numbers;");
        System.out.println("- separate the parameters with one space;");
        System.out.println("- enter 0 to exit.\n");
    }

    enum Props {
        BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, EVEN, ODD, JUMPING, HAPPY, SAD
    }

    private static String[] checkErrors(String[] array) {
        boolean hasWrongWords = false;
        boolean hasExclusives = false;
        StringBuilder errors = new StringBuilder();
        StringBuilder exclusives = new StringBuilder();
        String enums = "BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, EVEN, ODD, JUMPING, HAPPY, SAD,\n" +
                "-BUZZ, -DUCK, -PALINDROMIC, -GAPFUL, -SPY, -SQUARE, -SUNNY, -EVEN, -ODD, -JUMPING, -HAPPY, -SAD";
        for (String s : array) {
            if (!enums.contains(s)) {
                hasWrongWords = true;
                errors.append(String.format("%s, ", s));
            }
        }
        if (!hasWrongWords && array.length > 1) {
            StringBuilder arrayToString = new StringBuilder();
            for (String s : array) {
                arrayToString.append(String.format(" %s", s));
            }
            boolean minusEvenOdd = arrayToString.toString().contains("-EVEN") && arrayToString.toString().contains("-ODD");
            boolean evenOdd = arrayToString.toString().contains(" EVEN") && arrayToString.toString().contains(" ODD");
            boolean minusSunnySquare = arrayToString.toString().contains("-SUNNY") && arrayToString.toString().contains("-SQUARE");
            boolean sunnySquare = arrayToString.toString().contains(" SUNNY") && arrayToString.toString().contains(" SQUARE");
            boolean minusDuckSpy = arrayToString.toString().contains("-DUCK") && arrayToString.toString().contains("-SPY");
            boolean duckSpy = arrayToString.toString().contains(" DUCK") && arrayToString.toString().contains(" SPY");
            boolean minusHappySad = arrayToString.toString().contains("-HAPPY") && arrayToString.toString().contains("-SAD");
            boolean happySad = arrayToString.toString().contains(" HAPPY") && arrayToString.toString().contains(" SAD");
            if (minusEvenOdd) {
                hasExclusives = true;
                exclusives.append("-EVEN, -ODD, ");
            } else if (evenOdd) {
                hasExclusives = true;
                exclusives.append("EVEN, ODD, ");
            } else if (minusSunnySquare) {
                hasExclusives = true;
                exclusives.append("-SUNNY, -SQUARE, ");
            } else if (sunnySquare) {
                hasExclusives = true;
                exclusives.append("SUNNY, SQUARE, ");
            } else if (minusDuckSpy) {
                hasExclusives = true;
                exclusives.append("-DUCK, -SPY, ");
            } else if (duckSpy) {
                hasExclusives = true;
                exclusives.append("DUCK, SPY, ");
            } else if (minusHappySad) {
                hasExclusives = true;
                exclusives.append("-HAPPY, -SAD, ");
            } else if (happySad) {
                hasExclusives = true;
                exclusives.append("HAPPY, SAD, ");
            } else {
                for (Props p : Props.values()) {
                    if (arrayToString.toString().contains(String.format("-%s", p.name()))) {
                        if (arrayToString.toString().contains(String.format(" %s", p.name()))) {
                            hasExclusives = true;
                            exclusives.append(String.format("%s, -%s, ", p.name(), p.name()));
                        }
                    }
                }
            }
        }
        String[] result = new String[3];
        result[0] = hasWrongWords || hasExclusives ? String.valueOf(true) : String.valueOf(false);
        result[1] = hasWrongWords ? errors.substring(0, (errors.toString().length() - 2)) : "";
        result[2] = hasExclusives ? exclusives.substring(0, (exclusives.toString().length() - 2)) : "";
        return result;
    }

    private static void oneNumber(long number) {
        System.out.printf(Locale.US, "\nProperties of %,d\n", number);
        System.out.println("\t\tbuzz: " + isBuzz(number));
        System.out.println("\t\tduck: " + isDuck(number));
        System.out.println(" palindromic: " + isPalindromic(number));
        System.out.println("\t  gapful: " + isGapful(number));
        System.out.println("\t\teven: " + isEven(number));
        System.out.println("\t\t odd: " + !isEven(number));
        System.out.println("\t\t spy: " + isSpy(number));
        System.out.println("\t  square: " + isSquare(number));
        System.out.println("\t jumping: " + isJumping(number));
        System.out.println("\t   happy: " + isHappy(number));
        System.out.println("\t\t sad: " + !isHappy(number));
        System.out.println("\t   sunny: " + isSunny(number) + "\n");
    }

    private static String someNumbers(long start, long range) {
        StringBuilder result = new StringBuilder("\n");
        for (long i = start; i < (start + range); i++) {
            result.append(String.format("%d is ", i));
            result.append(isBuzz(i) ? "buzz, " : "");
            result.append(isDuck(i) ? "duck, " : "");
            result.append(isPalindromic(i) ? "palindromic, " : "");
            result.append(isGapful(i) ? "gapful, " : "");
            result.append(isSpy(i) ? "spy, " : "");
            result.append(isSquare(i) ? "square, " : "");
            result.append(isSunny(i) ? "sunny, " : "");
            result.append(isJumping(i) ? "jumping, " : "");
            result.append(isHappy(i) ? "happy, " : "sad, ");
            result.append(isEven(i) ? "even" : "odd");
            result.append("\n");
        }
        return (result.toString());
    }

    private static boolean getMethod(long number, String property) {
        boolean result = false;
        switch (property.toUpperCase()) {
            case "BUZZ":
                result = isBuzz(number);
                break;
            case "-BUZZ":
                result = !isBuzz(number);
                break;
            case "DUCK":
                result = isDuck(number);
                break;
            case "-DUCK":
                result = !isDuck(number);
                break;
            case "PALINDROMIC":
                result = isPalindromic(number);
                break;
            case "-PALINDROMIC":
                result = !isPalindromic(number);
                break;
            case "GAPFUL":
                result = isGapful(number);
                break;
            case "-GAPFUL":
                result = !isGapful(number);
                break;
            case "SPY":
                result = isSpy(number);
                break;
            case "-SPY":
                result = !isSpy(number);
                break;
            case "SQUARE":
                result = isSquare(number);
                break;
            case "-SQUARE":
                result = !isSquare(number);
                break;
            case "SUNNY":
                result = isSunny(number);
                break;
            case "-SUNNY":
                result = !isSunny(number);
                break;
            case "JUMPING":
                result = isJumping(number);
                break;
            case "-JUMPING":
                result = !isJumping(number);
                break;
            case "EVEN":
            case "-ODD":
                result = isEven(number);
                break;
            case "-EVEN":
            case "ODD":
                result = !isEven(number);
                break;
            case "HAPPY":
            case "-SAD":
                result = isHappy(number);
                break;
            case "-HAPPY":
            case "SAD":
                result = !isHappy(number);
                break;
        }
        return result;
    }

    private static boolean isBuzz(long number) {
        boolean divBy7 = number % 7 == 0;
        boolean endsWith7 = number % 10 == 7;
        return divBy7 || endsWith7;
    }

    private static boolean isEven(long number) {
        return number % 2 == 0;
    }

    private static boolean isDuck(long number) {
        while (number > 9) {
            if (number % 10 == 0) {
                return true;
            } else {
                number /= 10;
            }
        }
        return false;
    }

    private static boolean isPalindromic(long number) {
        String sNumber = "" + number;
        StringBuilder temp = new StringBuilder();
        for (int i = sNumber.length() - 1; i >= 0; i--) {
            temp.append(sNumber.charAt(i));
        }
        return sNumber.equals(temp.toString());
    }

    private static boolean isGapful(long number) {
        if (number / 100 > 0) {
            long numberLength = String.valueOf(Math.abs(number)).length();
            long divider = (10 * (number / (long) Math.pow(10, numberLength - 1)) + number % 10);
            return number % divider == 0;
        } else {
            return false;
        }
    }

    private static boolean isSpy(long number) {
        String digits = String.valueOf(number);
        long digitsSum = 0;
        long digitsProduct = 1;
        for (int i = 0; i < digits.length(); i++) {
            digitsSum += number % 10;
            digitsProduct *= number % 10;
            number /= 10;
        }
        return digitsSum == digitsProduct;
    }

    private static boolean isSquare(long number) {
        return number % Math.sqrt(number) == 0;
    }

    private static boolean isSunny(long number) {
        return isSquare(number + 1);
    }

    private static boolean isJumping(long number) {
        if (number < 10) {
            return true;
        }
        while (number > 9) {
            if (Math.abs((number % 10) - ((number / 10) % 10)) != 1) {
                return false;
            } else {
                number /= 10;
            }
        }
        return true;
    }

    private static boolean isHappy(long number) {
        boolean result;
        ArrayList<Long> temp = new ArrayList<>();
        temp.add(number);
        long start = number;
        long sum = 0;
        while (true) {
            while (start > 9) {
                sum += (start % 10) * (start % 10);
                start /= 10;
            }
            sum += start * start;
            if (sum == 1) {
                result = true;
                break;
            } else if (temp.contains(sum)) {
                result = false;
                break;
            } else {
                temp.add(sum);
                start = sum;
                sum = 0;
            }
        }
        return result;
    }
}

