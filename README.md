# tigerc - An implementation of the Tiger programming language

`tigerc` is my attempt at implementing the "Tiger" pedagogical
programming language used in Andrew W. Appel's "Modern Compiler
Implementation in C" textbook. Despite implementing `tigerc` in Java,
I am using the C textbook as a reference, so my code will likely look
very different than "Modern Compiler Implementation in Java".

Tiger is a simple programming language featuring `int`, `string`,
record, and array types and lexical scoping using the `let`
construct. A specification for the Tiger programming language that I
have been using for this project is located [here](http://cs.nyu.edu/courses/fall13/CSCI-GA.2130-001/tiger-spec.pdf).

A sample "hello, world" program in Tiger could be:

```
let function hello_world() = print("hello, world!") in
    hello_world()
end
```
or, more simply,
```
print("hello, world!")
```

Tiger also has the ability to define type aliases using
the `type` construct,

```
let type point = {x: int, y: int} in
    ...
end
```

and the ability to define variables with `var`:
```
let var fourty_two := 42 in
    ...
end
```

## Notes on this implementation

This implementation of Tiger makes use of the ANTLR parser generator
for parsing and lexing. Several Java classes are generated by ANTLR
as part of the build process. This process is handled by maven,
so you shouldn't have to worry about it if you are building this
project.

In particular, the classes generated by ANTLR that are used by this
project are TigerLexer (lexical analyzer), TigerParser (parser), and
TigerBaseVisitor (abstract parse tree visitor).

### Implementation status
This implementation is **not complete**. The following things have
been completed in the initial commit, sans bugs:
* Lexing, parsing, AST generation
* Semantic analysis, type checking

The following things are not yet complete:
* Target-agnostic translation to IR
* IR transformations, optimization passes
* Target-specific instruction selection, register selection, code generation

The initial target platform will be MIPS32, as provided by [SPIM](http://spimsimulator.sourceforge.net/).
`tigerc` will emit a .S file that contains unassembled MIPS32 code,
which will be linked against a small runtime and executed on SPIM.

I am designing `tigerc` to be as easy to retarget as possible. It
will (hopefully!) be easy to retarget `tigerc` to platforms such
as the JVM, x86, and others. However, MIPS will be my primary focus,
as this project is designed to be a learning experience and MIPS
is a friendly compiler target for new compiler developers.

### Known issues with the current implementation
I've found that my text spans on AST nodes tend to be completely
wrong. The line numbers are correct, but the column numbers are
hugely off. I haven't looked into this yet and I'll probably forge
onward with new stuff before I fix this bug.

### Tests
I'm adding tests as I go to `tigerc/src/test/java/org/swgillespie/tigerc/test/`.
These get executed by maven during a build. there are about 30 or
so of them right now, so it's a pretty small coverage of what Tiger
allows, but it's been helpful in tracking down bugs during type
resolution. Aside from a few unit tests of small areas, these tests
generally fall into one of two categories: compile pass and compile
fail tests. (In the future, there will be runtime tests.)

Compile pass tests go in `tigerc/src/test/java/org/swgillespie/tigerc/test/code/pass` and
consist of code snippets that should compile without errors. The
corresponding test in `CompilePassTest.java` runs the file through
the compilation pipeline and asserts that there are no errors or
crashes.

Compile fail tests go in `tigerc/src/test/java/org/swgillespie/tigerc/test/code/fail` and
consist of code snippets that will produce compile errors. The
test author can place a comment on the line of code that will
produce the compile error that indicates to the test runner what
sort of error is expected. For example, a test like
```
let function i_take_an_int(x: int): int = x in
    i_take_an_int("not an int") /* error: mismatched types*/
end
```
indicates to the test runner that, on that line, there will be
an error whose message begins with "mismatched types". The test
runner will fail the test if there are any extraneous errors,
as well as if there are any expected errors that are not generated.
It'll also fail the test if there is a crash.

Since I can't figure out how to get maven to run dynamically-generated
JUnit tests, compile pass/fail tests are hooked up to the test
runner through `CompilePassTest.java` and `CompileFailTest.java`.

## Building and running
This project uses Maven for dependency management and building.
After cloning this repo, the maven invocation
```
mvn clean compile assembly:single
```
will create a standalone jar file under `target` that can be
executed like any other jar. As of today, running the generated
jar will start a REPL that will show the generated AST and the type
of the toplevel expression. This will change in the future to
a legitimate compiler driver as I work on this project.

Pressing `Control+D` at the REPL will terminate it with a nice
crash. It's not pretty, but since this is temporary, it's fine with
me.

The tests can be run using `mvn test`.

I'll add more documentation as I go, I promise!