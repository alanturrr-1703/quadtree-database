package com.plusCode;

import com.plusCode.quadtree.Quadrant;

import java.util.Arrays;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Quadrant root = new Quadrant(null, -90, 90, -180, 180);
        double lat = -89.9999999;
        double lon = -179.99999;
        System.out.println(root.encode(root, 15, lat, lon));
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the plus code: ");
        String code = sc.nextLine();
        System.out.println(Arrays.toString(Quadrant.decode(code)));
    }
}