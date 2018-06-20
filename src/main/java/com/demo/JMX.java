package com.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by someone on someday.
 */
public class JMX implements JMXMBean {
    public String run(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(String.format(cmd));

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        StringBuilder sb = new StringBuilder();
        String s = new String();
        while ((s = stdInput.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }

        while ((s = stdError.readLine()) != null) {
            sb.append(s);
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String s = new JMX().run("ls /tmp/");
        System.out.println(s);
    }
}
