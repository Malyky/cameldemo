package com.example.demo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderUtil {

    public static String readFile(File file) throws IOException {

    FileReader fr =
            new FileReader(file);

    StringBuilder sb = new StringBuilder();
    int i;
    while ((i=fr.read()) != -1) {


        sb.append(i);

    }
    return sb.toString();
}
}
