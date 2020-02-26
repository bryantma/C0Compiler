# C0Compiler

A java implementation of C0 compiler. 

## Standards

See: [C0 Wiki](https://github.com/BUAA-SE-Compiling).

## Deployment

For linux with Java 11.0.5 or higher:

```shell
bash build
bash make
cd out

#get assembly
java -jar cc0.jar -s <input>

#get ELF binary (unstable):
jave -jar cc0.jar -c <input>

#specify output name
java -jar cc0.jar -s <input> -o <output>
```

## Completion

- C0 basics
- C0 comment
- C0 scoped variables

## Undefined Behaviors
- integer overflow : compile error
- uninitialized variables : do nothing
- missing return statement for functions that are not void : return 0

## Acknowlegement

- Special thanks to [yiranyyu](https://github.com/yiranyyu).
