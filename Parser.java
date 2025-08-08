/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        System.out.println("Aceptada? " + S());

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        System.out.println("Resultado: " + this.operandos.peek());

        // Verifica si terminamos de consumir el input
        if(this.next != this.tokens.size()) {
            return false;
        }
        return true;
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            
            // Codigo para el Shunting Yard Algorithm
            
            if (id == Token.NUMBER) {

				operandos.push( this.tokens.get(this.next).getVal() );

			} else if (id == Token.SEMI) {

				while (!this.operadores.empty()) {
					popOp();
				}
				
			} else {
				
				pushOp( this.tokens.get(this.next) );
			}
			

            this.next++;
            return true;
        }
        return false;
    }

    // Funcion que verifica la precedencia de un operador
    private int pre(Token op) {
        /* TODO: Su codigo aqui */

        /* El codigo de esta seccion se explicara en clase */

        switch(op.getId()) {
        	case Token.PLUS:
        		return 1;
        	case Token.MINUS:
        		return 1;
            case Token.MULT:
        		return 2;
        	case Token.DIV:
        		return 2;
            case Token.MOD:
        		return 2;
        	case Token.EXP:
        		return 3;
            case Token.UNARY:
        		return 4;
        	case Token.LPAREN:
        		return 5;  
            case Token.RPAREN:
        		return 5;
        	default:
        		return -1;
        }
    }

    private void popOp() {
        Token op = this.operadores.pop();

        /* TODO: Su codigo aqui */

        /* El codigo de esta seccion se explicara en clase */

        if (op.equals(Token.PLUS)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(a + b);

        } else if (op.equals(Token.MINUS)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(b - a);

        } else if (op.equals(Token.MULT)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(a * b);

        } else if (op.equals(Token.DIV)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(b / a);

        } else if (op.equals(Token.MOD)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(b % a);

        } else if (op.equals(Token.EXP)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	this.operandos.push(Math.pow(b, a));

        } else if (op.equals(Token.UNARY)) {
        	double a = this.operandos.pop();
        	this.operandos.push(-a);
            
        } 
    }

    private void pushOp(Token op) {
        /* TODO: Su codigo aqui */
        if(operadores.isEmpty()){
            operadores.push(op);
            return;
        }

        int precOp = pre(op);

        if (op.equals(Token.LPAREN)) {
            operadores.push(op);
            return;
        }
        if (op.equals(Token.RPAREN)) {
            while (!operadores.isEmpty() && !operadores.peek().equals(Token.LPAREN)) {
                popOp();
            }
            if (!operadores.isEmpty() && operadores.peek().equals(Token.LPAREN)) {
                operadores.pop(); 
            }
            return;
        }

        // Desencolar operadores con igual o mayor precedencia
        while (!operadores.isEmpty() && pre(operadores.peek()) >= precOp &&
            !operadores.peek().equals(Token.LPAREN)) {
            popOp();
        }
        operadores.push(op);


        /* Casi todo el codigo para esta seccion se vera en clase */
    	
    	// Si no hay operandos automaticamente ingresamos op al stack

    	// Si si hay operandos:
    		// Obtenemos la precedencia de op
        	// Obtenemos la precedencia de quien ya estaba en el stack
        	// Comparamos las precedencias y decidimos si hay que operar
        	// Es posible que necesitemos un ciclo aqui, una vez tengamos varios niveles de precedencia
        	// Al terminar operaciones pendientes, guardamos op en stack

    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

   private boolean E() {
        if (!A()) return false;

        while (term(Token.PLUS) || term(Token.MINUS)) {
            if (!A()) return false;
        }
        return true;
    }

    /* TODO: sus otras funciones aqui */

    private boolean A() {
        if (!B()) return false;

         while (term(Token.MULT) || term(Token.DIV) || term(Token.MOD)) {
            Token op = this.tokens.get(this.next - 1);
            if (!B()) return false;
        }
        return true;
    }

    private boolean B() {
        if (!C()) return false;

        while (term(Token.EXP)) {
            Token op = this.tokens.get(this.next - 1);
            if (!C()) return false;
        }
        return true;
    }

    private boolean C() {
        if (term(Token.NUMBER)) {
            return true;
        }
        else if (term(Token.LPAREN)) {
            if (!E()) return false;
            if (!term(Token.RPAREN)) return false;
            return true;
        }
        else if (term(Token.MINUS)) {
            pushOp(new Token(Token.UNARY, "-"));
            if (!C()) return false;
            return true;
        }
        return false;
    }
}
