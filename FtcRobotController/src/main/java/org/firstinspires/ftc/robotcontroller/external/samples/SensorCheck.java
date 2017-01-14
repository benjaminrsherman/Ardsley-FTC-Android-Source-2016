package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by benjamin on 1/3/17.
 */

// A quick and dirty class designed to check if all sensors are working as needed
@Autonomous(name = "Sensor Check", group = "Hardware Checks")
public class SensorCheck extends OpMode {

    ColorSensor csr;
    ColorSensor csl;

    @Override
    public void init() {
        csr = (ColorSensor)hardwareMap.get("right");
        csl = (ColorSensor)hardwareMap.get("left");

        csr.enableLed(true);
        csl.enableLed(true);
    }

    @Override
    public void loop() {
        telemetry.addData("Right Blue", csr.blue());
        telemetry.addData("Right Red", csr.red());

        telemetry.addLine();

        telemetry.addData("Left Blue", csl.blue());
        telemetry.addData("Left Red", csl.red());

        telemetry.addLine();

        telemetry.addData("Right Location", System.identityHashCode(csr));
        telemetry.addData("Left Location", System.identityHashCode(csl));

        telemetry.update();
    }
}
