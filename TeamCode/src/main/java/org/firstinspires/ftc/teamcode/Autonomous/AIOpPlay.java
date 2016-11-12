package org.firstinspires.ftc.teamcode.Autonomous;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.widget.EditText;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.teamcode.Values;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Benjamin on 10/19/2016.
 */
@Autonomous(name = "Play Recording", group = "Record/PlayBack")
public class AIOpPlay extends OpMode {

    List<List<Hardware>> data; // This ArrayList will store all of the data sets that our robot will run from

    List<Hardware> CURRENTDATA;

    List<Hardware> SERVOS;

    int step;
    int dataSetIndex;

    // Variables used for the beacon
    ColorSensor colorSensorRight;
    ColorSensor colorSensorLeft;

    boolean checkBeacon; // Should we check the beacon?
    int blueAlliance;

    /**
     * Runs once OpMode is selected on phone
     * Use to setup variables that don't rely on the state of the robot
     */
    @Override
    public void init()
    {
        Context context = FtcRobotControllerActivity.getContext(); // Because we aren't in the main activity, this is a roundabout way of getting the Context
        data = new ArrayList<>();
        dataSetIndex = 0;

        AlertDialog.Builder chooseAlliance = new AlertDialog.Builder(context);
        chooseAlliance.setTitle("Which alliance are we?")
                .setPositiveButton("Blue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blueAlliance = 1;
                    }
                })
                .setNegativeButton("Red", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blueAlliance = -1;
                    }
                }).show();

        // Set up input field
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Just a normal text field

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Filename")
                .setMessage("What file should the values to the first beacon be loaded from? Don't include file extensions")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() // We only want a confirm button
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String fileName1 = input.getText().toString() + ((blueAlliance==1) ? "-BLUE" : "-RED"); // Sets the filename to whatever the user entered and adds the alliance prefix

                        try {
                            ReadData(new File(Environment.getExternalStorageDirectory() + fileName1 + ".txt")); // Import data from the file
                            CURRENTDATA = data.get(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show(); // Show the user the alert

        // Set up input field
        final EditText i = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Just a normal text field

        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Set Filename")
                .setMessage("What file should the values to the second beacon be loaded from? Don't include file extensions")
                .setView(i)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() // We only want a confirm button
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName2 = i.getText().toString() + ((blueAlliance==1) ? "-BLUE" : "-RED"); // Sets the filename to whatever the user entered and adds the alliance prefix

                        try {
                            ReadData(new File(Environment.getExternalStorageDirectory() + fileName2 + ".txt")); // Import data from the file
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show(); // Show the user the alert

        // Setup for beacon
        colorSensorRight = hardwareMap.colorSensor.get(Values.COLOR_SENSOR_RIGHT);
        colorSensorLeft = hardwareMap.colorSensor.get(Values.COLOR_SENSOR_LEFT);
        for (String s : Values.SERVOS)
        {
            String[] sId = s.split("_");
            SERVOS.add(new Hardware(sId[0],
                                    sId[1],
                                    hardwareMap.servo.get(sId[1])));
        }

        step = 0;
    }

    void ReadData(File f) throws IOException
    {
        Scanner in = new Scanner(f);
        int numHardware = in.nextInt(); // Number of hardware devices to add
        in.nextLine();

        List<Hardware> dataSet = new ArrayList<>();

        for (int i=0; i<numHardware; i++)
        {
            int numData = in.nextInt();
            String[] hardwareId = in.next().split("_"); // Separate the id into hardware type and name
            Hardware ah;

            // Setup the piece of hardware
            if (hardwareId[0].equals("MOTOR"))
            {
                DcMotor motor = hardwareMap.dcMotor.get(hardwareId[1]);
                ah = new Hardware(hardwareId[0], hardwareId[1], motor);
            }
            else
            {
                Servo servo = hardwareMap.servo.get(hardwareId[1]);
                ah = new Hardware(hardwareId[0], hardwareId[1], servo);
            }

            List<Double> data = new ArrayList<>();
            for (int j = 0; j < numData; j++)
            {
                data.add(in.nextDouble()); // Add each value from the .txt file to the hardware's data array
            }

            ah.SetValues(data); // Add the values to the hardware
            in.nextLine();

            dataSet.add(ah); // Add the hardware to this data set
        }

        in.close();

        data.add(dataSet); // Add this data set to the array of all data sets
    }

    @Override
    public void loop()
    {
        if (time>.1 && !checkBeacon && step<CURRENTDATA.size())
        {
            for (Hardware ah : CURRENTDATA) ah.SetPosition(ah.dataValues.get(step++)); // Sets each hardware piece to its respective position for this time and increments step
            if (step > CURRENTDATA.size()) // Executes once all the values in CURRENTDATA have been exhausted
            {
                switch (CheckBeacon())
                {
                    case 0:
                        SERVOS.get(0).SetPosition(1);
                        break;

                    case 1:
                        SERVOS.get(1).SetPosition(1);
                        break;

                    default:
                        break;
                }
                checkBeacon = true;
                step = 0;
            }
            resetStartTime();
        }

        if (checkBeacon && time>Values.BEACON_SERVO_TIME)
        {
            SERVOS.get(0).SetPosition(0);
            SERVOS.get(1).SetPosition(0);

            if (CURRENTDATA.equals(data.get(data.size()-1))) requestOpModeStop(); // We've finished running the recording
            else CURRENTDATA = data.get(++dataSetIndex); // Sets CURRENTDATA to the next data set

            checkBeacon = false;
            resetStartTime();
        }
    }

    /**
     * Determines which button to push
     * @return Integer indicating which servo to activate: 0=right, 1=left, -1=none
     */
    int CheckBeacon()
    {
        if (colorSensorLeft.blue()*blueAlliance>colorSensorLeft.red()*blueAlliance && colorSensorRight.blue()*blueAlliance<colorSensorRight.red()*blueAlliance) // Triggers is left is our color and right is opponent color
            return 1;
        else if (colorSensorLeft.blue()*blueAlliance>colorSensorLeft.red()*blueAlliance && colorSensorRight.blue()*blueAlliance<colorSensorRight.red()*blueAlliance) // Triggers is right is our color and left is opponent color
            return 0;

        return -1;
    }
}
