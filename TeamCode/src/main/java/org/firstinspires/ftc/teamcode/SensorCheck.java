package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by benjamin on 1/3/17.
 */

// A quick and dirty class designed to check if all sensors are working as needed
@Autonomous(name = "Sensor Check", group = "Hardware Checks")
public class SensorCheck extends OpMode {

    ColorSensor cs1;
    ColorSensor cs2;

    @Override
    public void init() {
        cs1 = hardwareMap.colorSensor.get("cs1");
        cs2 = hardwareMap.colorSensor.get("cs2");
    }

    @Override
    public void loop() {
        telemetry.addData("CS1 Blue", cs1.blue());
        telemetry.addData("CS1 Red", cs1.red());
        telemetry.addData("CS2 Blue", cs2.blue());
        telemetry.addData("CS2 Red", cs2.red());
    }
}
