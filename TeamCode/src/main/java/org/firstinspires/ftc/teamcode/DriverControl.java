package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 10/20/2016.
 */

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Driver Control", group="DriverControl")
public class DriverControl extends OpMode{

    List<Hardware> motors;
    List<Hardware> servos;

    @Override
    public void init()
    {
        // Initialize motors
        motors = new ArrayList<>();
        for (String m : Values.MOTORS)
        {
            String[] mId = m.split("_");
            motors.add(new Hardware(mId[0],
                           mId[1],
                           hardwareMap.dcMotor.get(mId[1])));
        }

        // Initialize servos
        servos = new ArrayList<>();
        for (String s : Values.MOTORS)
        {
            String[] sId = s.split("_");
            motors.add(new Hardware(sId[0],
                           sId[1],
                           hardwareMap.servo.get(sId[1])));
        }
    }

    @Override
    public void loop()
    {
        Movement();
    }

    /**
     * This method controls the movement of the robot
     */
    void Movement()
    {
        double throttle = gamepad1.left_stick_y;
        double direction = gamepad1.left_stick_x;

        double right = Range.clip((throttle - direction) * Values.MOTORSPEED, -1, 1);
        double left = Range.clip((throttle + direction)*Values.MOTORSPEED, -1, 1);

        motors.get(0).motor.setPower(right); // Front-Right motor
        motors.get(1).motor.setPower(left);  // Front-Left motor
        motors.get(2).motor.setPower(right); // Back-Right motor
        motors.get(3).motor.setPower(left);  // Back-Left motor
    }
}
