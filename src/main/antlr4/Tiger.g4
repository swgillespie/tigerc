grammar Tiger;

@header {
    package org.swgillespie.tigerc.parser;
}

program
    :   exp
    ;

declaration
    :   'type' IDENTIFIER '=' ty                                            #type_dec
    |   'var' IDENTIFIER ':=' exp                                           #var_dec
    |   'var' IDENTIFIER ':' IDENTIFIER ':=' exp                            #var_dec_with_type
    |   'function' IDENTIFIER '(' field_dec_list ')' '=' exp                #function_dec
    |   'function' IDENTIFIER '(' field_dec_list ')' ':' IDENTIFIER '=' exp #function_dec_with_type
    ;

ty
    :   IDENTIFIER                 #ty_fundamental
    |   'array' 'of' IDENTIFIER    #ty_array
    |   '{' field_dec_list '}'     #ty_record
    ;

field_dec_list
    :   /* empty */
    |   nonempty_field_dec_list
    ;

nonempty_field_dec_list
    :   field_dec (',' field_dec)*
    ;

field_dec
    :   IDENTIFIER ':' IDENTIFIER
    ;

l_value
    :   IDENTIFIER             #lvalue_identifier
    |   l_value '[' exp ']'    #lvalue_index
    |   l_value '.' IDENTIFIER #lvalue_field_access
    ;

exp
    :   l_value                                             #l_value_exp
    |   'nil'                                               #nil_exp
    |   INTEGER                                             #integer_literal_exp
    |   STRING_LITERAL                                      #string_literal_exp
    |   '(' semi_sep_exp ')'                                #paren_exp
    |   '-' exp                                             #negation_exp
    |   IDENTIFIER '(' comma_sep_exp ')'                    #call_exp
    |   exp infix_op exp                                    #infix_op_exp
    |   IDENTIFIER '[' exp ']' 'of' exp                     #array_create_exp
    |   IDENTIFIER '{' comma_sep_field_create '}'           #rec_create_exp
    |   l_value ':=' exp                                    #assign_exp
    |   'if' exp 'then' exp 'else' exp                      #if_then_else_exp
    |   'if' exp 'then' exp                                 #if_then_exp
    |   'while' exp 'do' exp                                #while_exp
    |   'for' IDENTIFIER ':=' exp 'to' exp 'do' exp         #for_exp
    |   'break'                                             #break_exp
    |   'let' declarations 'in' semi_sep_exp 'end'          #let_exp
    ;

declarations
    :   declaration+
    ;

semi_sep_exp
    :   /*empty*/
    |   nonempty_semi_sep_exp
    ;

nonempty_semi_sep_exp
    :   exp (';' exp)*
    ;

comma_sep_exp
    :   /*empty*/
    |   nonempty_comma_sep_exp
    ;

nonempty_comma_sep_exp
    :   exp (',' exp)*
    ;

comma_sep_field_create
    :   /*empty*/
    |   nonempty_comma_sep_field_create
    ;

nonempty_comma_sep_field_create
    :   field_create (',' field_create)*
    ;

field_create
    :   IDENTIFIER '=' exp
    ;

infix_op
    :   '*' | '/' | '+' | '-' | '=' | '<>' | '>' | '<' | '>=' | '<=' | '&' | '|'
    ;

IDENTIFIER
	:	[a-zA-Z][a-zA-Z0-9_]*
	;

INTEGER
	:	[0-9]+
	;

BOOLEAN
	:	('true' | 'false')
	;

WS
	:	[ \n\t\r] -> channel(HIDDEN)
	;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;

STRING_LITERAL :  '"' (ESC | ~["\\])* '"' ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;
