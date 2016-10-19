package org.firstinspires.ftc.robotcontroller.external;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.util.JsonWriter;
import android.widget.EditText;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 10/18/2016.
 */

@Autonomous(name = "Record Autonomous", group = "Record/PlayBack")
public class AIOpRecord extends OpMode
{
    String fileName; // Name of the file to write to, set in init

    /**
     * This ArrayList will store all of the data that our robot will record
     */
    List<AutonomousData> data;

    /**
     * Runs once OpMode is selected on the phone
     * Use to setup variables that don't rely on the state of the robot
     */
    @Override
    public void init()
    {
        Context context = FtcRobotControllerActivity.getContext(); // Because we aren't in the main activity, this is a roundabout way of getting the Context
        data = new ArrayList<>();

        // Set up input field
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Just a normal text field

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Filename")
                .setMessage("What file should the values be saved to? Don't include file extensions")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() // We only want a confirm button
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        fileName = input.getText().toString(); // Sets the filename to whatever the user entered
                    }
                })
                .show(); // Show the user the alert

        for (String s : Values.HARDWARE)
        {
            String[] add = s.split("_");
            if (add[0].equals("MOTOR"))
            {
                DcMotor motor = hardwareMap.dcMotor.get(add[1]);
                data.add(new AutonomousData(add[0], add[1], motor));
            }
            else if (add[0].equals("SERVO"))
            {
                Servo servo = hardwareMap.servo.get(add[1]);
                data.add(new AutonomousData(add[0], add[1], servo));
            }
        }
    }

    /**
     * Runs once Start is clicked
     * Use to setup variables that rely on the state of the robot and create initial conditions for motors/servos
     */
    @Override
    public void start()
    {

    }

    /**
     * Runs in a loop after Start is pressed
     */
    @Override
    public void loop()
    {
        if (time > .1) // We're recording values every .1 seconds
        {
            for (AutonomousData ad : data)
            {
                if (ad.isMotor) ad.AddValue(ad.motor.getCurrentPosition());
                else ad.AddValue(ad.servo.getPosition());
            }
        }

        // This is the same as TeleOp
        Movement();
    }

    /**
     * This method controls the movement of the robot
     */
    void Movement()
    {
        double throttle = gamepad1.left_stick_y;
        double direction = gamepad1.left_stick_x;

        double right = Range.clip((throttle - direction)*Values.MOTORSPEED, -1, 1);
        double left = Range.clip((throttle + direction)*Values.MOTORSPEED, -1, 1);

        data.get(0).motor.setPower(right); // Front-Right motor
        data.get(1).motor.setPower(left);  // Front-Left motor
        data.get(2).motor.setPower(right); // Back-Right motor
        data.get(3).motor.setPower(left);  // Back-Left motor
    }

    /**
     * Runs at the end of the OpMode
     */
    @Override
    public void stop()
    {
        try {
            WriteData(new File(Environment.getExternalStorageDirectory() + fileName + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void WriteData(File f) throws IOException
    {
        Writer out = new BufferedWriter(new FileWriter(f));
        for (AutonomousData ad : data)
        {
            out.append(Integer.toString(ad.dataValues.size()) + " ")
                    .append(ad.hardwareType + "_")
                    .append(ad.hardwareName + " ");

            for (Double d : ad.dataValues)
                out.append(Double.toString(d) + " ");
        }
        out.close();
    }
}
