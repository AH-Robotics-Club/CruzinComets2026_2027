package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanisms.Camera;
import org.firstinspires.ftc.teamcode.mechanisms.Constants;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@TeleOp(name = "Launch Align")
public class LaunchAlign extends OpMode {

    private Follower follower;

    /*
    Create two poses
    The current pose and the pose with bearing==0

    Make a path between the two poses
    Make a command to follow the path
     */
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Poses
    private final Pose startPose = poseFactory.of(0, 0, 0);

    Pose tag_to_follow;
    Path path_to_tag;

    private double distance_to_tag;

    private Command rotate(double heading) {
        Pose initial_pose = follower.pose();
        // Lines in Pedro pathing cannot have zero length
        Pose end_pose = initial_pose.withX(follower.pose().x()+ 0.001);
        Path path = line(initial_pose, end_pose).linear(initial_pose.heading(), initial_pose.heading()+heading);
        telemetry.addData("Path", path);
        return follow(follower, path);
    }

    private Command autoRoutine(double bearing) {
        return rotate(bearing);
    }


    @Override
    public void init() {
        Scheduler.reset();
        Camera.init("Webcam 1", hardwareMap, telemetry, true);

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);

        follower.update();

    }

    @Override
    public void start() {
        Scheduler.schedule(autoRoutine(180));

    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();

        Optional<AprilTagClusterDetection> blue_scoring = Camera.get("BLUE SCORING");

//        if (blue_scoring!=null){
//            schedule(autoRoutine(blue_scoring.ftcPose.bearing));
//        }
        telemetry.addData("Tags", blue_scoring);

        blue_scoring.ifPresent((tag) -> {
            telemetry.addData("Tag X", tag.ftcPose.x);
            telemetry.addData("Tag Y", tag.ftcPose.y);
            telemetry.addData("Robot pose", follower.pose());
            telemetry.addData("Range", tag.ftcPose.range);
            distance_to_tag = tag.ftcPose.range;

        });

//        tag_to_follow = new Pose(tag.ftcPose.x, tag.ftcPose.y, 0);
//        path_to_tag = line(follower.pose(), tag_to_follow);
//
//        Scheduler.schedule(follow(follower, path_to_tag));

        telemetry.update();
    }
}
