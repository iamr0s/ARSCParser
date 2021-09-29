package com.rosan.parser.arsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        System.out.println("see the api,pls");
//        test("D:\\Developer\\Rosan\\installer\\ARSCParser\\resources.arsc");
//        test("D:\\Developer\\Rosan\\installer\\ARSCParser\\resources via.arsc");
//        test("D:\\Developer\\Rosan\\installer\\ARSCParser\\resources photos.arsc");
    }

    public static void test(String path) {
        FileInputStream inputStream = null;
        try {
            System.out.println("**********");
            File file = new File(path);
            byte[] bytes = new byte[(int) file.length()];
            new FileInputStream(file).read(bytes);
//            int resId = 0x7F060073;
            int resId = 0x7F060153;
            TableChunk table = new TableChunk(bytes);
            for (PackageChunk aPackage : table.getPackages()) {
                if (aPackage.getPackageId() == resId >> 24) {
                    for (TypeChunk type : aPackage.getTypes()) {
                        if (type.getTypeId() == resId << 8 >> 24) {
                            Entry entry = type.getEntries().get(resId % 0x10000);
                            if (entry != null) {
                                Value value = entry.getValue();
                                System.out.println(String.format("%02X", value.getType()));
                                System.out.println(String.format("%08X", value.getData()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
