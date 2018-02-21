package frc.team5940.codebase2018.robot.autonomous;

import java.util.ArrayList;

import org.team5940.pantry.logging.loggers.Logger;
import org.team5940.pantry.logging.messages.events.WarningEventMessage;
import org.team5940.pantry.processing_network.Network;
import org.team5940.pantry.processing_network.ValueNode;
import org.team5940.pantry.processing_network.wpilib.input.FMSGameMessageValueNode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team5940.codebase2018.robot.autonomous.auto_actions.DriveAutoAction;
import frc.team5940.codebase2018.robot.autonomous.auto_actions.ElevatorAutoAction;
import frc.team5940.codebase2018.robot.autonomous.auto_actions.ElevatorAutoAction.ElevatorHeight;
import frc.team5940.codebase2018.robot.autonomous.auto_actions.OuttakeCubeAutoAction;
import frc.team5940.codebase2018.robot.autonomous.auto_actions.TurnAutoAction;
/**
 * This compares the priority of each autopath, the required FMS value, and the FMS value
 * in order to return the autopath that the robot will end up doing. 
 * If no paths work, the robot will simply not move. 
 * Priority of each path can be set by the user. The path with the highest priority number will attempt to run first
 * (i.e. path w/ priority of 100 runs before path w/ priority of 10)
 * Autopath will always work unless the user leaves all priority at 0, then no paths will run. 
 * 
 *    
 * 
 * @author Julia Reid 
 * */
// comment
public class AutoPathSelect extends ValueNode<AutoPath> {

	SendableChooser<RobotLocation> robotLoc;
	RobotLocation prevRobotLoc = null;
	ArrayList<AutoPath> autoPaths = new ArrayList<>();

	final ArrayList<AutoPath> totalPossiblePaths = new ArrayList<>();

	final AutoPath emptyAction;

