package com.termux.terminal;

import java.util.List;

/** "\033[" is the Control Sequence Introducer char sequence (CSI). */
public class ControlSequenceIntroducerTest extends TerminalTestCase {

    /**
     * CSI Ps P Scroll down Ps lines (default = 1) (SD).
     */
    public void testCsiT() {
        withTerminalSized(4, 6).enterString("1\r\n2\r\n3\r\nhi\033[2Tyo\r\nA\r\nB").assertLinesAre("    ", "    ", "1   ", "2 yo", "A   ", "Bi  ");
        // Default value (1):
        withTerminalSized(4, 6).enterString("1\r\n2\r\n3\r\nhi\033[Tyo\r\nA\r\nB").assertLinesAre("    ", "1   ", "2   ", "3 yo", "Ai  ", "B   ");
    }

    /**
     * See <a href="https://sw.kovidgoyal.net/kitty/underlines/">Colored and styled underlines</a>:
     *
     * <pre>
     *  <ESC>[4:0m  # no underline
     *  <ESC>[4:1m  # straight underline
     *  <ESC>[4:2m  # double underline
     *  <ESC>[4:3m  # curly underline
     *  <ESC>[4:4m  # dotted underline
     *  <ESC>[4:5m  # dashed underline
     *  <ESC>[4m    # straight underline (for backwards compat)
     *  <ESC>[24m   # no underline (for backwards compat)
     * </pre>
     * <p>
     * We currently parse the variants, but map them to normal/no underlines as appropriate
     */
    public void testUnderlineVariants() {
        for (String suffix : List.of("", ":1", ":2", ":3", ":4", ":5")) {
            for (String stop : List.of("24", "4:0")) {
                withTerminalSized(3, 3);
                enterString("\033[4" + suffix + "m").assertLinesAre("   ", "   ", "   ");
                assertEquals(TextStyle.CHARACTER_ATTRIBUTE_UNDERLINE, mTerminal.mEffect);
                enterString("\033[4;1m").assertLinesAre("   ", "   ", "   ");
                assertEquals(TextStyle.CHARACTER_ATTRIBUTE_BOLD | TextStyle.CHARACTER_ATTRIBUTE_UNDERLINE, mTerminal.mEffect);
                enterString("\033[" + stop + "m").assertLinesAre("   ", "   ", "   ");
                assertEquals(TextStyle.CHARACTER_ATTRIBUTE_BOLD, mTerminal.mEffect);
            }
        }
    }

    public void testManyParameters() {
        StringBuilder b = new StringBuilder("\033[");
        for (int i = 0; i < 30; i++) {
            b.append("0;");
        }
        b.append("4:2");
        // This clearing of underline should be ignored as the parameters pass the threshold for too many parameters:
        b.append("4:0m");
        withTerminalSized(3, 3)
            .enterString(b.toString())
            .assertLinesAre("   ", "   ", "   ");
        assertEquals(TextStyle.CHARACTER_ATTRIBUTE_UNDERLINE, mTerminal.mEffect);
    }

}
