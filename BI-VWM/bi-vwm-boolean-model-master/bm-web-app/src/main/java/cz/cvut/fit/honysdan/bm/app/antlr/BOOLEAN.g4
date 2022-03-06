/*
 *  Expression -> Token OR Expression
 *  Expression -> Token
 *  Token -> Factor AND Token
 *  Token -> Factor
 *  Factor -> TERM
 *  Factor -> NOT Factor
 *  Factor -> ( Expression )
 */

grammar BOOLEAN;

// Parser

start : expression END ;

expression : token OR expression | token ;

token : factor AND token | factor ;

factor : TERM | NOT factor | LPAREN expression RPAREN ;

// Lexer

TERM : [a-z]+ ;

OR : 'OR' ;
AND : 'AND' ;
NOT : 'NOT' ;
LPAREN : '(' ;
RPAREN : ')' ;
END : '\n' ;

WHITESPACE : ' ' -> skip ;