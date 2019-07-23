package com.tinslam.convolution.Concepts;

import java.util.ArrayList;
import java.util.Stack;

public class Function{
    private String function;
    private ArrayList<String> tokens = new ArrayList<>();
    private ArrayList<String> postFix = new ArrayList<>();

    public Function(String function){
        this.function = function;
        tokenize();
        calcPostFix();
    }

    public float output(float t){
        Stack<Float> stack = new Stack<>();
        Float op1, op2;

        for(int i = 0; i < postFix.size(); i++){
            String token = postFix.get(i);
            switch(token){
                case "sin" :
                    stack.push((float) Math.sin(stack.pop()));
                    break;

                case "cos" :
                    stack.push((float) Math.cos(stack.pop()));
                    break;

                case "tan" :
                    stack.push((float) Math.tan(stack.pop()));
                    break;

                case "cot" :
                    stack.push((float) (1 / Math.tan(stack.pop())));
                    break;

                case "@" :
                    stack.push(-stack.pop());
                    break;

                case "^" :
                    op2 = stack.pop();
                    op1 = stack.pop();
                    stack.push((float) Math.pow(op1, op2));
                    break;

                case "+" :
                    stack.push(stack.pop() + stack.pop());
                    break;

                case "-" :
                    op2 = stack.pop();
                    op1 = stack.pop();
                    stack.push(op1 - op2);
                    break;

                case "*" :
                    stack.push(stack.pop() * stack.pop());
                    break;

                case "/" :
                    op2 = stack.pop();
                    op1 = stack.pop();
                    stack.push(op1 / op2);
                    break;

                default :
                    float val;
                    switch(token){
                        case "t":
                            val = t;
                            break;
                        case "pi":
                            val = (float) Math.PI;
                            break;
                        case "e":
                            val = (float) Math.E;
                            break;
                        default:
                            val = Float.parseFloat(token);
                            break;
                    }
                    stack.push(val);
                    break;
            }
        }

        return stack.pop();
    }

    private void calcPostFix(){
        Stack<String> stack = new Stack<>();
        String lastToken = "(";
        for(int i = 0; i < tokens.size(); i++){
            String token = tokens.get(i);
            if(token.equals("-")){
                if(lastToken.equals("(")) token = "@";
            }
            lastToken = token;
            if(isOperator(token)){
                String top;
                if(token.equals(")")){
                    while(!stack.empty()){
                        top = stack.pop();
                        if(!top.equals("(")){
                            postFix.add(top);
                        }else break;
                    }
                }else{
                    while(!stack.empty()){
                        top = stack.peek();
                        if(top.equals("(")) break;
                        if(priority(top) >= priority(token)){
                            top = stack.pop();
                            postFix.add(top);
                        }else break;
                    }
                    stack.push(token);
                }
            }else{
                postFix.add(token);
            }
        }
        while(!stack.empty()){
            String top = stack.pop();
            if(!top.equals("(") && !top.equals(")")) postFix.add(top);
        }
    }

    private int priority(String token){
        switch(token){
            case "(" :
                return Integer.MAX_VALUE;

            case "@" : // U-minus.
                return 40;

            case ")" :
                return 0;

            case "+" :
                return 5;

            case "-" :
                return 5;

            case "/" :
                return 10;

            case "*" :
                return 10;

            case "^" :
                return 20;

            case "sin" :
            case "cos" :
            case "tan" :
            case "cot" :
                return 100;

            default :
                return 0;
        }
    }

    private boolean isOperator(String token){
        switch(token){
            case "(" :
            case ")" :
            case "+" :
            case "-" :
            case "@" :
            case "/" :
            case "*" :
            case "^" :
            case "sin" :
            case "cos" :
            case "tan" :
            case "cot" :
                return true;

            default :
                return false;
        }
    }

    private void tokenize(){
        for(int i = 0; i < function.length(); i++){
            Character c = function.charAt(i);
            if(isSpecialCharacter(c)){
                if(Character.isWhitespace(c)) continue;
                tokens.add(c + "");
            }else{
                StringBuilder temp = new StringBuilder();
                while(!isSpecialCharacter(function.charAt(i))){
                    temp.append(function.charAt(i));
                    i++;
                    if(i == function.length()) break;
                }
                tokens.add(temp.toString());
                i--;
            }
        }
    }

    private boolean isSpecialCharacter(Character character){
        switch(character){
            case '(' :
            case ')' :
            case '+' :
            case '-' :
            case '/' :
            case '*' :
            case '^' :
            case 't' :
            case 'e' :
                return true;

            default :
                return Character.isWhitespace(character);
        }
    }
}
