package com.airwallex.rpn;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

public class RpnCaculatorTest {

    private RpnCaculator calc;

    @Before
    public void setUp() {
        calc = new RpnCaculator();
    }

    @Test
    public void testEvalRPN() {
        Stack stack = new Stack();
        Stack dest = new Stack();
        dest.push("5");
        dest.push("2");
        assertEquals(dest, calc.evalRPN("5 2", stack));
    }

    @Test
    public void testEvalRPN4Sqrt() {
        Stack<String> stack = new Stack();
        calc.evalRPN("9 sqrt", stack);
        assertEquals(Double.valueOf(stack.peek()).compareTo(3d), 0);
    }

    @Test
    public void testEvalRPN4Clear() {
        Stack<String> stack = new Stack();
        calc.evalRPN("0 clear", stack);
        assertTrue(stack.empty());
    }

    @Test
    public void testEvalRPN4Undo() {
        Stack<String> stack = new Stack();
        calc.evalRPN("5 4 3 2 undo undo *", stack);
        assertTrue(Double.valueOf(stack.peek()) == 20d);
    }

    @Test
    public void testEvalRPN4Minus() {
        Stack<String> stack = new Stack();
        calc.evalRPN("42 4 /", stack);
        assertEquals(Double.valueOf(stack.peek()).compareTo(10.5d), 0);
    }

    @Test
    public void testEvalRPN4MultiSteps() {
        Stack<String> stack = new Stack();
        calc.evalRPN("1 2 3 4 5 *", stack);
        assertEquals(Double.valueOf(stack.peek()).compareTo(20d), 0);
        calc.evalRPN("clear 3 4 -", stack);
        assertEquals(Double.valueOf(stack.peek()).compareTo(-1d), 0);
    }

    @Test
    public void testEvalRPN4MultiOps() {
        Stack<String> stack = new Stack();
        calc.evalRPN("1 2 3 4 5 * * * *", stack);
        assertEquals(Double.valueOf(stack.peek()).compareTo(120d), 0);
    }

    @Test
    public void testEvalRPN4WrongParams() {
        Stack<String> stack = new Stack();
        calc.evalRPN("1 2 3 * 5 + * * 6 5", stack);
    }

}
