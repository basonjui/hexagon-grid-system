package com.geospatial.geometryapi.utils;

public class JVMUtils {
        private static final Runtime RUNTIME = Runtime.getRuntime();

        private static final int MB = 1024 * 1024;
        private static final int GB = 1024 * 1024 * 1024;

        private static final long maxMemory = RUNTIME.maxMemory();
        private static final long freeMemory = RUNTIME.freeMemory();
        private static final long totalMemory = RUNTIME.totalMemory();
        private static final long usedMemory = totalMemory - freeMemory;

        public static final void printUsedMemory(String unit) {
                final long convertedMemory;

                switch(unit) {
                        case "MB":
                                convertedMemory = usedMemory / MB;
                                break;
                        case "GB":
                                convertedMemory = usedMemory / GB;
                                break;
                        default:
                                convertedMemory = usedMemory;
                                break;
                }
                
                System.out.println("Used Memory *\t: " + convertedMemory  + unit);
        }

        public static final void printFreeMemory(String unit) {
                final long convertedMemory;

                switch(unit) {
                        case "MB":
                                convertedMemory = freeMemory / MB;
                                break;
                        case "GB":
                                convertedMemory = freeMemory / GB;
                                break;
                        default:
                                convertedMemory = freeMemory;
                                break;
                }

                System.out.println("Free Memory *\t: " + convertedMemory  + unit);
        }

        public static final void printTotalMemory(String unit) {
                final long convertedMemory;
                
                switch(unit) {
                        case "MB":
                                convertedMemory = totalMemory / MB;
                                break;
                        case "GB":
                                convertedMemory = totalMemory / GB;
                                break;
                        default:
                                convertedMemory = totalMemory;
                                break;
                }

                System.out.println("Total Memory\t: " + convertedMemory  + unit);
        }

        public static final void printMaxMemory(String unit) {
                final long convertedMemory;
                
                switch(unit) {
                        case "MB":
                                convertedMemory = maxMemory / MB;
                                break;
                        case "GB":
                                convertedMemory = maxMemory / GB;
                                break;
                        default:
                                convertedMemory = maxMemory;
                                break;
                }

                System.out.println("Max Memory\t: " + convertedMemory  + unit);
        }

        public static final void printMemoryUsages(String unit) {
                System.out.println("\n------ JVM's stats ------");
                printMaxMemory(unit);
                printTotalMemory(unit);
                printUsedMemory(unit);
                printFreeMemory(unit);
        }
}
