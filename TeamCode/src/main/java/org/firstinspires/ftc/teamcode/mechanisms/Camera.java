package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;



/*
This mechanism will be used for detecting April Tag clusters

April Tag clusters are several April Tags next to each other in a specific location.
Look at the Hive for an example.

There are no single April Tags this year, but incomplete detections may lead to single detections.

Detecting a cluster allows for accurate localization.


 */


public class Camera {

    private static AprilTagProcessor aprilTag;
    private static VisionPortal visionPortal;

    private static boolean ourTelemetry;
    private static Telemetry subTelemetry;

    public static void init(String cameraName, HardwareMap hardwareMap, Telemetry telemetry, boolean useTelemetry) {

        aprilTag = new AprilTagProcessor.Builder()

                // The following default settings are available to un-comment and edit as needed.
                .setDrawAxes(true) // Changed in V12.0
                .setDrawTagOutline(true)
                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setDrawCubeProjection(true)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                // ... these parameters are fx, fy, cx, cy.

                .build();
// Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();


        builder.setCamera(hardwareMap.get(WebcamName.class, cameraName));


        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        ourTelemetry = useTelemetry;
        subTelemetry = telemetry;


    }

    // We don't always know if we will detect AprilTags
    // So, we use Optional
    // It allows use to return either the list or nothing
    public static List<String> detectedTags() {
        subTelemetry.addLine("Detecting");

        // We will put the integer numbers of the detected tags here
        // We don't need all of the information for every tag
        List<String> detected_tags = new ArrayList<String>();

        // Get a list of detected tags
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

        // If there is no April Tag detected, we return "nothing"


        for (AprilTagDetection detection : currentDetections) {

            // This checks whether the detection is a single April tag
            if (detection instanceof AprilTagSingleDetection) {

                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                if (singleDet.metadata != null) { // If the metadata is null, the tag is not for BioBuzz
                    detected_tags.add(String.valueOf(singleDet.id));
                }
            } else {
                // Cluster tags are two clusters side by side
                // Look under the hive for an example
                // By detecting two known tags at once, the program can calculate
                // the exact position of the robot on the field
                AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                detected_tags.add(clusterDet.metadata.name);

                // Adding the cluster name to
                if (ourTelemetry) {
                    // Tag clusters have specific names, not id's.
                    subTelemetry.addData("====== Tag Cluster", clusterDet.metadata.name);
                    // Percent tags found is to determine if anything is blocking the tags
                    subTelemetry.addData("Percent tags found", clusterDet.percentClusterFound);
                    subTelemetry.addData("Range", detection.ftcPose.range);
                    subTelemetry.addData("Bearing", detection.ftcPose.bearing);
                    subTelemetry.addData("Elevation", detection.ftcPose.elevation);
                }

            }
        }


        return detected_tags;
    }

    public static Optional<AprilTagClusterDetection> get(String name) {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster_detection = (AprilTagClusterDetection) detection;
                // first_string.equals(other_string) checks if the values of both string match
                // String == other_string checks if they are both the same variable
                // So we must use .equals for String comparisons
                if (name.equals(cluster_detection.metadata.name) || name.equals(cluster_detection.metadata.shortName)) {
                    subTelemetry.addLine("Detected tag");
                    return Optional.of(cluster_detection);
                }
            }
        }
        return Optional.empty();
    }

    // TODO make the method check for only shooting april tags
//    public static AprilTagClusterDetection get_shooting_tag() {
//        List<AprilTagDetection> detections = aprilTag.getDetections();
//        AprilTagDetection closest_april_tag = null;
//
//        for (AprilTagDetection detection : detections) {
//            if (detection instanceof AprilTagClusterDetection) {
//                AprilTagClusterDetection cluster_detection = (AprilTagClusterDetection) detection;
//                // first_string.equals(other_string) checks if the values of both string match
//                // String == other_string checks if they are both the same variable
//                // So we must use .equals for String comparisons
//                if (name.equals(cluster_detection.metadata.name)) {
//                    if (closest_april_tag==null){
//                        closest_april_tag = cluster_detection;
//                    }else{
//                        if (closest_april_tag.ftcPose.range>cluster_detection.ftcPose.range){
//                            closest_april_tag = cluster_detection;
//                        }
//                    }
//
//                }
//            }
//        }
//
//        return closest_april_tag;
//    }


    public static boolean isScorable() {

        // TODO Use this link to check the orientation of the hive https://ftc-docs.firstinspires.org/en/latest/tech_tips/tech-tips/tech-tip-apriltag-clusters/tech-tip-apriltag-clusters.html
        return false;
    }

}
