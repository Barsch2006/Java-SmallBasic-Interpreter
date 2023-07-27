package lib;

public class TextWindow {
    /*
     * Writes a line to the console with a new line character at the end.
     */
    public void writeLine(String obj) {
        System.out.println(obj);
    }

    /*
     * Writes a line to the console with a new line character at the end.
     */
    public void writeLine(Integer obj) {
        System.out.println(obj);
    }

    /*
     * Writes a line to the console with a new line character at the end.
     */
    public void writeLine(Double obj) {
        System.out.println(obj);
    }

    /*
     * Writes a line to the console with a new line character at the end.
     */
    public void writeLine(Boolean obj) {
        System.out.println(obj);
    }

    /*
     * Writes a line to the console without a new line character at the end.
     */
    public void write(String obj) {
        System.out.print(obj);
    }

    /*
     * Writes a line to the console without a new line character at the end.
     */
    public void write(Integer obj) {
        System.out.print(obj);
    }

    /*
     * Writes a line to the console without a new line character at the end.
     */
    public void write(Double obj) {
        System.out.print(obj);
    }

    /*
     * Writes a line to the console without a new line character at the end.
     */
    public void write(Boolean obj) {
        System.out.print(obj);
    }

    /*
     * Reads a line from the console.
     */
    public String readLine(String str) {
        return System.console().readLine(str);
    }

    /*
     * Reads a line from the console.
     */
    public String readLine() {
        return System.console().readLine();
    }
}
