import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.circuit.project.*;

public class CSExample extends JPanel implements ActionListener {
    private CSGraph circuit;
    private CSEngine engine;
    private int maxCycles;
    private int cycleCount;
    private JButton nextCycleButton;
    private HorizontalSegment[] hSegments;
    private VerticalSegment[] vSegments;
    private int[] values;

    public CSExample() {
        try {
            circuit = CSFileIO.readSaveFile("SevenSegmentExample");
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }

        engine = new CSEngine(circuit);
        maxCycles = engine.getLongestSequenceLength();
        cycleCount = 0;

        setLayout(null);
    }

    public void addComponents() {
        // horizontal segments
        hSegments = new HorizontalSegment[3];

        HorizontalSegment seg = new HorizontalSegment((OutputVariableNode)circuit.getNode("b"));
        seg.setSize(90, 20);
        seg.setLocation(150, 100);
        add(seg);

        HorizontalSegment seg2 = new HorizontalSegment((OutputVariableNode)circuit.getNode("c"));
        seg2.setSize(90, 20);
        seg2.setLocation(150, 220);
        add(seg2);

        HorizontalSegment seg3 = new HorizontalSegment((OutputVariableNode)circuit.getNode("f"));
        seg3.setSize(90, 20);
        seg3.setLocation(150, 340);
        add(seg3);

        hSegments[0] = seg;
        hSegments[1] = seg2;
        hSegments[2] = seg3;

        // vertical segments
        vSegments = new VerticalSegment[4];

        VerticalSegment seg4 = new VerticalSegment((OutputVariableNode)circuit.getNode("a"));
        seg4.setSize(20, 90);
        seg4.setLocation(125, 125);
        add(seg4);

        VerticalSegment seg5 = new VerticalSegment((OutputVariableNode)circuit.getNode("d"));
        seg5.setSize(20, 90);
        seg5.setLocation(245, 125);
        add(seg5);

        VerticalSegment seg6 = new VerticalSegment((OutputVariableNode)circuit.getNode("e"));
        seg6.setSize(20, 90);
        seg6.setLocation(125, 245);
        add(seg6);

        VerticalSegment seg7 = new VerticalSegment((OutputVariableNode)circuit.getNode("g"));
        seg7.setSize(20, 90);
        seg7.setLocation(245, 245);
        add(seg7);

        vSegments[0] = seg4;
        vSegments[1] = seg5;
        vSegments[2] = seg6;
        vSegments[3] = seg7;

        // next cycle button
        nextCycleButton = new JButton("next clock cycle");
        nextCycleButton.addActionListener(this);
        nextCycleButton.setSize(135, 20);
        nextCycleButton.setLocation(125, 400);
        add(nextCycleButton);

        for(String nodeName : engine.getTrackedNodeNames())
            System.out.printf("%3s", nodeName);
        System.out.println();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        values = engine.getNextCircuitState();
        for(int value : values)
            System.out.printf("%3d", value);
        System.out.println();

        for(HorizontalSegment seg : hSegments)
            seg.repaint();
        for(VerticalSegment seg : vSegments)
            seg.repaint();

        cycleCount++;
        if(cycleCount > maxCycles) {
            nextCycleButton.setVisible(false);
            engine.resetCircuit();
        }
    }

    static class HorizontalSegment extends JPanel {
        private OutputVariableNode source;

        public HorizontalSegment(OutputVariableNode source) {
            this.source = source;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(source.getValue() == 1)
                g.setColor(Color.RED);
            else
                g.setColor(Color.LIGHT_GRAY);

            g.drawRect(0, 0, 90, 20);
            g.fillRect(0, 0, 90, 20);
        }
    }

    static class VerticalSegment extends JPanel {
        private OutputVariableNode source;

        public VerticalSegment(OutputVariableNode source) {
            this.source = source;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(source.getValue() == 1)
                g.setColor(Color.RED);
            else
                g.setColor(Color.LIGHT_GRAY);

            g.drawRect(0, 0, 20, 90);
            g.fillRect(0, 0, 20, 90);
        }
    }

    

    private static void showExampleGUI() {
        JFrame frame = new JFrame("Seven Segment Display Problem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);

        CSExample panel = new CSExample();
        panel.addComponents();
        frame.add(panel);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                showExampleGUI();
            }
        });
    }
}