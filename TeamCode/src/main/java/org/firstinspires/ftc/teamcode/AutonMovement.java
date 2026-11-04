package org.firstinspires.ftc.teamcode;

import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.api.Paths.line;


import org.firstinspires.ftc.teamcode.mechanisms.Constants;

/*The operation mode will be categorized as an autonomous op mode with a certain name
in the driver station
 */
@Autonomous(name="Autonomous Movement")
public class AutonMovement extends OpMode {


    private Follower robot; // Pedro path controls for robot
    private final PoseFactory poseFactory = PoseFactory.degrees(); // Creates poses with heading in degrees


    Command move_forward(Follower follower, double distance){
        // A pose stores 3 values (x, y, heading (angle))
        Pose pose1 = follower.pose();

        // We can only move the direction by adding to its x and y directions
        // So we have a direction to move in (the heading) and the distance to move in that direction
        // In physics, that is a vector. It has a direction and magnitude.
        // We can represent it with a polar coordinate in math.
        // A polar coordinate is a radius and an angle. https://www.khanacademy.org/math/precalculus-tx/xecf67014514b60db:polar-equations/xecf67014514b60db:graph-polar-coordinates/e/graph-polar-coordinates

        Pose pose2 = poseFactory.of(pose1.x(), pose1.y(), pose1.heading());


        Path path = line(pose1, pose2).linear(heading_start, heading_end); // The path is a straight line from pose1 to pose2. The robot will turn from heading_start to heading_end

        return follow(follower, path);
    }


    // Init is ran once when INIT is pressed
    @Override
    public void init(){
        /* Hardware map is a dictionary of motor locations (0, 1, ...) and names (left_front_motor, ...)
        Ex. hardwareMap.get(DcMotor.class, "left_front_motor") maps "left_front_motor"
        in the code to the motor plugged into port 0

        The following line initializes robot motors with hardwareMap info
        It assigns the names in mechanisms/Constants.java::create

         */
        robot = Constants.create(hardwareMap);

        /*
        Scheduler manages the robot commands.
        For instance, it can run intake balls for 5s, move forward 5m, shoot for 3s

        Scheduler.reset() makes sure nothing from previous runs is kept
         */
        Scheduler.reset();

    }

    // Ran once after triangle pressed
    @Override
    public void start(){

    }

    // Ran repeatedly after start
    @Override
    public void loop(){

    }

}
