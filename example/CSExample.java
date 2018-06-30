import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.circuit.project.CSGraph;
import simulator.circuit.project.CSGraph.IllegalCircuitStateException;
import simulator.circuit.project.CSEngine;
import simulator.circuit.project.CSFileIO;
import simulator.circuit.project.OutputVariableNode;

public class CSExample extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private CSGraph circuit;
    private CSEngine engine;
    private int maxCycles;
    private int cycleCount;
    private JButton nextCycleButton;
    private HorizontalSegment[] hSegments;
    private VerticalSegment[] vSegments;
    private int[] values;

    public CSExample(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
        if(fileName == null)
            fileName = "SevenSegmentExample";


        circuit = CSFileIO.readSaveFile(fileName);

        engine = new CSEngine(circuit);
        maxCycles = engine.getLongestInputSeqLength();
        cycleCount = 0;

        hSegments = new HorizontalSegment[3];
        vSegments = new VerticalSegment[4];

        setLayout(null);
    }

    public void addComponents() {
        // horizontal segments
        HorizontalSegment seg1 = new HorizontalSegment((OutputVariableNode)circuit.getNode("b"));
        seg1.setSize(90, 20);
        seg1.setLocation(150, 100);
        add(seg1);

        HorizontalSegment seg2 = new HorizontalSegment((OutputVariableNode)circuit.getNode("c"));
        seg2.setSize(90, 20);
        seg2.setLocation(150, 220);
        add(seg2);

        HorizontalSegment seg3 = new HorizontalSegment((OutputVariableNode)circuit.getNode("f"));
        seg3.setSize(90, 20);
        seg3.setLocation(150, 340);
        add(seg3);

        hSegments[0] = seg1;
        hSegments[1] = seg2;
        hSegments[2] = seg3;

        // vertical segments
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

        // print the headers
        for(String nodeName : engine.getTrackedNodeNames())
            System.out.printf("%3s", nodeName);
        System.out.println();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            values = engine.getNextCircuitState();

            if(cycleCount < maxCycles) {
                for(int value : values)
                    System.out.printf("%3d", value);
                System.out.println();
            }
    
            for(HorizontalSegment seg : hSegments)
                seg.repaint();
            for(VerticalSegment seg : vSegments)
                seg.repaint();

        } catch(IllegalCircuitStateException icse) {
            System.err.println(icse.getMessage());
        }

        cycleCount++;
        if(cycleCount > maxCycles) {
            nextCycleButton.setVisible(false);
            engine.resetCircuit();
        }
    }

    static class HorizontalSegment extends JPanel {
        private static final long serialVersionUID = 1L;
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
        private static final long serialVersionUID = 1L;
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

    private static void showExampleGUI(String fileName) {
        JFrame frame = new JFrame("Seven Segment Display Problem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);

        try {
            CSExample panel = new CSExample(fileName);
            panel.addComponents();
            frame.add(panel);

            frame.setVisible(true);
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                if(args.length > 0)
                    showExampleGUI(args[0]);
                else
                    showExampleGUI(null);
            }
        });
    }
}