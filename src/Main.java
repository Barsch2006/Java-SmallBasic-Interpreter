import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

public class Main {
    private static final Map<String, Class<?>> loadedClasses = new HashMap<>();
    private static final Map<String, Object> variables = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // check if the user provided a valid .js file as an argument
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments provided");
        }

        if (!args[0].endsWith(".sb")) {
            throw new IllegalArgumentException("Invalid file extension. Only .sb files are supported");
        }

        Path path = Paths.get(args[0]);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist");
        }

        // read the file into a list of strings
        List<String> fileInLines = Files.readAllLines(path);

        interpret(fileInLines);
    }

    private static void interpret(List<String> lines) {
        int lineIndex = 0;
        while (lineIndex < lines.size()) {
            try {
                // remove all whitespaces except for the ones in strings
                String line = lines.get(lineIndex).replaceAll("\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", "");
                // remove all comments
                line = line.replaceAll("//.*", "");
                // remove all empty lines
                if (line.isEmpty()) {
                    lineIndex++;
                    continue;
                }

                if (line.equals("EndIf")) {
                    lineIndex++;
                    continue;
                }

                if (line.equals("EndWhile")) {
                    lineIndex++;
                    continue;
                }

                if (line.startsWith("If")) {
                    // get the condition string (without the If keyword and the Then keyword and the
                    // brackets)
                    String condition = line.substring(line.indexOf("If") + 2, line.indexOf("Then"));
                    // get the lineIndex of the next "EndIf" line, and skip an EndIf for each new If
                    // we find
                    // if there is a Else statement, skip it and save its lineIndex
                    int endIfIndex = lineIndex + 1;
                    int ifCount = 1;
                    int elseIndex = -1;
                    while (ifCount > 0) {
                        String nextLine = lines.get(endIfIndex);
                        if (nextLine.startsWith("If")) {
                            ifCount++;
                        } else if (nextLine.startsWith("EndIf")) {
                            ifCount--;
                        } else if (nextLine.startsWith("Else")) {
                            elseIndex = endIfIndex;
                        }
                        endIfIndex++;
                    }
                    // check if the condition is true
                    if (ConditionIsTrue(condition) == true) {
                        // interpret the lines between the If and the EndIf
                        interpret(lines.subList(lineIndex + 1, endIfIndex - 1));
                        // skip the lines between the If and the EndIf
                        lineIndex = endIfIndex - 1;
                        continue;
                    } else {
                        // check if there is an Else statement
                        if (elseIndex != -1) {
                            // interpret the lines between the Else and the EndIf
                            interpret(lines.subList(elseIndex + 1, endIfIndex - 1));
                        }
                        // skip the lines between the If and the EndIf
                        lineIndex = endIfIndex - 1;
                        continue;
                    }
                } else if (line.startsWith("While")) {
                    // get the condition string (without the If keyword and the Do keyword and the
                    // brackets)
                    String condition = line.substring(line.indexOf("While") + 5, line.length());
                    // check if the condition is true
                    while (ConditionIsTrue(condition) == true) {
                        // get the lineIndex of the next "EndIf" line, and skip an EndIf for each new If
                        // we find
                        int endWhileIndex = lineIndex + 1;
                        int ifCount = 1;
                        while (ifCount > 0) {
                            String nextLine = lines.get(endWhileIndex);
                            if (nextLine.startsWith("While")) {
                                ifCount++;
                            } else if (nextLine.startsWith("EndWhile")) {
                                ifCount--;
                            }
                            endWhileIndex++;
                        }
                        // interpret the lines between the While and the EndWhile
                        interpret(lines.subList(lineIndex + 1, endWhileIndex - 1));
                    }
                    // get the lineIndex of the next "EndIf" line, and skip an EndIf for each new If
                    // we find
                    int endWhileIndex = lineIndex + 1;
                    int ifCount = 1;
                    while (ifCount > 0) {
                        String nextLine = lines.get(endWhileIndex);
                        if (nextLine.startsWith("While")) {
                            ifCount++;
                        } else if (nextLine.startsWith("EndWhile")) {
                            ifCount--;
                        }
                        endWhileIndex++;
                    }
                    // skip the lines between the If and the EndIf
                    lineIndex = endWhileIndex - 1;
                    continue;
                } else if (line.contains("=")) {
                    if (line.contains("(") && line.contains(")")) {
                        // get the method name
                        String methodName = line.substring(line.indexOf("=") + 1, line.indexOf("("));
                        // get the arguments
                        String argsString = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                        String[] args = argsString.split(",");
                        for (int i = 0; i < args.length; i++) {
                            // remove all whitespaces from the arguments except for the ones in strings
                            args[i] = args[i].replaceAll("\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", "");
                            // remove the quotes from the arguments
                            args[i] = args[i].replaceAll("\"", "");
                        }

                        // get the class name
                        String className = methodName.substring(0, methodName.lastIndexOf("."));
                        // get the method name
                        methodName = methodName.substring(methodName.lastIndexOf(".") + 1);

                        // execute the method
                        Object returned = ClassMethodManager.executeClassMethod(className, methodName, (Object[]) args);
                        if (returned != null) {
                            variables.put(line.substring(0, line.indexOf("=")), returned);
                        }
                    } else if (variables.containsKey(line.substring(line.indexOf("=") + 1, line.length()))) {
                        variables.put(line.substring(0, line.indexOf("=")),
                                variables.get(line.substring(line.indexOf("=") + 1, line.length())));
                    } else {
                        // If the line contains an arithmetic operation for variable assignment
                        if (line.contains("+") || line.contains("-") || line.contains("*") || line.contains("/")) {
                            String[] parts = line.split("=");
                            String variableName = parts[0].trim();
                            String operationString = parts[1].trim();

                            String[] operationParts = operationString
                                    .split("(?<=\\+)|(?=\\+)|(?<=\\-)|(?=\\-)|(?<=\\*)|(?=\\*)|(?<=\\/)|(?=\\/)");

                            Object result = null;
                            String operator = null;

                            for (int i = 0; i < operationParts.length; i++) {
                                String part = operationParts[i].trim();
                                if (part.equals("+") || part.equals("-") || part.equals("*") || part.equals("/")) {
                                    operator = part;
                                } else {
                                    if (result == null) {
                                        result = Math.getValueOrVariable(part);
                                    } else {
                                        Object rightOperand = Math.getValueOrVariable(part);
                                        result = Math.performArithmeticOperation(operator, result, rightOperand);
                                    }
                                }
                            }

                            variables.put(variableName, result);
                        } else {
                            // get the value of the variable
                            String value = line.substring(line.indexOf("=") + 1, line.length());
                            // remove all whitespaces from the value except for the ones in strings
                            value = value.replaceAll("\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", "");
                            // remove the quotes from the value
                            value = value.replaceAll("\"", "");
                            // check if value is a boolean value
                            if (value.equals("true") || value.equals("false")) {
                                variables.put(line.substring(0, line.indexOf("=")), Boolean.parseBoolean(value));
                                lineIndex++;
                                continue;
                            }
                            // check if value is a number (int or double)
                            try {
                                if (value.contains(".")) {
                                    variables.put(line.substring(0, line.indexOf("=")), Double.parseDouble(value));
                                } else {
                                    variables.put(line.substring(0, line.indexOf("=")), Integer.parseInt(value));
                                }
                                lineIndex++;
                                continue;
                            } catch (NumberFormatException e) {
                                variables.put(line.substring(0, line.indexOf("=")), value);
                            }
                        }
                    }
                } // check if the line is a method call
                else if (line.contains("(") && line.contains(")")) {
                    // get the method name
                    String methodName = line.substring(0, line.indexOf("("));
                    // get the arguments
                    String argsString = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    String[] args = argsString.split(",");
                    for (int i = 0; i < args.length; i++) {
                        // remove all whitespaces from the arguments except for the ones in strings
                        args[i] = args[i].replaceAll("\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", "");
                        // remove the quotes from the arguments
                        args[i] = args[i].replaceAll("\"", "");
                    }

                    // get the class name
                    String className = methodName.substring(0, methodName.lastIndexOf("."));
                    // get the method name
                    methodName = methodName.substring(methodName.lastIndexOf(".") + 1);

                    // execute the method
                    Object returned = ClassMethodManager.executeClassMethod(className, methodName, (Object[]) args);
                    if (returned != null) {
                        System.out.println(returned);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid line: " + line);
                }

                lineIndex++;
            } catch (Exception e) {
                // todo: add error handling
                System.out.println("Error on line " + (lineIndex + 1));
                e.printStackTrace();
                lineIndex++;
                continue;
            }
        }
    }

    private static Boolean ConditionIsTrue(String condition) {
        /*
         * Example:
         * (True && True) && (True || False)
         */
        // remove all whitespaces from the condition except for the ones in strings
        condition = condition.replaceAll("\\s+(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", "");
        Boolean res = SingleConditionIsTrue(condition) == true;
        return res;
    }

    private static Boolean SingleConditionIsTrue(String condition) {
        /*
         * Example:
         * True && True
         */
        if (condition == null || condition.isEmpty()) {
            return false;
        }
        if (condition.equals("true")) {
            return true;
        }
        if (condition.equals("false")) {
            return false;
        }
        if (condition.contains("==")) {
            String leftHand = condition.substring(0, condition.indexOf("==")).trim();
            String rightHand = condition.substring(condition.indexOf("==") + 2,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble - rightDouble == 0;
            } catch (NumberFormatException e) {
                return leftHand.equals(rightHand);
            }
        } else if (condition.contains("<>")) {
            String leftHand = condition.substring(0, condition.indexOf("<>")).trim();
            String rightHand = condition.substring(condition.indexOf("<>") + 2,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble != rightDouble;
            } catch (NumberFormatException e) {
                return !leftHand.equals(rightHand);
            }
        } else if (condition.contains("<")) {
            String leftHand = condition.substring(0, condition.indexOf("<")).trim();
            String rightHand = condition.substring(condition.indexOf("<") + 1,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble < rightDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (condition.contains(">")) {
            String leftHand = condition.substring(0, condition.indexOf(">")).trim();
            String rightHand = condition.substring(condition.indexOf(">") + 1,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble > rightDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (condition.contains("<=")) {
            String leftHand = condition.substring(0, condition.indexOf("<=")).trim();
            String rightHand = condition.substring(condition.indexOf("<=") + 2,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble <= rightDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (condition.contains(">=")) {
            String leftHand = condition.substring(0, condition.indexOf(">=")).trim();
            String rightHand = condition.substring(condition.indexOf(">=") + 2,
                    condition.length()).trim();
            // check if leftHand is a variable
            if (variables.containsKey(leftHand)) {
                leftHand = variables.get(leftHand).toString();
            }
            // check if rightHand is a variable
            if (variables.containsKey(rightHand)) {
                rightHand = variables.get(rightHand).toString();
            }
            // check if leftHand and rightHand are numbers
            try {
                double leftDouble = Double.parseDouble(leftHand);
                double rightDouble = Double.parseDouble(rightHand);
                return leftDouble >= rightDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private class ClassMethodManager {
        public static void loadClassFromLib(String className) {
            try {
                // check if the class is already loaded
                if (loadedClasses.containsKey(className)) {
                    return;
                }
                Class<?> classFound = Class.forName("lib.SB" + className);
                loadedClasses.put(className, classFound);
            } catch (ClassNotFoundException e) {
                // todo: add error handling
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                // todo: add error handling
                e.printStackTrace();
                System.exit(1);
            }
        }

        public static Object executeClassMethod(String className, String methodName, Object... args) {
            try {
                loadClassFromLib(className);
                Class<?> loadedClass = loadedClasses.get(className);
                Object instance = loadedClass.getDeclaredConstructor().newInstance();

                // Prepare the array of Class objects representing the argument types
                Class<?>[] argTypes = new Class<?>[args.length];
                Object[] argValues = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof String && variables.containsKey((String) args[i])) {
                        // If the argument is a variable name (starts with $), get its value from the
                        // variables map
                        String variableName = (String) args[i];
                        argValues[i] = variables.get(variableName);
                        argTypes[i] = argValues[i].getClass();
                    } else {
                        // If the argument is a regular value, use it as is
                        argValues[i] = args[i];
                        argTypes[i] = args[i].getClass();
                    }
                }

                Method method = loadedClass.getMethod(methodName, argTypes);
                Object returned = method.invoke(instance, argValues);
                return returned;
            } catch (Exception e) {
                // todo: add error handling
                e.printStackTrace();
                return null;
            }
        }
    }

    private class Math {
        public static Object getValueOrVariable(String input) {
            try {
                if (input.matches("-?\\d+")) {
                    // Integer value
                    return Integer.parseInt(input);
                } else if (input.matches("-?\\d+\\.\\d+")) {
                    // Double value
                    return Double.parseDouble(input);
                } else if (variables.containsKey(input)) {
                    // Variable
                    return variables.get(input);
                } else {
                    throw new IllegalArgumentException("Invalid input: " + input);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid input format: " + input);
            }
        }

        public static Object performArithmeticOperation(String operation, Object leftOperand, Object rightOperand) {
            if (leftOperand instanceof Integer && rightOperand instanceof Integer) {
                int leftInt = (Integer) leftOperand;
                int rightInt = (Integer) rightOperand;

                switch (operation) {
                    case "+":
                        return leftInt + rightInt;
                    case "-":
                        return leftInt - rightInt;
                    case "*":
                        return leftInt * rightInt;
                    case "/":
                        if (rightInt != 0) {
                            String leftStr = Integer.toString(leftInt);
                            String rightStr = Integer.toString(rightInt);
                            return Double.parseDouble(leftStr) / Double.parseDouble(rightStr);
                        } else {
                            throw new ArithmeticException("Division by zero");
                        }
                    default:
                        throw new IllegalArgumentException("Unsupported arithmetic operation: " + operation);
                }
            } else if (leftOperand instanceof Double || rightOperand instanceof Double) {
                double leftDouble = Double.parseDouble(leftOperand.toString());
                double rightDouble = Double.parseDouble(rightOperand.toString());

                switch (operation) {
                    case "+":
                        return leftDouble + rightDouble;
                    case "-":
                        return leftDouble - rightDouble;
                    case "*":
                        return leftDouble * rightDouble;
                    case "/":
                        if (rightDouble != 0) {
                            return leftDouble / rightDouble;
                        } else {
                            throw new ArithmeticException("Division by zero");
                        }
                    default:
                        throw new IllegalArgumentException("Unsupported arithmetic operation: " + operation);
                }
            } else {
                throw new IllegalArgumentException("Operands must be of numeric types (int or double)");
            }
        }
    }
}
