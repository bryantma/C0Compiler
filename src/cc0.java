
import analyser.AST;
import analyser.Parser;
import argparser.*;
import binaries.ELF;
import exceptions.tokenizerExceptions.TokenizerException;
import generator.Generator;
import tokenizer.*;

import java.io.*;
import java.lang.Object.*;
import java.util.ArrayList;


public class cc0 {

    public static void main(String[] args){
        StringHolder assemblyInput = new StringHolder();
        StringHolder binaryInput = new StringHolder();
        StringHolder output = new StringHolder();
        ArgParser ap =  new ArgParser("java -jar cc0.jar");
        ap.addOption("-s %s #compile to assembly", assemblyInput);
        ap.addOption("-c %s #compile to binary code", binaryInput);
        ap.addOption("-o %s #specify output file name", output);

        ap.matchAllArgs(args);
        OutputStream out = System.out;
        InputStream in = System.in;

        ////System.out.println(binaryInput.value);
        if (assemblyInput.value != null){
            try {
                in = new FileInputStream(assemblyInput.value);
            } catch (Exception e){
                System.out.println("Failed to read file.");
                System.exit(0);
            }
            Tokenizer tkz = new Tokenizer(in);
            try {

                ArrayList<Token> tokens = tkz.getAllTokens();
                /*for (Token t: tokens){
                    System.out.print(t+"\n");
                }*/
                Parser parser = new Parser(tokens);
                AST ast = parser.parseAll();
                //ast.draw(true);
                Generator generator = new Generator(ast);
                ELF elf = generator.generate();
                File fout;
                if (output.value != null){
                    fout = new File(output.value);
                } else {
                    fout = new File("out");
                }
                out = new FileOutputStream(fout);
                out.write(elf.generateAssembly().getBytes());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (binaryInput.value != null){
            try {
                in = new FileInputStream(binaryInput.value);
            } catch (Exception e){
                System.out.println("Failed to read file.");
                System.exit(0);
            }
            Tokenizer tkz = new Tokenizer(in);
            try {

                ArrayList<Token> tokens = tkz.getAllTokens();
                Parser parser = new Parser(tokens);
                AST ast = parser.parseAll();
                Generator generator = new Generator(ast);
                ELF elf = generator.generate();
                File fout;
                if (output.value != null){
                    fout = new File(output.value);
                } else {
                    fout = new File("out");
                }
                out = new FileOutputStream(fout);
                out.write(elf.generateBinaries());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}