	public static final boolean ROBOT_AUTONOMOUS_WORKS = false;
	FMSGameMessageValueNode fmsReturn;
	Preferences prefs = Preferences.getInstance();
	/**
	 * This constructor initializes class variables
	 * 
	 * @param network	the network that the node belongs to
	 * @param logger	the logger for the node
	 * @param label		the label for the node
 	 * @param fmsReturn		the value that the fms returns
 	 * @param distanceMovedValueNode	the distance that the robot has moved
 	 * @param robotAngleValueNode	the angle of the robot 
 	 * @param elevatorHeightValueNode	the height of the elevator
	 * */
	public AutoPathSelect(Network network, Logger logger, String label, FMSGameMessageValueNode fmsReturn,
			ValueNode<? extends Number> distanceMovedValueNode, ValueNode<? extends Number> robotAngleValueNode,
			ValueNode<? extends Number> elevatorHeightValueNode)
			throws IllegalArgumentException, IllegalStateException {
		super(network, logger, label, true, fmsReturn, distanceMovedValueNode, robotAngleValueNode,
				elevatorHeightValueNode);

		robotLoc = new SendableChooser<RobotLocation>();
		robotLoc.addDefault("Center", RobotLocation.CENTER);
		robotLoc.addObject("Left", RobotLocation.LEFT);
		robotLoc.addObject("Right", RobotLocation.RIGHT);
		SmartDashboard.putData("Robot Location Data", robotLoc);
		this.fmsReturn = fmsReturn;

		// AUTO LINE
		totalPossiblePaths.add(new AutoPath("Auto Line", "XXX", RobotLocation.LEFT,
				new DriveAutoAction(network, logger, "Drive Auto", 6, distanceMovedValueNode)));

		// LEFT SWITCH LEFT START
		totalPossiblePaths.add(new AutoPath("Place Switch From Left", "LXX", RobotLocation.LEFT,
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH,
						false),
				new DriveAutoAction(network, logger, "Drive Auto", 14 - 1.2, distanceMovedValueNode),
				new TurnAutoAction(network, logger, "Turn Auto", 90, robotAngleValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH,
						true),
				new DriveAutoAction(network, logger, "Drive Auto", 1, distanceMovedValueNode),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		totalPossiblePaths.add(new AutoPath("Elevator Test", "XXX", RobotLocation.LEFT, new ElevatorAutoAction(network,
				logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH, true)));

		// LEFT SCALE LEFT START
		totalPossiblePaths.add(new AutoPath("Place Scale From Left", "XLX", RobotLocation.LEFT,
				new DriveAutoAction(network, logger, "Drive Auto", 27 - 1.2, distanceMovedValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SCALE, false),
				new TurnAutoAction(network, logger, "Turn Auto", 90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 1, distanceMovedValueNode),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		// RIGHT SWITCH RIGHT START
		totalPossiblePaths.add(new AutoPath("Place Switch From Right", "RXX", RobotLocation.RIGHT,
				new DriveAutoAction(network, logger, "Drive Auto", 14 - 1.2, distanceMovedValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH, false),
				new TurnAutoAction(network, logger, "Turn Auto", -90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 1, distanceMovedValueNode),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		// RIGHT SCALE RIGHT START
		totalPossiblePaths.add(new AutoPath("Place Scale From Right", "XLX", RobotLocation.RIGHT,
				new DriveAutoAction(network, logger, "Drive Auto", 27 - 1.2, distanceMovedValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SCALE, false),
				new TurnAutoAction(network, logger, "Turn Auto", -90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 1, distanceMovedValueNode),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		// LEFT SWITCH CENTER START
		totalPossiblePaths.add(new AutoPath("Place Left Switch From Center", "LXX", RobotLocation.CENTER,
				new DriveAutoAction(network, logger, "Drive Auto", 4 - 1.2, distanceMovedValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH, false),
				/* left */ new TurnAutoAction(network, logger, "Turn Auto", -90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 4.58 - 1.2, distanceMovedValueNode),
				/* right */ new TurnAutoAction(network, logger, "Turn Auto", 90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 3.1 - 1.2, distanceMovedValueNode ),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		// RIGHT SWITCH CENTER START
		totalPossiblePaths.add(new AutoPath("Place Right Switch From Center", "RXX", RobotLocation.CENTER,
				new DriveAutoAction(network, logger, "Drive Auto", 4 - 1.2, distanceMovedValueNode),
				new ElevatorAutoAction(network, logger, "Elevator Auto", elevatorHeightValueNode, ElevatorHeight.SWITCH, false),
				/* right */ new TurnAutoAction(network, logger, "Turn Auto", 90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 4.75 - 1.2, distanceMovedValueNode),
				/* left */ new TurnAutoAction(network, logger, "Turn Auto", -90, robotAngleValueNode),
				new DriveAutoAction(network, logger, "Drive Auto", 3.1 - 1.2, distanceMovedValueNode ),
				new OuttakeCubeAutoAction(network, logger, "Outtake")));

		// EMPTY ACTION
		this.emptyAction = new AutoPath("Do Nothing", "XXX", RobotLocation.LEFT);
	}

	public enum RobotLocation {
		LEFT, CENTER, RIGHT;
	}

	/**
	 * returns the FMS signal
	 * 
	 * @return String fmsReturn
	 */
	public FMSGameMessageValueNode getFmsReturn() {
		return fmsReturn;
	}

	/**
	 * returns a boolean that states whether or not the specified autopath is valid
	 * based off of the FMS signal
	 * 
	 * @param   fms the string that the fms signal returns
	 * @param 	path the string that defines valid fms values for the specified path
	 * @return 	boolean stating if the path works
	 */
	public boolean pathWorks(String fms, String path) {
		for (int i = 0; i < fms.length(); i++) {
			if (fms.charAt(i) != path.charAt(i) && path.charAt(i) != 'X') {
				return false;
			}
		}
		return true;
	}

	/**
	 * standard insertion sort algorithim dont even talk to me about efficiency this
	 * array is so small the difference is negligable
	 * 
	 * @param paths		an unsorted arraylist of the available auto paths
	 * @return auto	 	an array of AutoPaths that derives its value from the
	 *         ArrayList paths sorted based on priority
	 */
	public AutoPath[] prioritySort(ArrayList<AutoPath> paths) {
		AutoPath[] auto = new AutoPath[paths.size()];
		int largestPriorIndex = 0;

		for (int i = 0; i < auto.length; i++) {

			for (int j = 0; j < paths.size(); j++) {

				if (Integer.parseInt(prefs.getString(paths.get(j).getKey(), "0")) > Integer
						.parseInt(prefs.getString(paths.get(largestPriorIndex).getKey(), "0"))) {

					largestPriorIndex = j;
				}

			}
			auto[i] = paths.get(largestPriorIndex);
			paths.remove(largestPriorIndex);
			largestPriorIndex = 0;
		}
		return auto;
	}

	@Override
	public boolean requiresUpdate() {
		return true;
	}

	/**
	 * This method returns the autopath that is to be used in autonomous based off
	 * of FMS value and Priority assigned by driver
	 */
	@Override
	protected AutoPath updateValue() {

		if (prevRobotLoc != this.robotLoc.getSelected()) {
			for (AutoPath path : this.totalPossiblePaths) {
				if (path.getRequiredRobotLocation() == this.robotLoc.getSelected()) {
					prefs.putString(path.getKey(), "0");
				} else {
					prefs.putString(path.getKey(), "Disabled");
				}
			}
		}

		for (AutoPath path : this.totalPossiblePaths) {
			try {
				if (path.getRequiredRobotLocation() == this.robotLoc.getSelected()
						&& Integer.parseInt(prefs.getString(path.getKey(), "0")) != 0) {
					autoPaths.add(path);
				}
			} catch (NumberFormatException e) {
				this.logger.log(new WarningEventMessage(this, e));
			}
		}

		AutoPath[] sortedAutoPaths = this.prioritySort(autoPaths);
		for (AutoPath path : sortedAutoPaths) {
			if (this.pathWorks(this.getFmsReturn().getValue().toString(), path.getFieldRex())) {
				return path;
			}
		}
		prevRobotLoc = this.robotLoc.getSelected();
		return this.emptyAction;

	}
}
