package ee;
import robocode.*;
import robocode.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EverlastEngineeringTheSaboteur - a robot by (your name here)
 */
public class TheSaboteur extends AdvancedRobot
{
	// private 
	/**
	 * run: EverlastEngineering: TheSaboteur
	 */
//	private boolean activeRadar = true;
	private double distanceToTarget = 0;
	private double bearingToTarget = 0;
	private double moveDirection = 1;
	private AveragedArray bearingDelta;
	
	public void run() {
		// Initialization of the robot should be put here
		Random rand = new Random(); 
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 //setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// face direction for testing
//		this.setTurnLeft(this.getHeading()+90);
		
		bearingDelta = new AveragedArray(5);

		// Robot main loop
		int counter = 0;
		while(true) {
			if (this.getVelocity() == 0) {
				moveDirection = moveDirection * -1;
			}
			
//			out.printf("\n DistanceToTarget: %f",distanceToTarget);
//			out.printf("\n BearingToTarget: %f",bearingToTarget);
			if (getRadarTurnRemaining() == 0) {
//				go back to a 360 scan for more targets
				setTurnRadarRight(Double.POSITIVE_INFINITY);
			}
			counter ++;
			
//			if (distanceToTarget > 200) // kamakazeeeee! If they're far away, then weave 
//			{
//				double missBy = 0; // setup for a weave pattern
//				if (counter < 20) { 
//					//weave left
//					missBy = -40;
//				}
//				else if (counter < 40){
//					//weave right
//					missBy = 40;
//				}
//				else {
//					counter = 0;
//				}
//				
//				
//				setTurnRight(bearingToTarget+missBy);
//				setAhead(20);
//			}
//			else if (distanceToTarget < 200 && distanceToTarget > 120){
//				setTurnRight(bearingToTarget+75);
//				setAhead(50*moveDirection);
//			}
//			else {
//				setTurnRight(bearingToTarget);
//				setBack(50*moveDirection);
//			}
			scan();
//			setTurnRight(0);
//			setAhead(0);
			execute();
		}
	}

	public void target() {

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	private double previous = 0;
	private double previousBearingToTarget = 0;
	private double previousDistance = 0;
	private String target = "";
	
	public void onScannedRobot(ScannedRobotEvent e) {
		final double bulletStrength = 1;

		double gunBearing = getGunHeading();
		double heading = getHeading();
		double radarHeading = getRadarHeading();
				
		double distanceToTarget = e.getDistance(); //relative to my robot //d
		double bearingToTarget = e.getBearing(); //relative to my robots head
		double absoluteTargetBearing = heading+bearingToTarget; //a
		double targetVelocity = e.getVelocity(); //max 8 //s
		double targetHeading = e.getHeading(); //north 0 //b

		
//		double velocityToTarget = distanceToTarget - previousDistance;
//		double bearingDeltaToTarget = (heading+bearingToTarget)-previousBearingToTarget;
		double bulletSpeed = Rules.getBulletSpeed(bulletStrength);
//		double mySpeed = this.getVelocity();
		
//		Given two sides a, b and angle C. Find the third side of the triangle using law of cosines.
//		Input : a = 5, b = 8, C = 49 
//		Output : 6.04339
//		c = sqrt(a^2 + b^2 - 2*a*b*cos(C))
//		calculate the distance the bullet has to travel (side c)
		double a = distanceToTarget;
		double bestGuess = distanceToTarget;
		double B = 0;
		for (int i=0;i<5;i++) {
			double b = bestGuess/bulletSpeed*targetVelocity;
			double C = 180 - (360 - absoluteTargetBearing) - targetHeading;
	//		c2=a2+b2−2abcosγ
			double c = Math.sqrt(Math.pow(a,2) + Math.pow(b,2) - (2*a*b*Math.cos(C*Math.PI/180)));
			// next find the B angle
			//cos B = (a2 + c2 − b2)/2ac
	
			B = Math.acos((Math.pow(a, 2) + Math.pow(c,2) - Math.pow(b,2))/(2*a*c))*180/Math.PI;  
			bestGuess = c;
		}
		
		//calculate target position
//		double absoluteBearing = heading+bearingToTarget-90;
//		if (absoluteBearing < 0) absoluteBearing += 360; // convert to 0 degrees east
//		double absoluteBearingRadians = (absoluteBearing)*Math.PI/180;
//		double myX = getX();
//		double myY = this.getBattleFieldHeight()-getY(); // getY returns 0,0 as the BOTTOM LEFT FFS LOL
//		double xx = myX + distanceToTarget * Math.cos(absoluteBearingRadians);
//		double yy = myY + distanceToTarget * Math.sin(absoluteBearingRadians);
		
//		out.printf("\n target position: %f %f", xx, yy);
		
		double radarResetTo = 1.5 * Utils.normalRelativeAngleDegrees(heading + bearingToTarget - radarHeading);
		setTurnRadarRight(radarResetTo);
		 

		double relational_difference = distanceToTarget / 60; 
		
//		out.printf("\n distanceToTarget: %f",relational_difference);
//		out.printf(" cc: %f",cc);
//		double distance_offset = ((cc+previous)/2) * relational_difference;
//		out.printf(" distance_offset: %f",distance_offset);
//		out.printf(" unknown: %f",this.getGunTurnRemaining());
		double turnGun = Utils.normalRelativeAngleDegrees(bearingToTarget-gunBearing+heading+B);

		
		//		double delta = bearingDelta.average();
//		this.setTurnGunRight(Math.abs(turnGun)>5?turnGun:turnGun+(delta*3));
		this.setTurnGunRight(turnGun);
		
		//if we're within 1.5 degrees of the target's bearing, shoot
//		if (Math.abs(turnGun) < 1.5)
		{ 
			this.setFire(bulletStrength);
		}
		
		
		
//		previous = cc;
		previousDistance = distanceToTarget;
		previousBearingToTarget = (heading+bearingToTarget);
		target = e.getName();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
//		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//turnRight(120);
//		double a = Utils.normalRelativeAngleDegrees(e.getBearing());
//		turnLeft(a);
//		setAhead(200);
//		execute();
	}	
	
	public class AveragedArray {
		private ArrayList<Double> arrayList = new ArrayList<Double>();
		private int maxSampleSize;
		public AveragedArray(int _maxSampleSize) {
			maxSampleSize = _maxSampleSize; 
		}
		public void add(double d) {
			if (arrayList.size() == maxSampleSize) {
				arrayList.remove(0);
			}
			arrayList.add(d);
		}
		public double average() {
			double _average;
			double _total = 0;
			for (int i = 0; i<arrayList.size(); i++)
			{
				_total += arrayList.get(i);
			}
			_average = _total / arrayList.size();
			return _average;
		}
		public int getMaxSampleSize() {
			return maxSampleSize;
		}
	}
}
