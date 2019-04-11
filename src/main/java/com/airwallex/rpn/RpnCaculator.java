package com.airwallex.rpn;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RpnCaculator {
    public static void main(String[] args) {
        System.out.println("Please input the list and press Enter:");
        System.out.println("(The calculator expects to receive strings containing whitespace separated lists of numbers and operators.)");
        Scanner sc = new Scanner(System.in);
        RpnCaculator calc = new RpnCaculator();
        Stack<String> stack = new Stack<>();
        while (sc.hasNextLine()) {
            calc.printStack(calc.evalRPN(sc.nextLine(), stack));
        }
    }

    private Stack<RpnEntry> undoStackMap = new Stack();

    private void printStack(Stack stack) {
        Iterator<String> it = stack.iterator();
        while (it.hasNext()) {
            System.out.println(formatDouble(Double.valueOf(it.next())));
        }
    }


    public static String formatDouble(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(10, RoundingMode.UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    private void recordEachOperation(String operator, Double number) {
        RpnEntry entry = new RpnEntry();
        entry.setOperator(operator);
        entry.setNumber(number);
        undoStackMap.push(entry);
    }

    public Stack evalRPN(String input, Stack<String> stack) {
        String operators = "+,-,*,/,sqrt,undo,clear";
        String[] tokens = input.split(" ");
        int pos = 0;
        for (String t : tokens) {
            if (!operators.contains(t) && isNumeric(t)) {
                pos++;
                recordEachOperation("", Double.valueOf(t));
                stack.push(t);
            } else {
                if ("sqrt".equalsIgnoreCase(t)) {
                    Double factor;
                    if (!stack.empty()) {
                        factor = Double.valueOf(stack.pop());
                    } else {
                        pos++;
                        log.warn("operator <operator> (position: <" + pos + ">): insufficient parameters");
                        break;
                    }
                    pos++;
                    recordEachOperation(t, factor);
                    stack.push(String.valueOf(Math.sqrt(factor)));
                    continue;
                } else if ("undo".equalsIgnoreCase(t)) {
                    undo(stack);
                    continue;
                } else if ("clear".equalsIgnoreCase(t)) {
                    stack.removeAllElements();
                    continue;
                }
                Double a;
                if (!stack.empty()) {
                    pos++;
                    a = Double.valueOf(stack.pop());
                } else {
                    pos++;
                    log.warn("operator <operator> (position: <" + pos + ">): insufficient parameters");
                    break;
                }
                Double b;
                if (!stack.empty()) {
                    pos++;
                    b = Double.valueOf(stack.pop());
                } else {
                    pos++;
                    log.warn("operator <operator> (position: <" + pos + ">): insufficient parameters");
                    break;
                }
                switch (t) {
                    case "+":
                        pos++;
                        stack.push(String.valueOf(a + b));
                        recordEachOperation(t, a);
                        break;
                    case "-":
                        pos++;
                        stack.push(String.valueOf(b - a));
                        recordEachOperation(t, a);
                        break;
                    case "*":
                        pos++;
                        stack.push(String.valueOf(a * b));
                        recordEachOperation(t, a);
                        break;
                    case "/":
                        if (a != 0)
                            stack.push(String.valueOf(b / a));
                        else {
                            log.error("Denominator is zero!");
                            break;
                        }
                        pos++;
                        recordEachOperation(t, a);
                        break;
                }
            }
        }

        return stack;
    }

    private void undo(Stack<String> stack) {
        if (!undoStackMap.empty()) {
            RpnEntry entry = undoStackMap.pop();
            Double currently;
            Double original;
            switch (entry.getOperator()) {
                case "sqrt":
                    currently = Double.valueOf(stack.pop());
                    original = Math.pow(currently, 2);
                    stack.push(String.valueOf(original));
                    break;
                case "+":
                    currently = Double.valueOf(stack.pop());
                    original = currently - entry.getNumber();
                    stack.push(String.valueOf(original));
                    stack.push(String.valueOf(entry.getNumber()));
                    break;
                case "-":
                    currently = Double.valueOf(stack.pop());
                    original = currently + entry.getNumber();
                    stack.push(String.valueOf(original));
                    stack.push(String.valueOf(entry.getNumber()));
                    break;
                case "*":
                    currently = Double.valueOf(stack.pop());
                    original = currently / entry.getNumber();
                    stack.push(String.valueOf(original));
                    stack.push(String.valueOf(entry.getNumber()));
                    break;
                case "/":
                    currently = Double.valueOf(stack.pop());
                    original = currently * entry.getNumber();
                    stack.push(String.valueOf(original));
                    stack.push(String.valueOf(entry.getNumber()));
                    break;
                case "":
                    stack.pop();
                    break;
            }
        }
    }
}
