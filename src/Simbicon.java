/*
 *   This is an implementation of the planar character animation system presented in "SIMBICON: Simple Biped Locomotion Control"
 *   by Kangkang Yin, Kevin Loken and Michiel van de Panne. The purpose of this applet is to provide a simple demo to the aforementioned
 *   system.
 *
 */

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.DefaultKeyboardFocusManager;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Simbicon extends java.applet.Applet 
    implements MouseListener, MouseMotionListener, KeyListener{
   
    Bip7 bip7 = new Bip7();
    Ground gnd = new Ground();
    private float Dt = 0.00005f;
    private float DtDisp = 0.0054f;
    private float timeEllapsed = 0;
    
    //we'll use this buffered image to reduce flickering
    BufferedImage tempBuffer;
    Timer timer;

    
    //and the controller
    Controller con;
    
    
    float Md, Mdd;
    
    
    float DesVel = 0;
    
    //if this variable is set to true, the simulation will be running, otherwise it won't
    boolean simFlag = false;

    private javax.swing.JButton simButton;
    private javax.swing.JButton reset;
    private javax.swing.JPanel panel;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JLabel label;
    
   
    boolean shouldPanY = false;
	private int last_state;
	private float last_foot_location;
    
    
    public void init(){
    	
  	  float torso0 = 0;
	  float torso1 = 0;
	  float torso2 = 0;
	  float rhip0 = 0;
	  float rhip1 = 0;
	  float rhip2 = 0;
	  float rknee0 = 0;
	  float rknee1 = 0;
	  float rknee2 = 0;
	  float lhip0 = 0;  
	  float lhip1 = 0;  
	  float lhip2 = 0;
	  float lknee0 = 0;
	  float lknee1 = 0;
	  float lknee2 = 0;
	  float rankle0 = 0;
	  float rankle1 = 0;
	  float rankle2 = 0;
	  float lankle0 = 0;
	  float lankle1 = 0;
	  float lankle2 = 0;
	  float transTime = 0;
    	
    	// Load running controller params from file
    	try{
    		  // Open the file that is the first 
    		  // command line parameter
    		  FileInputStream fstream = new FileInputStream("run_params_045.txt");
    		  // Get the object of DataInputStream
    		  DataInputStream in = new DataInputStream(fstream);
    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		  String strLine;
    		  
    		  //Read File Line By Line
    		  //while ((strLine = br.readLine()) != null)   {
    		  // Print the content on the console
    		  //System.out.println (strLine);
    		  //}
    		  
    		  torso0 = new Float(br.readLine());
    		  torso1 = new Float(br.readLine());
    		  torso2 = new Float(br.readLine());
    		  rhip0 = new Float(br.readLine());
    		  rhip1 = new Float(br.readLine());
    		  rhip2 = new Float(br.readLine());
    		  rknee0 = new Float(br.readLine());
    		  rknee1 = new Float(br.readLine());
    		  rknee2 = new Float(br.readLine());
    		  lhip0 = 0;  
    		  lhip1 = 0;  
    		  lhip2 = 0;
    		  lknee0 = new Float(br.readLine());
    		  lknee1 = new Float(br.readLine());
    		  lknee2 = new Float(br.readLine());
    		  rankle0 = new Float(br.readLine());
    		  rankle1 = new Float(br.readLine());
    		  rankle2 = new Float(br.readLine());
    		  lankle0 = new Float(br.readLine());
    		  lankle1 = new Float(br.readLine());
    		  lankle2 = new Float(br.readLine());
    		  transTime = new Float(br.readLine());
    		  
    		  
    		  //Close the input stream
    		  in.close();
    		    }catch (Exception e){//Catch exception if any
    		  System.err.println("Error: " + e.getMessage());
    		  }
    	
    	//GF   	
        setSize(500, 500);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        
        //initialize the biped to a valid state:
        float[] state = {0.463f, 0.98f, 0.898f, -0.229f, 0.051f, 0.276f, -0.221f, -1.430f, -0.217f, 0.086f, 0.298f, -3.268f, -0.601f, 3.167f, 0.360f, 0.697f, 0.241f, 3.532f};
        bip7.setState(state);
        
        int delay = 1; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              runLoop();
          }
      };
        timer = new Timer(delay, taskPerformer);
        timer.start();     
          
        tempBuffer = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        
        
        initComponents();
        con = new Controller();
        con.addWalkingController();
        con.addRunningController();
        con.addCrouchWalkController();
        
        // manually set controller params
        con.state[4].transTime = transTime;
        con.state[4].setThThDThDD(0, torso0,  torso1,  torso2 );		// torso
        con.state[4].setThThDThDD(1, rhip0,   rhip1,   rhip2  );		// rhip
        con.state[4].setThThDThDD(2, rknee0,  rknee1,  rknee2 );		// rknee
        con.state[4].setThThDThDD(3, lhip0,   lhip1,   lhip2  );		// lhip
        con.state[4].setThThDThDD(4, lknee0,  lknee1,  lknee2 );		// lknee
        con.state[4].setThThDThDD(5, rankle0, rankle1, rankle2);		// rankle
        con.state[4].setThThDThDD(6, lankle0, lankle1, lankle2);		// lankle
        
        // switch legs
        con.state[6].transTime = transTime;
        con.state[6].setThThDThDD(0, torso0,  torso1,  torso2 );		// torso
        con.state[6].setThThDThDD(1, lhip0,   lhip1,   lhip2  );		// rhip
        con.state[6].setThThDThDD(2, lknee0,  lknee1,  lknee2 );		// rknee
        con.state[6].setThThDThDD(3, rhip0,   rhip1,   rhip2  );		// lhip
        con.state[6].setThThDThDD(4, rknee0,  rknee1,  rknee2 );		// lknee
        con.state[6].setThThDThDD(5, lankle0, lankle1, lankle2);		// rankle
        con.state[6].setThThDThDD(6, rankle0, rankle1, rankle2);		// lankle
        
        this.addKeyListener(this);
        this.requestFocus();
    }
    
    
    public float boundRange(float value, float min, float max) {
        if (value<min) 
            value=min;
        
        if (value>max) 
            value=max;
        return value;
    }

    //////////////////////////////////////////////////////////
    //  PROC: wPDtorq()
    //  DOES: computes requires torque to move a joint wrt world frame
    //////////////////////////////////////////////////////////
    public void wPDtorq(float torq[], int joint, float dposn, float kp, float kd, boolean world){
        float joint_posn = bip7.State[4 + joint*2];
        float joint_vel = bip7.State[4 + joint*2 + 1];
        if (world) {                   // control wrt world frame? (virtual)
            joint_posn += bip7.State[4];    // add body tilt
            joint_vel  += bip7.State[5];    // add body angular velocity
        }
        torq[joint] = kp*(dposn - joint_posn) - kd*joint_vel;
    }

    //////////////////////////////////////////////////////////
    // PROC:  jointLimit()
    // DOES:  enforces joint limits
    //////////////////////////////////////////////////////////
    public float jointLimit(float torq, int joint){
            float kpL=800;
            float kdL = 80;
            float minAngle = con.jointLimit[0][joint];
            float maxAngle = con.jointLimit[1][joint];
            float currAngle  = bip7.State[4 + joint*2];
            float currOmega = bip7.State[4 + joint*2 + 1];

            if (currAngle<minAngle)
                    torq = kpL*(minAngle - currAngle) - kdL*currOmega;
            else if (currAngle>maxAngle)
                    torq = kpL*(maxAngle - currAngle) - kdL*currOmega;
        return torq;
    }
	

    //////////////////////////////////////////////////////////
    //	PROC:	bip7WalkFsm(torq)
    //	DOES:	walking control FSM
    //////////////////////////////////////////////////////////
    public void bip7WalkFsm(float torq[]){
            int torsoIndex  = 0;
            int rhipIndex   = 1;
            int rkneeIndex  = 2;
            int lhipIndex   = 3;
            int lkneeIndex  = 4;
            int rankleIndex = 5;
            int lankleIndex = 6;
            boolean worldFrame[] = {
                    false,  // torso
                    true,   // rhip
                    false,  // rknee
                    true,   // lhip
                    false,  // lknee
                    false,  // rankle
                    false   // lankle
            };

            con.stateTime += Dt;
            ConState s = con.state[con.fsmState];

            computeMdMdd();
            for (int n=0; n<7; n++) {         // compute target angles for each joint
                    float target  = s.th[n] + Md*s.thd[n] + Mdd*s.thdd[n];         // target state + fb actions
                    target = boundRange(target, con.targetLimit[0][n], con.targetLimit[1][n]);    // limit range of target angle
                    wPDtorq(torq, n, target, con.kp[n], con.kd[n], worldFrame[n]);  // compute torques
            }

            con.advance(bip7);   	// advance FSM to next state if needed
    }

    //////////////////////////////////////////////////////////
    //	PROC:	bip7Control(torq)
    //	DOES:	calculates some primitive controlling torques
    //////////////////////////////////////////////////////////
    public void bip7Control(float torq[]){
            int body=0, stanceHip, swingHip;
            float fallAngle = 60;

            for (int n=0; n<7; n++)
                torq[n] = 0;

            // The following applies the control FSM.
            // As part of this, it computes the virtual fb torque for the
            // body, as implemented by a simple PD controller wrt to the world up vector
            if (!bip7.lostControl)  
                bip7WalkFsm(torq);

        // now change torq[body], which is virtual, 
        // to include a FEL feed-forward component

          // compute stance leg torque based upon body and swing leg
            if (con.state[con.fsmState].leftStance) {
                    stanceHip = 3;   // left hip
                    swingHip  = 1;   // right hip
            } else {
                    stanceHip = 1;   // right hip
                    swingHip  = 3;   // left hip
            }

            if (!con.state[con.fsmState].poseStance) {
                    torq[stanceHip] = -torq[body] - torq[swingHip];
            }
            torq[0] = 0;         // no external torque allowed !

            for (int n=1; n<7; n++) {  
                  torq[n] = boundRange(torq[n], con.torqLimit[0][n], con.torqLimit[1][n]);   // torq limits
                  jointLimit(torq[n],n);		                                     // apply joint limits
            }
    }    
    
    
    public void computeMdMdd(){
        float stanceFootX = bip7.getStanceFootXPos(con);
        Mdd = bip7.State[1] - DesVel;          // center-of-mass velocity error
        Md = bip7.State[0] - stanceFootX;      // center-of-mass position error
    }        
    
    public void initComponents(){
        //now we'll make a little of a GUI to let the user pause the simulation, etc.
        
        simButton = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        label.setText("Speed: ");
        
        speedSlider = new javax.swing.JSlider();        
        speedSlider.setMaximum(100);
        speedSlider.setMinimum(0);
        speedSlider.setToolTipText("Adjust the speed of the simulation by adjusting this slider.");
        
        setLayout( new BorderLayout() );
        panel.setLayout(new FlowLayout());
        add(panel, BorderLayout.NORTH);
        
        panel.add(label);
        panel.add(speedSlider);
        panel.add(simButton);
        panel.add(reset);

        speedSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                //adjust the speed of the animation:
                float slow = 0.0001f;
                float fast = 0.02f;
                float range = fast - slow;
                
                DtDisp = slow + range * speedSlider.getValue() / 100.0f;
            }
        });        

        
        simButton.setText("  Start  ");
        reset.setText("Reset");
        simButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //toggle the sim flag
                simFlag = ! simFlag;
                if (simFlag)
                    simButton.setText("Pause");
                else
                    simButton.setText("  Start  ");
            }
        });

        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSimulation();
            }
        });        
    }
    
    public void resetSimulation(){
                //toggle the sim flag
                bip7.resetBiped();

        	con.stateTime = 0;
        	con.fsmState = 0;
                con.currentGroupNumber = 0;
                con.desiredGroupNumber = 0;
                
                repaint();        
    }
    
    public void runLoop(){
        if (simFlag == false)
            return;
        timer.stop();
        //we'll run this a few times since the timer doesn't fire fast enough
        for (int i=0;i<200;i++){
            bip7.computeGroundForces(gnd);
            bip7Control(bip7.t);
            bip7.runSimulationStep(Dt);
            
            timeEllapsed += Dt;
            if (timeEllapsed>DtDisp){
                //we need to redraw the frame
                this.update(this.getGraphics());
                timeEllapsed = 0;
                
                int state = con.fsmState;
            	if (state == 6 && last_state != 6){
            		float new_foot_location = bip7.getStanceFootXPos(con);           		
            		System.out.println(new_foot_location - last_foot_location);
            		last_foot_location = new_foot_location; 
            	}   
            	last_state = con.fsmState;
            }
        }
        timer.start();
    }
    

    
    public void update(Graphics g){
        if (g == null)
            return;
        Graphics g2 = tempBuffer.getGraphics();
        g2.setColor(new Color(255, 255, 255));
        g2.fillRect(0, 0, getSize().width - 1, getSize().height - 1);
        Matrix3x3 m = Matrix3x3.getTranslationMatrix(0,-300);
        m = m.multiplyBy(Matrix3x3.getScalingMatrix((float)100));
        
        
        float panX = bip7.State[0];
        float panY = bip7.State[2];
        if (shouldPanY == false)
            panY = 0;
	 m = m.multiplyBy(Matrix3x3.getTranslationMatrix(-panX+1.5f,-panY+0.5f));
        
        
        bip7.drawBiped(g2, m);
        gnd.draw(g2,m);
        g.drawImage(tempBuffer, 0, panel.getHeight(), this);        
        panel.repaint();
    }
    
    public void paint(Graphics g){
       update(g);
       panel.repaint();
    }
    
    

    
    /*
     * Keyboard methods
     */
    public void keyReleased(KeyEvent e){

    }

    public void keyPressed(KeyEvent e){
        if (e.getKeyCode() == e.VK_LEFT){
            bip7.PushTime = 0.2f;
            bip7.PushForce = -60;
        }

        if (e.getKeyCode() == e.VK_RIGHT){
            bip7.PushTime = 0.2f;
            bip7.PushForce = 60;
        }
        
        
        if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R'){
            con.desiredGroupNumber = 1;            
        }

        if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W'){
            con.desiredGroupNumber = 0;
        }
        
        if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C'){
            con.desiredGroupNumber = 2;
        }
        
        if (e.getKeyChar() == '1'){
            gnd.getFlatGround();
            resetSimulation();
        }

        if (e.getKeyChar() == '2'){
            gnd.getComplexTerrain();
            resetSimulation();
        }
        
    }    
    
    /*
     * Keyboard methods
     */
    public void keyTyped(KeyEvent e){
    }
    
    /*
     * Mouse methods
     */
    public void mouseDragged(MouseEvent e) {
    
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        this.requestFocus();
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {}

    public void destroy() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    public String getAppletInfo() {
        return "Title: Simbicon\n"
            + "Author: Stelian Coros, Michiel van de Panne.";
    }


}
